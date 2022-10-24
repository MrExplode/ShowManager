package me.sunstorm.showmanager;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.modules.artnet.ArtNetModule;
import me.sunstorm.showmanager.modules.audio.AudioModule;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.modules.http.HttpModule;
import me.sunstorm.showmanager.injection.DependencyInjection;
import me.sunstorm.showmanager.modules.ltc.LtcModule;
import me.sunstorm.showmanager.modules.osc.OscModule;
import me.sunstorm.showmanager.redis.Redis;
import me.sunstorm.showmanager.redis.impl.DummyRedisImpl;
import me.sunstorm.showmanager.redis.impl.RedisImpl;
import me.sunstorm.showmanager.modules.remote.OscRemoteModule;
import me.sunstorm.showmanager.modules.scheduler.SchedulerModule;
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
    private final OscModule oscModule;
    private final LtcModule ltcModule;
    private final OscRemoteModule oscRemoteModule;
    private final AudioModule audioModule;
    private final HttpModule httpModule;
    private final Redis redis;
    private final SchedulerModule schedulerModule;
    private final Worker worker;

    @SneakyThrows({UnknownHostException.class, IOException.class})
    public ShowManager() {
        instance = this;
        DependencyInjection.registerProvider(ShowManager.class, () -> this);
        DependencyInjection.registerProvider(Worker.class, () -> null);
        if (!Constants.BASE_DIRECTORY.exists())
            Constants.BASE_DIRECTORY.mkdirs();
        settingsStore = new SettingsStore();
        DependencyInjection.registerProvider(SettingsStore.class, this::getSettingsStore);
        settingsStore.load();
        config = JsonLoader.loadOrDefault("config.json", Config.class);
        eventBus = new EventBus();
        DependencyInjection.registerProvider(EventBus.class, this::getEventBus);
        //top tier sketchy
        DependencyInjection.registerProvider(OscModule.class, () -> null);
        projectManager = new ProjectManager();
        schedulerModule = new SchedulerModule();
        DependencyInjection.registerProvider(SchedulerModule.class, this::getSchedulerModule);
        oscModule = new OscModule();
        DependencyInjection.updateProvider(OscModule.class, this::getOscModule);
        ltcModule = new LtcModule();
        ltcModule.init();
        DependencyInjection.registerProvider(LtcModule.class, this::getLtcModule);
        oscRemoteModule = new OscRemoteModule();
        audioModule = new AudioModule();
        DependencyInjection.registerProvider(AudioModule.class, this::getAudioModule);
        DependencyInjection.registerProvider(ArtNetModule.class, () -> null);
        httpModule = new HttpModule();
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
