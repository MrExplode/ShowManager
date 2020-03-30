package me.mrexplode.timecode;

/**
 * A wrapper class for holding a timecode value.
 * 
 * @author <a href="https://mrexplode.github.io">MrExplode</a>
 *
 */
public class Timecode implements Comparable<Timecode> {
    
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
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + frame;
        result = prime * result + hour;
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
        if (frame != other.getFrame())
            return false;
        if (hour != other.getHour())
            return false;
        if (min != other.getMin())
            return false;
        if (sec != other.getSec())
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return (hour < 10 ? "0" + hour : hour) + ":" + (min < 10 ? "0" + min : min) + ":" + (sec < 10 ? "0" + sec : sec) + "/" + (frame < 10 ? "0" + frame : frame); 
    }


    @Override
    public int compareTo(Timecode tc) {
        if (tc == null) {
            return -1;
        }
        if (this.equals(tc)) {
            return 0;
        }
        
        if (getHour() >= tc.getHour()) {
            if (getHour() == tc.getHour()) {
                if (getMin() >= tc.getMin()) {
                    if (getMin() == tc.getMin()) {
                        if (getSec() >= tc.getSec()) {
                            if (getSec() == tc.getSec()) {
                                if (getFrame() >= tc.getFrame()) {
                                    if (getFrame() == tc.getFrame()) {
                                        //should not happen
                                        return 0;
                                    }
                                    return 1;
                                } else {
                                    return -1;
                                }
                            }
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                    return 1;
                } else {
                    return -1;
                }
            }
            return 1;
        } else {
            return -1;
        }
    }

}
