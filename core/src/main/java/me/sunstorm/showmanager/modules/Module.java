package me.sunstorm.showmanager.modules;

import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.Listener;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;
import me.sunstorm.showmanager.settings.SettingsHolder;
import me.sunstorm.showmanager.terminable.Terminable;

@Slf4j
public abstract class Module extends SettingsHolder implements InjectRecipient, Listener, Terminable {
    @Inject
    protected EventBus eventBus;

    public Module(String name) {
        super(name);
        log.info("Loading {}...", getClass().getSimpleName());
        inject();
        load();
        register();
        eventBus.register(this);
    }
}
