package me.sunstorm.showmanager.clock;

import me.sunstorm.showmanager.util.Timecode;

public interface TimeSource {

    void start();

    void pause();

    void stop();

    void seek(Timecode time);

    void tick(long now);

    Timecode current();

    boolean isPlaying();
}
