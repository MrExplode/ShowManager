package me.sunstorm.showmanager;

import me.sunstorm.showmanager.util.Framerate;
import me.sunstorm.showmanager.util.Timecode;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(new Timecode(0, 0, 0, 0), new Timecode(0, 0, 0, 0));
    }
    
    @RepeatedTest(3)
    public void testMillis() {
        assertEquals(0, new Timecode(0, 0, 0, 0).millis());
        Timecode time = new Timecode(1, 2, 3, 4);
        if (Framerate.get() == 24)
            assertEquals(3723164, time.millis());
        if (Framerate.get() == 25)
            assertEquals(3723160, time.millis());
        if (Framerate.get() == 30)
            assertEquals(3723132, time.millis());
    }
    
    
    @RepeatedTest(3)
    public void testAdd() {
        Timecode value = new Timecode(0, 0, 5, 0).add(new Timecode(0, 0, 5, 0));
        Timecode excepted = new Timecode(0, 0, 10, 0);
        assertEquals(excepted, value);
        assertEquals(excepted.millis(), value.millis());
    }
    
    @RepeatedTest(3)
    public void testSubtract() {
        Timecode value = new Timecode(0, 0, 10, 0).subtract(new Timecode(0, 0, 5, 0));
        Timecode excepted = new Timecode(0, 0, 5, 0);
        assertEquals(excepted, value);
        assertEquals(excepted.millis(), value.millis());
    }
}
