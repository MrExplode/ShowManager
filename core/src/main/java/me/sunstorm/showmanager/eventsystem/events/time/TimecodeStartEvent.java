package me.sunstorm.showmanager.eventsystem.events.time;

import me.sunstorm.showmanager.eventsystem.events.CancellableEvent;
import me.sunstorm.showmanager.util.Timecode;

public class TimecodeStartEvent extends CancellableEvent {
    private final Timecode time;

    public TimecodeStartEvent(Timecode time) {
        this.time = time;
    }

    public Timecode getTime() {
        return time;
    }
}
