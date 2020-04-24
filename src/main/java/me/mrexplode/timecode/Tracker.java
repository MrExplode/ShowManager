package me.mrexplode.timecode;


public class Tracker {
    
    private int index;
    private Timecode start;
    private Timecode end;
    private boolean isPaused = false;
    
    public Tracker(int index, Timecode start, Timecode end) {
        this.index = index;
        this.start = start;
        this.end = end;
    }
    
    public boolean inTrack(Timecode time) {
        return time.compareTo(start) >= 0 && time.compareTo(end) <= 0;
    }

    
    public int getIndex() {
        return index;
    }

    
    public void setIndex(int index) {
        this.index = index;
    }

    
    public Timecode getStart() {
        return start;
    }

    
    public void setStart(Timecode start) {
        this.start = start;
    }

    
    public Timecode getEnd() {
        return end;
    }

    
    public void setEnd(Timecode end) {
        this.end = end;
    }
    
    public boolean isNaturalEnd() {
        return isPaused;
    }
    
    public void setnaturalEnd(boolean value) {
        isPaused = value;
    }

}
