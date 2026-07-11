package me.sunstorm.showmanager.clock;

import me.sunstorm.showmanager.util.Timecode;

public class MasterTimeSource implements TimeSource {
    private boolean playing = false;
    private long start = 0;
    private long elapsed = 0;
    private Timecode currentTime = new Timecode(0);

    @Override
    public void start() {
        if (start == 0)
            start = System.currentTimeMillis();
        else
            start = System.currentTimeMillis() - elapsed;
        playing = true;
    }

    @Override
    public void pause() {
        playing = false;
    }

    @Override
    public void stop() {
        playing = false;
        currentTime = new Timecode(0);
        start = 0;
    }

    @Override
    public void seek(Timecode time) {
        elapsed = time.millis();
        start = System.currentTimeMillis() - elapsed;
        currentTime = time;
    }

    @Override
    public void tick(long now) {
        elapsed = now - start;
        currentTime.set(elapsed);
    }

    @Override
    public Timecode current() {
        return currentTime;
    }

    @Override
    public boolean isPlaying() {
        return playing;
    }
}
