package me.mrexplode.showmanager.eventsystem.events.time;

import me.mrexplode.showmanager.eventsystem.events.CancellableEvent;
import me.mrexplode.showmanager.util.Timecode;

public class TimecodeStopEvent extends CancellableEvent {
    public final Timecode ZERO = new Timecode(0);
}
