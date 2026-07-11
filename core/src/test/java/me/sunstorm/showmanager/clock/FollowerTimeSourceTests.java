package me.sunstorm.showmanager.clock;

import me.sunstorm.showmanager.util.Timecode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FollowerTimeSourceTests {

    private record FixedOffset(long offsetNanos, boolean synced) implements ClockOffset {
    }

    @Test
    void dumbFollowsUntilSynced() {
        FollowerTimeSource source = new FollowerTimeSource(new FixedOffset(0, false));
        assertThat(source.isPlaying()).isFalse();

        source.start();
        source.feed(new Timecode(0, 0, 5, 0), System.nanoTime());
        assertThat(source.isPlaying()).isTrue();
        assertThat(source.current()).isEqualTo(new Timecode(0, 0, 5, 0));

        source.pause();
        assertThat(source.isPlaying()).isFalse();

        source.stop();
        assertThat(source.current()).isEqualTo(new Timecode(0));
    }

    @Test
    void tickDoesNotAdvance() {
        FollowerTimeSource source = new FollowerTimeSource(new FixedOffset(0, true));
        source.feed(new Timecode(0, 0, 10, 0), 0);
        source.tick(System.nanoTime());
        assertThat(source.current()).isEqualTo(new Timecode(0, 0, 10, 0));
    }

    @Test
    void extrapolatesFromAnchorWithOffset() {
        long offset = 123_456;
        FollowerTimeSource source = new FollowerTimeSource(new FixedOffset(offset, true));
        source.start();
        long masterNow = System.nanoTime() + offset;
        source.feed(new Timecode(0), masterNow - 5_000_000_000L);
        assertThat(source.current().millis()).isBetween(5_000L, 5_100L);
    }
}
