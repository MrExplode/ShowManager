package me.sunstorm.showmanager.modules;

import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.Listener;
import me.sunstorm.showmanager.settings.SettingsHolder;
import me.sunstorm.showmanager.terminable.Terminable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public abstract class Module extends SettingsHolder implements Listener, Terminable {
    private static final Logger log = LoggerFactory.getLogger(Module.class);

    protected EventBus eventBus;

    @Inject
    public Module(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    protected void init() {
        log.info("Loading {}...", getClass().getSimpleName());
        load();
        register();
        eventBus.register(this);
    }

    @Override
    public void shutdown() throws Exception {
        // empty
    }
}
