package me.mrexplode.timecode.events;

import me.mrexplode.timecode.Timecode;

public class TimeChangeEvent extends TimecodeEvent {
    
    private Timecode time;

    public TimeChangeEvent(int hour, int min, int sec, int frame) {
        super(EventType.TIME_CHANGE, "TimeChangeEvent");
        this.time = new Timecode(hour, min, sec, frame);
    }
    
    public TimeChangeEvent(Timecode time) {
        super(EventType.TIME_CHANGE, "TimeChangeEvent");
        this.time = time;
    }
    
    public Timecode getTime() {
        return time;
    }

}
