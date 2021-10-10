package me.sunstorm.showmanager.audio.marker;

import lombok.Getter;
import me.sunstorm.showmanager.scheduler.impl.ScheduledJumpEvent;
import me.sunstorm.showmanager.util.Timecode;

/*
 * Temporary workaround for easy jumps in time (and audio) until the planned audio manager is implemented
 * or not
 */
@Getter
public class Marker {
    private final String label;
    private final Timecode time;
    private final ScheduledJumpEvent wrappedEvent;

    public Marker(String label, Timecode time) {
        this.label = label;
        this.time = time;
        wrappedEvent = new ScheduledJumpEvent(Timecode.ZERO, time);
    }

    public void jump() {
        wrappedEvent.execute();
    }
}
