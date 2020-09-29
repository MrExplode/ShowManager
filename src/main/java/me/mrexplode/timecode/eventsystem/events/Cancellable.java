package me.mrexplode.timecode.eventsystem.events;

public interface Cancellable {
    boolean isCancelled();

    void setCancelled(boolean cancelled);
}
