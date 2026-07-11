package me.sunstorm.showmanager.clock;

import me.sunstorm.showmanager.util.Timecode;

public class MasterTimeSource implements TimeSource {
    private volatile boolean playing = false;
    private long startNanos = 0;
    private long elapsedMillis = 0;
    private volatile Timecode currentTime = new Timecode(0);

    @Override
    public synchronized void start() {
        startNanos = System.nanoTime() - elapsedMillis * 1_000_000L;
        playing = true;
    }

    @Override
    public synchronized void pause() {
        if (playing)
            elapsedMillis = (System.nanoTime() - startNanos) / 1_000_000L;
        playing = false;
    }

    @Override
    public synchronized void stop() {
        playing = false;
        currentTime = new Timecode(0);
        startNanos = 0;
        elapsedMillis = 0;
    }

    @Override
    public synchronized void seek(Timecode time) {
        elapsedMillis = time.millis();
        startNanos = System.nanoTime() - elapsedMillis * 1_000_000L;
        currentTime = time.copy();
    }

    @Override
    public synchronized void tick(long nowNanos) {
        elapsedMillis = (nowNanos - startNanos) / 1_000_000L;
        currentTime = new Timecode(elapsedMillis);
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
