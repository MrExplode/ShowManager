package me.sunstorm.showmanager.config.server;

import lombok.Data;
import me.sunstorm.showmanager.fileio.Music;

import java.util.ArrayList;
import java.util.List;

@Data
public class AudioPlayerConfig {
    private boolean enabled = false;
    private String audioOutput = "";
    private List<Music> trackList = new ArrayList<>();
}
