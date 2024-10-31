package me.sunstorm.showmanager.eventsystem.events.time;

import me.sunstorm.showmanager.eventsystem.events.Event;
import me.sunstorm.showmanager.util.Timecode;

public class TimecodeChangeEvent extends Event {
    private final Timecode time;

    public TimecodeChangeEvent(Timecode time) {
        this.time = time;
    }

    public Timecode getTime() {
        return time;
    }
}
