package me.mrexplode.showmanager.events.impl.music;

import me.mrexplode.showmanager.events.EventType;
import me.mrexplode.showmanager.events.TimecodeEvent;
import me.mrexplode.showmanager.fileio.Music;

public class MusicEvent extends TimecodeEvent {
    
    private transient float[] samples;
    private Music music;

    public MusicEvent(EventType type, float[] samples, Music music) {
        super(type, "MusicEvent");
        this.samples = samples;
        this.music = music;
    }
    
    public float[] getSamples() {
        if (this.getType() != EventType.MUSIC_LOAD)
            throw new IllegalArgumentException("sample array only available on load event!");
        
        return samples;
    }
    
    public Music getMusic() {
        return music;
    }

}
