package me.sunstorm.showmanager.eventsystem.events.time;

import me.sunstorm.showmanager.eventsystem.events.CancellableEvent;
import me.sunstorm.showmanager.util.Timecode;


public class TimecodeStopEvent extends CancellableEvent {
    private final Timecode time;

    public TimecodeStopEvent(Timecode time) {
        this.time = time;
    }

    public Timecode getTime() {
        return time;
    }
}
