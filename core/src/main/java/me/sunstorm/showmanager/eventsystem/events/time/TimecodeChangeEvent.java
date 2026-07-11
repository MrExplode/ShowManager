package me.sunstorm.showmanager.eventsystem.events.time;

import me.sunstorm.showmanager.eventsystem.events.Event;
import me.sunstorm.showmanager.util.Timecode;

public class TimecodeChangeEvent extends Event {
    private final Timecode time;
    // master monotonic nanos when this position was sampled; 0 for non-clock sources (UI, etc.)
    private final long masterTimestamp;

    public TimecodeChangeEvent(Timecode time) {
        this(time, 0L);
    }

    public TimecodeChangeEvent(Timecode time, long masterTimestamp) {
        this.time = time;
        this.masterTimestamp = masterTimestamp;
    }

    public Timecode getTime() {
        return time;
    }

    public long getMasterTimestamp() {
        return masterTimestamp;
    }
}
