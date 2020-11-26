package me.mrexplode.showmanager.schedule;

import me.mrexplode.showmanager.util.Timecode;

public class ScheduledEvent implements Comparable<ScheduledEvent> {
    
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
    
    public void setExecTime(Timecode time) {
        this.time = time;
    }
    
    /*
     * #2
     */
    public ScheduleType getType() {
        return this.type;
    }
    
    /**
     * 
     * @return the corresponding element for the first column
     */
    public Object getFirstColumn() {
        return getExecTime();
    }
    
    /**
     * 
     * @return the corresponding element for the second column
     */
    public Object getSecondColumn() {
        return getType();
    }
    
    /**
     * 
     * @return the corresponding element for the third column
     */
    public Object getThirdColumn() {
        return null;
    }
    
    /**
     * 
     * @return the corresponding element for the fourth column
     */
    public Object getFourthColumn() {
        return null;
    }
    
    /**
     * 
     * @return the corresponding element for the fifth column
     */
    public Object getFifthColumn() {
        return null;
    }

    @Override
    public int compareTo(ScheduledEvent o) {
        if (time != null) {
            return time.compareTo(o.getExecTime());
        }
        if (o.getExecTime() == null) {
            return 0;
        }
        return 1;
    }

}
