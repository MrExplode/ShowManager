package me.mrexplode.timecode.events;

import me.mrexplode.timecode.Timecode;

public class TimeChangeEvent extends TimecodeEvent {
    
    private Timecode time;
    
    public TimeChangeEvent(long time) {
        super(EventType.TIME_CHANGE, "TimeChangeEvent");
        this.time = new Timecode(time);
    }
    
    public TimeChangeEvent(Timecode time) {
        super(EventType.TIME_CHANGE, "TimeChangeEvent");
        this.time = time;
    }
    
    public Timecode getTime() {
        return time;
    }

}
