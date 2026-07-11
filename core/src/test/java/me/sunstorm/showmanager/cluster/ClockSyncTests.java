package me.sunstorm.showmanager.cluster;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ClockSyncTests {

    @Test
    void picksLowestDelaySample() {
        long[] delays = {900_000, 1_200_000, 40_000_000, 700_000, 5_000_000};
        assertThat(ClockSync.minDelayIndex(delays, 5)).isEqualTo(3);
    }

    @Test
    void respectsWindowCount() {
        long[] delays = {800_000, 600_000, 0, 0, 0};
        assertThat(ClockSync.minDelayIndex(delays, 2)).isEqualTo(1);
    }
}
