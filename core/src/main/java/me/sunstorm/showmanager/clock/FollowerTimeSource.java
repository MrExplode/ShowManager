package me.sunstorm.showmanager.clock;

import me.sunstorm.showmanager.util.Timecode;

public class FollowerTimeSource implements TimeSource {
    private volatile boolean playing = false;
    private volatile Timecode current = new Timecode(0);

    public void feed(Timecode time) {
        this.current = time;
    }

    @Override
    public void start() {
        playing = true;
    }

    @Override
    public void pause() {
        playing = false;
    }

    @Override
    public void stop() {
        playing = false;
        current = new Timecode(0);
    }

    @Override
    public void seek(Timecode time) {
        current = time;
    }

    @Override
    public void tick(long now) {
    }

    @Override
    public Timecode current() {
        return current;
    }

    @Override
    public boolean isPlaying() {
        return playing;
    }
}
