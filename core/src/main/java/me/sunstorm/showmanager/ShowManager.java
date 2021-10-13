package me.sunstorm.showmanager;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.artnet.ArtNetHandler;
import me.sunstorm.showmanager.audio.AudioPlayer;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.http.HttpHandler;
import me.sunstorm.showmanager.injection.DependencyInjection;
import me.sunstorm.showmanager.ltc.LtcHandler;
import me.sunstorm.showmanager.osc.OscHandler;
import me.sunstorm.showmanager.redis.Redis;
import me.sunstorm.showmanager.redis.impl.DummyRedisImpl;
import me.sunstorm.showmanager.redis.impl.RedisImpl;
import me.sunstorm.showmanager.remote.OscRemoteControl;
import me.sunstorm.showmanager.scheduler.EventScheduler;
import me.sunstorm.showmanager.settings.SettingsStore;
import me.sunstorm.showmanager.settings.config.Config;
import me.sunstorm.showmanager.settings.project.Project;
import me.sunstorm.showmanager.settings.project.ProjectManager;
import me.sunstorm.showmanager.terminable.Terminables;
import me.sunstorm.showmanager.util.JsonLoader;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
public class ShowManager {
    @Deprecated
    @Getter private static ShowManager instance;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    private final Config config;
    private final SettingsStore settingsStore;
    private final ProjectManager projectManager;
    private final EventBus eventBus;
    private final OscHandler oscHandler;
    private final LtcHandler ltcHandler;
    private final OscRemoteControl oscRemoteControl;
    private final AudioPlayer audioPlayer;
    private final HttpHandler httpHandler;
    private final Redis redis;
    private final EventScheduler eventScheduler;
    private final Worker worker;

    @SneakyThrows({UnknownHostException.class, IOException.class})
    public ShowManager() {
        instance = this;
        DependencyInjection.registerProvider(ShowManager.class, () -> this);
        DependencyInjection.registerProvider(Worker.class, () -> null);
        if (!Constants.BASE_DIRECTORY.exists())
            Constants.BASE_DIRECTORY.mkdirs();
        settingsStore = new SettingsStore();
        DependencyInjection.registerProvider(SettingsStore.class, () -> settingsStore);
        settingsStore.load();
        config = JsonLoader.loadOrDefault("config.json", Config.class);
        eventBus = new EventBus();
        DependencyInjection.registerProvider(EventBus.class, () -> eventBus);
        //top tier sketchy
        DependencyInjection.registerProvider(OscHandler.class, () -> null);
        projectManager = new ProjectManager();
        eventScheduler = new EventScheduler();
        DependencyInjection.registerProvider(EventScheduler.class, () -> eventScheduler);
        oscHandler = new OscHandler();
        DependencyInjection.updateProvider(OscHandler.class, () -> oscHandler);
        ltcHandler = new LtcHandler(settingsStore.getMixerByName(config.getLtcConfig().getLtcOutput()), config.getFramerate());
        DependencyInjection.registerProvider(LtcHandler.class, () -> ltcHandler);
        oscRemoteControl = new OscRemoteControl();
        audioPlayer = new AudioPlayer();
        DependencyInjection.registerProvider(AudioPlayer.class, () -> audioPlayer);
        DependencyInjection.registerProvider(ArtNetHandler.class, () -> null);
        httpHandler = new HttpHandler();
        if (config.getRedisConfig().isEnabled()) {
            redis = new RedisImpl(config.getRedisConfig().getCredentials());
        }
        else {
            redis = new DummyRedisImpl();
        }
        worker = new Worker(config.getFramerate());
        DependencyInjection.updateProvider(Worker.class, () -> worker);

        Runtime.getRuntime().addShutdownHook(new Thread(Terminables::shutdownAll));
        Project.current().save();
        worker.run();
    }

    public void reload(boolean wait) {
        Terminables.shutdownAll();
        System.gc();
        if (wait) {
            try {
                for (int i = 1; i <= 5; i++) {
                    TimeUnit.SECONDS.sleep(1);
                    log.info(". ".repeat(i));
                }
            } catch (InterruptedException ignored) { }
        }
        try {
            Class<?> bClass = Class.forName("me.sunstorm.showmanager.Bootstrap");
            Field lastArgField = bClass.getDeclaredField("lastArgs");
            Method mainMethod = bClass.getDeclaredMethod("main", String[].class);
            mainMethod.invoke(null, lastArgField.get(null));
        } catch (ReflectiveOperationException e) {
            log.error("Failed to invoke Bootstrap", e);
        }
    }
}
