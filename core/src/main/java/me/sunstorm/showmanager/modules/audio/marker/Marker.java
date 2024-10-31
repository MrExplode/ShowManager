package me.sunstorm.showmanager.modules.audio.marker;

import me.sunstorm.showmanager.modules.scheduler.impl.ScheduledJumpEvent;
import me.sunstorm.showmanager.util.Timecode;

/*
 * Temporary workaround for easy jumps in time (and audio) until the planned audio manager is implemented
 * or not
 */
public class Marker {
    private final String label;
    private final Timecode time;
    private transient ScheduledJumpEvent wrappedEvent;

    public Marker(String label, Timecode time) {
        this.label = label;
        this.time = time;
    }

    public void jump() {
        //starting to hate gson more and more.
        if (wrappedEvent == null)
            wrappedEvent = new ScheduledJumpEvent(Timecode.ZERO, time);
        wrappedEvent.execute();
    }

    // generated

    public String getLabel() {
        return label;
    }

    public Timecode getTime() {
        return time;
    }

    public ScheduledJumpEvent getWrappedEvent() {
        return wrappedEvent;
    }
}
