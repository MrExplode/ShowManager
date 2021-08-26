package me.sunstorm.showmanager.settings.config;

import lombok.Data;

@Data
public class AudioPlayerConfig {
    private boolean enabled = false;
    private String audioOutput = "";
    //private List<Music> trackList = new ArrayList<>();
}
