package me.sunstorm.showmanager;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.http.HttpHandler;
import me.sunstorm.showmanager.redis.impl.DummyRedisImpl;
import me.sunstorm.showmanager.redis.Redis;
import me.sunstorm.showmanager.redis.impl.RedisImpl;
import me.sunstorm.showmanager.remote.OscRemoteControl;
import me.sunstorm.showmanager.scheduler.EventScheduler;
import me.sunstorm.showmanager.settings.SettingsStore;
import me.sunstorm.showmanager.settings.config.Config;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.ltc.LtcHandler;
import me.sunstorm.showmanager.osc.OscHandler;
import me.sunstorm.showmanager.terminable.Terminables;
import me.sunstorm.showmanager.util.JsonLoader;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@Getter
public class ShowManager {
    @Getter private static ShowManager instance;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    private final Config config;
    private final SettingsStore settingsStore;
    private final EventBus eventBus;
    private final OscHandler oscHandler;
    private final LtcHandler ltcHandler;
    private final OscRemoteControl oscRemoteControl;
    private final HttpHandler httpHandler;
    private final Redis redis;
    private final EventScheduler eventScheduler;
    private final Worker worker;

    @SneakyThrows({UnknownHostException.class})
    public ShowManager() {
        instance = this;
        if (!Constants.BASE_DIRECTORY.exists())
            Constants.BASE_DIRECTORY.mkdirs();
        settingsStore = new SettingsStore();
        settingsStore.load();
        config = JsonLoader.loadOrDefault("config.json", Config.class);
        eventBus = new EventBus();
        oscHandler = new OscHandler(InetAddress.getByName(config.getOscDispatchConfig().getTarget()), config.getOscDispatchConfig().getPort(), config.getOscDispatchConfig().getPort() + 1);
        ltcHandler = new LtcHandler(settingsStore.getMixerByName(config.getLtcConfig().getLtcOutput()), config.getFramerate());
        oscRemoteControl = new OscRemoteControl();
        httpHandler = new HttpHandler();
        if (config.getRedisConfig().isEnabled())
            redis = new RedisImpl(config.getRedisConfig().getCredentials());
        else
            redis = new DummyRedisImpl();
        eventScheduler = new EventScheduler();
        worker = new Worker(InetAddress.getByName(config.getArtNetConfig().getArtNetInterface()), config.getFramerate());

        Runtime.getRuntime().addShutdownHook(new Thread(Terminables::shutdownAll));
        worker.run();
    }
}
