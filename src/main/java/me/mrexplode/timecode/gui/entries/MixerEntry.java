package me.mrexplode.timecode.gui.entries;

import javax.sound.sampled.Mixer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MixerEntry {
    
    private Mixer.Info mixerInfo;
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
