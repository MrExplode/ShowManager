package me.mrexplode.timecode;

/**
 * A wrapper class for holding a timecode value.
 * 
 * @author <a href="https://mrexplode.github.io">MrExplode</a>
 *
 */
public class Timecode {
    
    private int hour;
    private int min;
    private int sec;
    private int frame;
    
    public Timecode(int hour, int min, int sec, int frame) {
        this.hour = hour;
        this.min = min;
        this.sec = sec;
        this.frame = frame;
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
    

}
