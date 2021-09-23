package me.sunstorm.showmanager.settings.config;

import lombok.Data;
import me.sunstorm.showmanager.audio.AudioTrack;

import java.util.ArrayList;
import java.util.List;

@Data
public class AudioPlayerConfig {
    private boolean enabled = false;
    private String audioOutput = "";
    private List<AudioTrack> tracks = new ArrayList<>();
}
