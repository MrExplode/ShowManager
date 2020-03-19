package me.mrexplode.timecode.schedule;

import me.mrexplode.timecode.Timecode;

public class ScheduledEvent {
    
    private ScheduleType type;
    private Timecode time;
    
    public ScheduledEvent(ScheduleType type, Timecode time) {
        this.type = type;
        this.time = time;
    }
    
    /*
     * #1
     */
    public Timecode getExecTime() {
        return time;
    }
    
    /*
     * #2
     */
    public ScheduleType getType() {
        return this.type;
    }

}
