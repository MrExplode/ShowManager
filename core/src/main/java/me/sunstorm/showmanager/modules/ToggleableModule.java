package me.sunstorm.showmanager.modules;

public abstract class ToggleableModule extends Module {
    private boolean enabled = false;

    public ToggleableModule(String name) {
        super(name);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
