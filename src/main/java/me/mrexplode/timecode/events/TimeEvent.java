package me.mrexplode.timecode.events;

import me.mrexplode.timecode.Timecode;

public class TimeEvent extends TimecodeEvent {
    
    private Timecode value;

    public TimeEvent(EventType type) {
        super(type, "TimeEvent");
    }
    
    public void setAdditionalValue(Timecode time) {
        this.value = time;
    }
    
    public Timecode getValue() {
        return value;
    }

}
