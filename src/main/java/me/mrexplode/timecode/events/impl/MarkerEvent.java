package me.mrexplode.timecode.events.impl;


import me.mrexplode.timecode.events.EventType;
import me.mrexplode.timecode.events.TimecodeEvent;

public class MarkerEvent extends TimecodeEvent {

    public MarkerEvent() {
        super(EventType.MARKER, "MarkerEvent");
    }

}
