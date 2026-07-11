package me.sunstorm.showmanager.eventsystem.events.time;

import me.sunstorm.showmanager.eventsystem.events.Event;
import me.sunstorm.showmanager.util.Timecode;

/**
 * The cluster clock beacon: the master's show position sampled at its monotonic {@code masterTimestamp}.
 * It only disciplines the follower clock — outputs are driven by each node's local
 * {@link TimecodeChangeEvent}, not by this.
 */
public class TimecodeSyncEvent extends Event {
    private final Timecode position;
    private final long masterTimestamp;

    public TimecodeSyncEvent(Timecode position, long masterTimestamp) {
        this.position = position;
        this.masterTimestamp = masterTimestamp;
    }

    public Timecode getPosition() {
        return position;
    }

    public long getMasterTimestamp() {
        return masterTimestamp;
    }
}
