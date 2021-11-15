package me.sunstorm.showmanager.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Getter
@Setter
@ToString
public class Timecode implements Comparable<Timecode> {
    public static final Timecode ZERO = new Timecode(0);
    @Setter(AccessLevel.NONE) private long millisecLength;
    
    private int hour;
    private int min;
    private int sec;
    private int frame;

    public Timecode(long lengthInMillis) {
        this.millisecLength = lengthInMillis;
        syncTo(Framerate.get());
    }
    
    public Timecode(int hour, int min, int sec, int frame) {
        this.hour = hour;
        this.min = min;
        this.sec = sec;
        this.frame = frame;
        syncFrom(Framerate.get());
    }

    public void set(long lengthInMillis) {
        this.millisecLength = lengthInMillis;
        syncTo(Framerate.get());
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

    public Timecode abs() {
        return new Timecode(Math.abs(millisecLength));
    }

    public long millis() {
        return millisecLength;
    }

    public Timecode subtract(@NotNull Timecode t) {
        return new Timecode(this.millisecLength - t.millisecLength);
    }

    public Timecode add(@NotNull Timecode t) {
        long time = this.millisecLength + t.millisecLength;
        return new Timecode(time);
    }

    public boolean isBetween(Timecode start, Timecode end) {
        return this.compareTo(start) >= 0 && this.compareTo(end) <= 0;
    }

    public String guiFormatted(boolean spaced) {
        String spacer = spaced ? " : " : ":";
        return (hour < 10 ? "0" + hour : hour) + spacer + (min < 10 ? "0" + min : min) + spacer + (sec < 10 ? "0" + sec : sec) + spacer.replace(':', '/') + (frame < 10 ? "0" + frame : frame);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timecode timecode = (Timecode) o;
        return hour == timecode.hour && min == timecode.min && sec == timecode.sec && frame == timecode.frame;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hour, min, sec, frame);
    }

    public Timecode copy() {
        return new Timecode(hour, min, sec, frame);
    }
}
