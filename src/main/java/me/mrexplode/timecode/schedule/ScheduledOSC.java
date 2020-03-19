package me.mrexplode.timecode.schedule;

import me.mrexplode.timecode.Timecode;

public class ScheduledOSC extends ScheduledEvent {
    
    private String path;
    private OSCDataType dataType;
    private String value;

    public ScheduledOSC() {
        this(null, null, null, null);
    }
    
    public ScheduledOSC(Timecode time, String path, OSCDataType dataType, String value) {
        super(ScheduleType.OSC, time);
        this.path = path;
        this.dataType = dataType;
        this.value = value;
    }

    
    public String getPath() {
        return path;
    }

    
    public void setPath(String path) {
        this.path = path;
    }

    
    public OSCDataType getDataType() {
        return dataType;
    }

    
    public void setDataType(OSCDataType dataType) {
        this.dataType = dataType;
    }

    
    public String getValue() {
        return value;
    }

    
    public void setValue(String value) {
        this.value = value;
    }

}
