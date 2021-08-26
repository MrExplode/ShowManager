package me.sunstorm.showmanager;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.redis.DummyRedisImpl;
import me.sunstorm.showmanager.redis.Redis;
import me.sunstorm.showmanager.redis.RedisImpl;
import me.sunstorm.showmanager.remote.OscRemoteControl;
import me.sunstorm.showmanager.settings.SettingsStore;
import me.sunstorm.showmanager.settings.config.Config;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.ltc.LtcHandler;
import me.sunstorm.showmanager.osc.OscHandler;
import me.sunstorm.showmanager.terminable.Terminables;

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
    private final Redis redis;
    private Worker worker;

    @SneakyThrows({UnknownHostException.class})
    public ShowManager() {
        instance = this;
        settingsStore = new SettingsStore();
        settingsStore.load();
        config = null;
        eventBus = new EventBus();
        oscHandler = new OscHandler(InetAddress.getByName(config.getOscDispatchConfig().getTarget()), config.getOscDispatchConfig().getPort(), config.getOscDispatchConfig().getPort() + 1);
        ltcHandler = new LtcHandler(settingsStore.getMixerByName(config.getLtcConfig().getLtcOutput()), 25);
        oscRemoteControl = new OscRemoteControl();
        if (config.getRedisConfig().isEnabled())
            redis = new RedisImpl(config.getRedisConfig().getCredentials());
        else
            redis = new DummyRedisImpl();

        Runtime.getRuntime().addShutdownHook(new Thread(Terminables::shutdownAll));
    }
}
