package me.sunstorm.showmanager;

import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.modules.ModuleManager;
import me.sunstorm.showmanager.redis.Redis;
import me.sunstorm.showmanager.redis.impl.DummyRedisImpl;
import me.sunstorm.showmanager.redis.impl.RedisImpl;
import me.sunstorm.showmanager.settings.SettingsStore;
import me.sunstorm.showmanager.settings.config.Config;
import me.sunstorm.showmanager.settings.project.Project;
import me.sunstorm.showmanager.settings.project.ProjectManager;
import me.sunstorm.showmanager.terminable.Terminables;
import me.sunstorm.showmanager.util.JsonLoader;
import org.codejargon.feather.Feather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class ShowManager {
    private static final Logger log = LoggerFactory.getLogger(ShowManager.class);

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    private final Config config;
    private final SettingsStore settingsStore;
    private final ProjectManager projectManager;
    private final EventBus eventBus;
    private final Redis redis;
    private final Worker worker;

    public ShowManager() throws IOException {
        if (!Constants.BASE_DIRECTORY.exists())
            Constants.BASE_DIRECTORY.mkdirs();
        settingsStore = new SettingsStore();
        settingsStore.load();
        config = JsonLoader.loadOrDefault("config.json", Config.class);
        eventBus = new EventBus();
        projectManager = new ProjectManager();

        if (config.getRedisConfig().isEnabled()) {
            redis = new RedisImpl(config.getRedisConfig().getCredentials());
        }
        else {
            redis = new DummyRedisImpl();
        }

        var feather = Feather.with(new DependencyGraph(this, eventBus, settingsStore, config));

        this.worker = feather.instance(Worker.class);
        new ModuleManager(feather);

        Runtime.getRuntime().addShutdownHook(new Thread(Terminables::shutdownAll));
        Project.current().save();
        worker.run();
    }
}
