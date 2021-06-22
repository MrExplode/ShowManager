package me.sunstorm.showmanager.eventsystem.events;

public interface Cancellable {
    boolean isCancelled();

    void setCancelled(boolean cancelled);
}
