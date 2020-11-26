package me.mrexplode.showmanager.events.impl.time;

import me.mrexplode.showmanager.events.EventType;
import me.mrexplode.showmanager.events.TimecodeEvent;
import me.mrexplode.showmanager.util.Timecode;

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
