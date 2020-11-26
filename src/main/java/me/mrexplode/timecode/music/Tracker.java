package me.mrexplode.timecode.music;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.mrexplode.timecode.util.Timecode;

@Setter
@AllArgsConstructor
public class Tracker {
    
    @Getter private int index;
    @Getter private Timecode start;
    @Getter private Timecode end;
    private boolean isPaused;
    
    public boolean inTrack(Timecode time) {
        return time.compareTo(start) >= 0 && time.compareTo(end) <= 0;
    }
    
    public boolean isNaturalEnd() {
        return isPaused;
    }
    
    public void setnaturalEnd(boolean value) {
        isPaused = value;
    }

}
