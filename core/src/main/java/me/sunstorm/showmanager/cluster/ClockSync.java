package me.sunstorm.showmanager.cluster;

import me.sunstorm.showmanager.clock.ClockOffset;
import me.sunstorm.showmanager.terminable.Terminable;
import org.jgroups.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Software offset/delay estimator (SNTP-style four-timestamp exchange) that lets a follower align
 * its monotonic clock to the coordinator's despite transit latency. A follower probes the
 * coordinator every {@link #PERIOD_MS}; both stamp {@link System#nanoTime}:
 * <pre>offset = ((t2-t1) + (t3-t4)) / 2   delay = (t4-t1) - (t3-t2)</pre>
 * where t1/t4 are the follower's nanos and t2/t3 the coordinator's. offset therefore maps follower
 * nanos -> coordinator nanos. Samples are min-delay filtered: the lowest-delay round-trip in the
 * window is the least perturbed by queuing/GC/scheduler spikes, so its offset is the trustworthy one.
 */
@Singleton
public class ClockSync implements ClockOffset, Terminable {
    private static final Logger log = LoggerFactory.getLogger(ClockSync.class);
    private static final int WINDOW = 8;
    private static final long PERIOD_MS = 1000;

    private final ClusterService cluster;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "clock-sync");
        t.setDaemon(true);
        return t;
    });

    private final long[] offsets = new long[WINDOW];
    private final long[] delays = new long[WINDOW];
    private int count = 0;
    private int idx = 0;
    private int logged = 0;
    private volatile boolean synced = false;
    private volatile long offsetNanos = 0;
    private volatile long delayNanos = 0;

    @Inject
    public ClockSync(ClusterService cluster) {
        this.cluster = cluster;
        this.cluster.setSyncHandler(this::onSync);
        register();
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::probe, PERIOD_MS, PERIOD_MS, TimeUnit.MILLISECONDS);
    }

    private void probe() {
        try {
            if (!cluster.isConnected() || cluster.isCoordinator())
                return;
            Address coord = cluster.getCoordinator();
            if (coord == null)
                return;
            cluster.sendSync(coord, ClusterService.MSG_SYNC_REQ, ByteBuffer.allocate(8).putLong(System.nanoTime()).array());
        } catch (Exception e) {
            log.debug("clock sync probe failed", e);
        }
    }

    private void onSync(Address src, byte type, byte[] body) {
        try {
            if (type == ClusterService.MSG_SYNC_REQ) {
                long t1 = ByteBuffer.wrap(body).getLong();
                long t2 = System.nanoTime();
                long t3 = System.nanoTime();
                cluster.sendSync(src, ClusterService.MSG_SYNC_RESP, ByteBuffer.allocate(24).putLong(t1).putLong(t2).putLong(t3).array());
            } else if (type == ClusterService.MSG_SYNC_RESP) {
                long t4 = System.nanoTime();
                ByteBuffer b = ByteBuffer.wrap(body);
                long t1 = b.getLong(), t2 = b.getLong(), t3 = b.getLong();
                record(((t2 - t1) + (t3 - t4)) / 2, (t4 - t1) - (t3 - t2));
            }
        } catch (Exception e) {
            log.debug("clock sync handling failed", e);
        }
    }

    private synchronized void record(long offset, long delay) {
        offsets[idx] = offset;
        delays[idx] = delay;
        idx = (idx + 1) % WINDOW;
        if (count < WINDOW)
            count++;
        int best = minDelayIndex(delays, count);
        offsetNanos = offsets[best];
        delayNanos = delays[best];
        synced = true;
        if (++logged % 10 == 1)
            log.info("[clock] offset {} us, one-way delay {} us (min of {} samples)", offsetNanos / 1000, delayNanos / 2000, count);
    }

    /**
     * Index of the lowest-delay (least perturbed) sample in the window.
     */
    static int minDelayIndex(long[] delays, int count) {
        int best = 0;
        for (int i = 1; i < count; i++)
            if (delays[i] < delays[best])
                best = i;
        return best;
    }

    /**
     * Follower nanos -> coordinator nanos correction; 0 when this node is the master.
     */
    @Override
    public long offsetNanos() {
        return offsetNanos;
    }

    @Override
    public boolean synced() {
        return synced;
    }

    public long delayNanos() {
        return delayNanos;
    }

    @Override
    public void shutdown() {
        scheduler.shutdownNow();
    }
}
