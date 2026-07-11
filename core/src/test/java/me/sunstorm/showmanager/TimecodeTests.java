package me.sunstorm.showmanager;

import me.sunstorm.showmanager.util.Framerate;
import me.sunstorm.showmanager.util.Timecode;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

import static org.assertj.core.api.Assertions.*;

public class TimecodeTests {
    
    @BeforeEach
    public void init(@NotNull RepetitionInfo info) {
        if (info.getCurrentRepetition() == 1)
            Framerate.set(24);
        if (info.getCurrentRepetition() == 2)
            Framerate.set(25);
        if (info.getCurrentRepetition() == 3)
            Framerate.set(30);
    }
    
    @RepeatedTest(3)
    @DisplayName("Equality test")
    public void testEquals() {
        assertThat(new Timecode(0, 0, 0, 0)).isEqualTo(new Timecode(0, 0, 0, 0));
    }
    
    @RepeatedTest(3)
    public void testMillis() {
        assertThat(new Timecode(0, 0, 0, 0).millis()).isEqualTo(0);
        Timecode time = new Timecode(1, 2, 3, 4);
        if (Framerate.get() == 24)
            assertThat(time.millis()).isEqualTo(3723167);
        if (Framerate.get() == 25)
            assertThat(time.millis()).isEqualTo(3723160);
        if (Framerate.get() == 30)
            assertThat(time.millis()).isEqualTo(3723134);
    }

    @RepeatedTest(3)
    @DisplayName("Every frame survives the conversion to millis and back")
    public void testFrameRoundTrip() {
        for (int frame = 0; frame < Framerate.get(); frame++)
            assertThat(new Timecode(0, 0, 0, frame).getFrame()).isEqualTo(frame);
    }

    @RepeatedTest(3)
    @DisplayName("Millis never convert to a frame number the framerate doesn't have")
    public void testFrameRange() {
        for (long millis = 0; millis < 1000; millis++)
            assertThat(new Timecode(millis).getFrame()).isBetween(0, Framerate.get() - 1);
    }


    @RepeatedTest(3)
    public void testAdd() {
        Timecode value = new Timecode(0, 0, 5, 0).add(new Timecode(0, 0, 5, 0));
        Timecode excepted = new Timecode(0, 0, 10, 0);
        assertThat(value).isEqualTo(excepted);
        assertThat(value.millis()).isEqualTo(excepted.millis());
    }
    
    @RepeatedTest(3)
    public void testSubtract() {
        Timecode value = new Timecode(0, 0, 10, 0).subtract(new Timecode(0, 0, 5, 0));
        Timecode excepted = new Timecode(0, 0, 5, 0);
        assertThat(value).isEqualTo(excepted);
        assertThat(value.millis()).isEqualTo(excepted.millis());
    }
}
