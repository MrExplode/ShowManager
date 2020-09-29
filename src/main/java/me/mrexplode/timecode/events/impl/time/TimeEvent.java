package me.mrexplode.timecode.events.impl.time;

import me.mrexplode.timecode.events.EventType;
import me.mrexplode.timecode.events.TimecodeEvent;
import me.mrexplode.timecode.util.Timecode;

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
