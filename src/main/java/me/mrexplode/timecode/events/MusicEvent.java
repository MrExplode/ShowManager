package me.mrexplode.timecode.events;

import me.mrexplode.timecode.fileio.Music;

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
