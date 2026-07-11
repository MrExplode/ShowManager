package me.sunstorm.showmanager.clock;

import me.sunstorm.showmanager.util.Timecode;

public class MasterTimeSource implements TimeSource {
    private volatile boolean playing = false;
    private long start = 0;
    private long elapsed = 0;
    private volatile Timecode currentTime = new Timecode(0);

    @Override
    public synchronized void start() {
        if (start == 0)
            start = System.currentTimeMillis();
        else
            start = System.currentTimeMillis() - elapsed;
        playing = true;
    }

    @Override
    public synchronized void pause() {
        playing = false;
    }

    @Override
    public synchronized void stop() {
        playing = false;
        currentTime = new Timecode(0);
        start = 0;
        elapsed = 0;
    }

    @Override
    public synchronized void seek(Timecode time) {
        elapsed = time.millis();
        start = System.currentTimeMillis() - elapsed;
        currentTime = time.copy();
    }

    @Override
    public synchronized void tick(long now) {
        elapsed = now - start;
        currentTime = new Timecode(elapsed);
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
