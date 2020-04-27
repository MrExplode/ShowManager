package me.mrexplode.timecode;

/**
 * A wrapper class for holding a timecode value.
 * 
 * @author <a href="https://mrexplode.github.io">MrExplode</a>
 *
 */
public class Timecode implements Comparable<Timecode> {
    
    private long millisecLength;
    private int framerate;
    
    private int hour;
    private int min;
    private int sec;
    private int frame;
    
    /**
     * Creates an unsynced instance, from millisecond length, the H:MM:SS:f value not calculated
     * @param lengthInMillis
     */
    public Timecode(long lengthInMillis) {
        this.millisecLength = lengthInMillis;
    }
    
    /**
     * Creates a Timecode instance, with synced values
     * Synced: holds both millisecond length, and calculated H:M:S:f value
     * @param lengthInMillis
     * @param framerate
     */
    public Timecode(long lengthInMillis, int framerate) {
        this.millisecLength = lengthInMillis;
        this.framerate = framerate;
        syncTo(this.framerate);
    }
    
    public Timecode(int hour, int min, int sec, int frame, int framerate) {
        this.hour = hour;
        this.min = min;
        this.sec = sec;
        this.frame = frame;
        this.framerate = framerate;
        syncFrom(this.framerate);
    }
    
    public Timecode syncedInstance(int framerate) {
        return new Timecode(this.millisecLength, framerate);
    }

    private void syncTo(int framerate) {
        long value = millisecLength;
        this.hour = (int) (value / 60 / 60 / 1000);
        value = value - (hour * 60 * 60 * 1000);
        this.min = (int) (value / 60 / 1000);
        value = value - (min * 60 * 1000);
        this.sec = (int) (value / 1000);
        value = value - (sec * 1000);
        this.frame = (int) (value / (1000 / framerate));
    }
    
    private void syncFrom(int framerate) {
        int hourM = this.hour * 60 * 60 * 1000;
        int minM = this.min * 60 * 1000;
        int secM = this.sec * 1000;
        int frameM = this.frame * (1000 / framerate);
        this.millisecLength = hourM + minM + secM + frameM;
    }
    
    public int getHour() {
        return hour;
    }

    
    public void setHour(int hour) {
        this.hour = hour;
    }

    
    public int getMin() {
        return min;
    }

    
    public void setMin(int min) {
        this.min = min;
    }

    
    public int getSec() {
        return sec;
    }

    
    public void setSec(int sec) {
        this.sec = sec;
    }

    
    public int getFrame() {
        return frame;
    }

    
    public void setFrame(int frame) {
        this.frame = frame;
    }
    
    public Timecode abs() {
        return new Timecode(Math.abs(millisecLength));
    }
    
    public long millis() {
        return millisecLength;
    }
    
    public Timecode subtract(Timecode t) {
        return new Timecode(this.millisecLength - t.millisecLength);
    }
    
    public Timecode add(Timecode t) {
        long time = this.millisecLength + t.millisecLength;
        return new Timecode(time);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + frame;
        result = prime * result + hour;
        result = prime * result + (int) (millisecLength ^ (millisecLength >>> 32));
        result = prime * result + min;
        result = prime * result + sec;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Timecode other = (Timecode) obj;
        if (frame != other.frame)
            return false;
        if (hour != other.hour)
            return false;
        //if (millisecLength != other.millisecLength)
        //    return false;
        if (min != other.min)
            return false;
        if (sec != other.sec)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return (hour < 10 ? "0" + hour : hour) + ":" + (min < 10 ? "0" + min : min) + ":" + (sec < 10 ? "0" + sec : sec) + "/" + (frame < 10 ? "0" + frame : frame); 
    }
    
    public String guiFormatted() {
        return (hour < 10 ? "0" + hour : hour) + " : " + (min < 10 ? "0" + min : min) + " : " + (sec < 10 ? "0" + sec : sec) + " / " + (frame < 10 ? "0" + frame : frame); 
    }


    @Override
    public int compareTo(Timecode tc) {
        if (tc == null) {
            return -1;
        }
        if (this.equals(tc)) {
            return 0;
        }
        if (this.millisecLength > tc.millisecLength) {
            return 1;
        } else {
            return -1;
        }
    }

}
