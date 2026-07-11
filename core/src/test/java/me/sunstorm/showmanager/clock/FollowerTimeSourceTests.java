package me.sunstorm.showmanager.clock;

import me.sunstorm.showmanager.util.Timecode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FollowerTimeSourceTests {

    @Test
    void tracksFedStateFromMaster() {
        FollowerTimeSource source = new FollowerTimeSource();
        assertThat(source.isPlaying()).isFalse();

        source.start();
        source.feed(new Timecode(0, 0, 5, 0));
        assertThat(source.isPlaying()).isTrue();
        assertThat(source.current()).isEqualTo(new Timecode(0, 0, 5, 0));

        source.pause();
        assertThat(source.isPlaying()).isFalse();

        source.stop();
        assertThat(source.current()).isEqualTo(new Timecode(0));
    }

    @Test
    void tickDoesNotAdvance() {
        FollowerTimeSource source = new FollowerTimeSource();
        source.feed(new Timecode(0, 0, 10, 0));
        source.tick(System.currentTimeMillis());
        assertThat(source.current()).isEqualTo(new Timecode(0, 0, 10, 0));
    }
}
