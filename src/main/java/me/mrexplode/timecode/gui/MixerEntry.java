package me.mrexplode.timecode.gui;

import javax.sound.sampled.Mixer;

public class MixerEntry {
    
    private Mixer.Info mixerInfo;
    private String name;
    
    public MixerEntry(String name, Mixer.Info mixer) {
        this.name = name;
        this.mixerInfo = mixer;
    }
    
    @Override
    public String toString() {
        return name;
    }

    
    public Mixer.Info getMixerInfo() {
        return mixerInfo;
    }

    
    public void setMixerInfo(Mixer.Info mixer) {
        this.mixerInfo = mixer;
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

}
