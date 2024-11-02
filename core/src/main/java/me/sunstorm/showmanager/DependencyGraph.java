package me.sunstorm.showmanager;

import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.settings.SettingsStore;
import me.sunstorm.showmanager.settings.config.Config;
import org.codejargon.feather.Feather;
import org.codejargon.feather.Provides;

import javax.inject.Named;

public class DependencyGraph {
    private final ShowManager sm;
    private final EventBus eventBus;
    private final SettingsStore settings;
    private final Config config;

    public DependencyGraph(ShowManager sm, EventBus eventBus, SettingsStore settings, Config config) {
        this.sm = sm;
        this.eventBus = eventBus;
        this.settings = settings;
        this.config = config;
    }

    @Provides
    EventBus bus() {
        return this.eventBus;
    }

    @Provides
    ShowManager sm() {
        return this.sm;
    }

    @Provides
    SettingsStore store() {
        return this.settings;
    }

    @Provides
    Config config() {
        return this.config;
    }

    @Provides
    @Named("framerate")
    int framerate() {
        return this.config.getFramerate();
    }
}
