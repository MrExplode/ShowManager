package me.sunstorm.showmanager.eventsystem.events;

public class CancellableEvent extends Event implements Cancellable {
    private boolean cancelled;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
