package me.sunstorm.showmanager.modules;

import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.Listener;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;
import me.sunstorm.showmanager.settings.SettingsHolder;
import me.sunstorm.showmanager.terminable.Terminable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Module extends SettingsHolder implements InjectRecipient, Listener, Terminable {
    private static final Logger log = LoggerFactory.getLogger(Module.class);

    @Inject
    protected EventBus eventBus;

    public Module(String name) {
        super(name);
    }

    protected void init() {
        log.info("Loading {}...", getClass().getSimpleName());
        inject();
        load();
        register();
        eventBus.register(this);
    }
}
