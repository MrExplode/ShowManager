package me.sunstorm.showmanager;

import me.sunstorm.showmanager.cluster.ClockSync;
import me.sunstorm.showmanager.cluster.ClusterNodes;
import me.sunstorm.showmanager.cluster.ClusterService;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.modules.ModuleManager;
import me.sunstorm.showmanager.settings.SettingsStore;
import me.sunstorm.showmanager.settings.config.Config;
import me.sunstorm.showmanager.settings.project.Project;
import me.sunstorm.showmanager.settings.project.ProjectManager;
import me.sunstorm.showmanager.terminable.Terminables;
import me.sunstorm.showmanager.util.Framerate;
import me.sunstorm.showmanager.util.JsonLoader;
import org.codejargon.feather.Feather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class ShowManager {
    private static final Logger log = LoggerFactory.getLogger(ShowManager.class);
    public static Feather FEATHER;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    private final Config config;
    private final SettingsStore settingsStore;
    private final ProjectManager projectManager;
    private final EventBus eventBus;
    private final ClusterService clusterService;
    private final Worker worker;

    public ShowManager() throws IOException {
        if (!Constants.BASE_DIRECTORY.exists())
            Constants.BASE_DIRECTORY.mkdirs();
        settingsStore = new SettingsStore();
        settingsStore.load();
        config = JsonLoader.loadOrDefault("config.json", Config.class);
        Framerate.set(config.getFramerate());
        eventBus = new EventBus();
        projectManager = new ProjectManager();
        clusterService = new ClusterService(config.getClusterConfig());

        FEATHER = Feather.with(new DependencyGraph(this, eventBus, settingsStore, config, clusterService));

        this.worker = FEATHER.instance(Worker.class);
        new ModuleManager(FEATHER);

        Runtime.getRuntime().addShutdownHook(new Thread(Terminables::shutdownAll));
        eventBus.setCluster(clusterService);
        clusterService.setMessageListener(eventBus::onClusterMessage);
        ClockSync clockSync = FEATHER.instance(ClockSync.class);
        ClusterNodes clusterNodes = FEATHER.instance(ClusterNodes.class);
        clusterService.connect();
        clockSync.start();
        clusterNodes.start();
        Project.current().save();
        worker.run();
    }
}
