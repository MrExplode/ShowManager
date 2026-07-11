package me.sunstorm.showmanager.clock;

/**
 * Follower-to-master monotonic clock correction, supplied by the cluster's offset estimator.
 */
public interface ClockOffset {
    /**
     * Nanos to add to local {@link System#nanoTime} to obtain the master's nanoTime.
     */
    long offsetNanos();

    /**
     * True once at least one offset sample has been measured.
     */
    boolean synced();
}
