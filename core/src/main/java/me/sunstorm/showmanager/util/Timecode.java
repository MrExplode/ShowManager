package me.sunstorm.showmanager.util;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

/**
 * A wrapper class for holding a timecode value.
 * 
 * Inner calculations are made with milliseconds value.
 * Calculation methods return UNSYNCED values!
 * 
 * @author <a href="https://mrexplode.github.io">MrExplode</a>
 *
 */
@Setter
@ToString
@EqualsAndHashCode
public class Timecode implements Comparable<Timecode> {

    @Setter(AccessLevel.NONE) private long millisecLength;
    @Setter(AccessLevel.NONE) private int framerate;
    
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

    public void set(long lengthInMillis, int framerate) {
        this.millisecLength = lengthInMillis;
        this.framerate = framerate;
        syncTo(this.framerate);
    }
    
    /**
     * Creates a synced instance from the parameter, by calculating the H:MM:SS:f value from millisecond value
     * @param framerate
     * @return synced instance
     */
    public Timecode syncedInstance(int framerate) {
        return new Timecode(this.millisecLength, framerate);
    }

    private void syncTo(int framerate) {
        long value = millisecLength;
        this.hour = (int) (value / 60 / 60 / 1000);
        value = value - ((long) hour * 60 * 60 * 1000);
        this.min = (int) (value / 60 / 1000);
        value = value - ((long) min * 60 * 1000);
        this.sec = (int) (value / 1000);
        value = value - (sec * 1000L);
        this.frame = (int) (value / (1000 / framerate));
    }
    
    private void syncFrom(int framerate) {
        int hourM = this.hour * 60 * 60 * 1000;
        int minM = this.min * 60 * 1000;
        int secM = this.sec * 1000;
        int frameM = this.frame * (1000 / framerate);
        this.millisecLength = hourM + minM + secM + frameM;
    }
    
    /**
     * 
     * @return hour value
     * @throws NullPointerException if the instance is unsynced
     */
    public int getHour() {
        return hour;
    }

    /**
     * 
     * @return minute value
     * @throws NullPointerException if the instance is unsynced
     */
    public int getMin() {
        return min;
    }

    /**
     * 
     * @return second value
     * @throws NullPointerException if the instance is unsynced
     */
    public int getSec() {
        return sec;
    }

    /**
     * 
     * @return frame value
     * @throws NullPointerException if the instance is unsynced
     */
    public int getFrame() {
        return frame;
    }
    
    /**
     * 
     * @return absolute value, if the timecode negative, unsynced.
     */
    public Timecode abs() {
        return new Timecode(Math.abs(millisecLength));
    }
    
    /**
     * 
     * @return the millisecond value of the timecode
     */
    public long millis() {
        return millisecLength;
    }
    
    /**
     * Subtracts the specified timecode value from the instance
     * @param t
     * @return the subtracted value, unsynced
     */
    public Timecode subtract(Timecode t) {
        return new Timecode(this.millisecLength - t.millisecLength);
    }
    
    /**
     * Adds togheter the two timecodes
     * @param t
     * @return the result, unsynced
     */
    public Timecode add(Timecode t) {
        long time = this.millisecLength + t.millisecLength;
        return new Timecode(time);
    }

    public boolean isBetween(Timecode start, Timecode end) {
        return this.compareTo(start) <= 0 && this.compareTo(end) <= 0;
    }
    
    /**
     * Same as {@link #toString()}, but it's spaced
     * @return gui formatted string
     */
    public String guiFormatted() {
        return (hour < 10 ? "0" + hour : hour) + " : " + (min < 10 ? "0" + min : min) + " : " + (sec < 10 ? "0" + sec : sec) + " / " + (frame < 10 ? "0" + frame : frame); 
    }

    @Override
    public int compareTo(@NotNull Timecode other) {
        if (this.equals(other)) {
            return 0;
        }
        if (this.millisecLength > other.millisecLength) {
            return 1;
        } else {
            return -1;
        }
    }

}
