package me.sunstorm.showmanager.audio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.sunstorm.showmanager.util.Timecode;

import java.io.File;

@Getter
@AllArgsConstructor
public class AudioTrack {
    @Setter private Timecode startTime;
    private final File file;
}
