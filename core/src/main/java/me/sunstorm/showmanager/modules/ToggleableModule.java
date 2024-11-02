package me.sunstorm.showmanager.modules;

import me.sunstorm.showmanager.eventsystem.EventBus;

import javax.inject.Inject;

public abstract class ToggleableModule extends Module {
    private boolean enabled = false;

    @Inject
    public ToggleableModule(EventBus eventBus) {
        super(eventBus);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
