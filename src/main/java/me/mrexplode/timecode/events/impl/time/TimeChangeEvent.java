package me.mrexplode.timecode.events.impl.time;

import lombok.Getter;
import me.mrexplode.timecode.events.EventType;
import me.mrexplode.timecode.events.TimecodeEvent;
import me.mrexplode.timecode.util.Timecode;

@Getter
public class TimeChangeEvent extends TimecodeEvent {
    
    private final Timecode time;
    
    public TimeChangeEvent(long time) {
        super(EventType.TIME_CHANGE, "TimeChangeEvent");
        this.time = new Timecode(time);
    }
    
    public TimeChangeEvent(Timecode time) {
        super(EventType.TIME_CHANGE, "TimeChangeEvent");
        this.time = time;
    }

}
