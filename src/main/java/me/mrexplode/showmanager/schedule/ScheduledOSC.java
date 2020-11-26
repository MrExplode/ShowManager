package me.mrexplode.showmanager.schedule;

import me.mrexplode.showmanager.util.Timecode;

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

    /**
     * 
     * @return true if every field is filled with data.
     */
    public boolean isReady() {
        return path != null && !path.equals("") && dataType != null && value != null && this.getExecTime() != null;
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
    
    /*
     * Cell implementations
     */

    @Override
    public Object getFirstColumn() {
        return this.getExecTime();
    }
    
    @Override
    public Object getSecondColumn() {
        return this.getType();
    }
    
    @Override
    public Object getThirdColumn() {
        return getPath();
    }
    
    @Override
    public Object getFourthColumn() {
        return getDataType();
    }
    
    @Override
    public Object getFifthColumn() {
        return getValue();
    }
}
