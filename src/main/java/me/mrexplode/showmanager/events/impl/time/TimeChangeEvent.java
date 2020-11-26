package me.mrexplode.showmanager.events.impl.time;

import lombok.Getter;
import me.mrexplode.showmanager.events.EventType;
import me.mrexplode.showmanager.events.TimecodeEvent;
import me.mrexplode.showmanager.util.Timecode;

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
