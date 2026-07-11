package me.sunstorm.showmanager.clock;

import me.sunstorm.showmanager.util.Timecode;

/**
 * Chasing clock. Each beacon anchors "show position P was true at master-nanos Tm"; between beacons
 * the position is extrapolated from local {@link System#nanoTime} corrected by the measured offset,
 * so output is smooth and compensated for transit latency (it tracks where the master is *now*, not
 * where it was when the beacon left). Until the first offset sample arrives it degrades to plain
 * dumb-follow (last received position).
 */
public class FollowerTimeSource implements TimeSource {
    private final ClockOffset offset;
    private volatile boolean playing = false;
    private volatile long anchorMillis = 0;
    private volatile long anchorMasterNanos = 0;
    private volatile Timecode fallback = new Timecode(0);

    public FollowerTimeSource(ClockOffset offset) {
        this.offset = offset;
    }

    /**
     * Beacon: the master reports position {@code time}, sampled at its {@code masterNanos}.
     */
    public void feed(Timecode time, long masterNanos) {
        anchorMillis = time.millis();
        anchorMasterNanos = masterNanos;
        fallback = time;
    }

    @Override
    public void start() {
        playing = true;
    }

    @Override
    public void pause() {
        fallback = current();
        anchorMasterNanos = 0;
        playing = false;
    }

    @Override
    public void stop() {
        playing = false;
        fallback = new Timecode(0);
        anchorMillis = 0;
        anchorMasterNanos = 0;
    }

    @Override
    public void seek(Timecode time) {
        fallback = time;
        anchorMillis = time.millis();
        anchorMasterNanos = 0;
    }

    @Override
    public void tick(long now) {
    }

    @Override
    public Timecode current() {
        if (playing && anchorMasterNanos != 0 && offset.synced()) {
            long masterNow = System.nanoTime() + offset.offsetNanos();
            long millis = anchorMillis + (masterNow - anchorMasterNanos) / 1_000_000L;
            return new Timecode(Math.max(0, millis));
        }
        return fallback;
    }

    @Override
    public boolean isPlaying() {
        return playing;
    }
}
