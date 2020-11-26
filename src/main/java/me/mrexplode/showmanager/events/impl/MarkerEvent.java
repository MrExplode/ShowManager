package me.mrexplode.showmanager.events.impl;


import me.mrexplode.showmanager.events.EventType;
import me.mrexplode.showmanager.events.TimecodeEvent;

public class MarkerEvent extends TimecodeEvent {

    public MarkerEvent() {
        super(EventType.MARKER, "MarkerEvent");
    }

}
