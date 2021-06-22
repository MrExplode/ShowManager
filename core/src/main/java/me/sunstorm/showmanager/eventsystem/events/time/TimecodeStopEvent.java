package me.sunstorm.showmanager.eventsystem.events.time;

import me.sunstorm.showmanager.eventsystem.events.CancellableEvent;
import me.sunstorm.showmanager.util.Timecode;

public class TimecodeStopEvent extends CancellableEvent {
    public final Timecode ZERO = new Timecode(0);
}
