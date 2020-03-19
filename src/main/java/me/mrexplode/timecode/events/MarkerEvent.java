package me.mrexplode.timecode.events;


public class MarkerEvent extends TimecodeEvent {

    public MarkerEvent() {
        super(EventType.MARKER, "MarkerEvent");
    }

}
