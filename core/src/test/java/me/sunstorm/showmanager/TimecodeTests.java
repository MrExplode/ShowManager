package me.sunstorm.showmanager;

import me.sunstorm.showmanager.util.Timecode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

import static org.junit.jupiter.api.Assertions.*;

public class TimecodeTests {
    
    private int framerate;
    
    @BeforeEach
    public void init(RepetitionInfo info) {
        if (info.getCurrentRepetition() == 1)
            framerate = 24;
        if (info.getCurrentRepetition() == 2)
            framerate = 25;
        if (info.getCurrentRepetition() == 3)
            framerate = 30;
    }
    
    @RepeatedTest(3)
    @DisplayName("Equality test")
    public void testEquals() {
        assertEquals(new Timecode(0, 0, 0, 0, framerate), new Timecode(0, 0, 0, 0, framerate));
    }
    
    @RepeatedTest(3)
    public void testMillis() {
        assertEquals(0, new Timecode(0, 0, 0, 0, framerate).millis());
        Timecode time = new Timecode(1, 2, 3, 4, framerate);
        if (framerate == 24)
            assertEquals(3723164, time.millis());
        if (framerate == 25)
            assertEquals(3723160, time.millis());
        if (framerate == 30)
            assertEquals(3723132, time.millis());
    }
    
    
    @RepeatedTest(3)
    public void testAdd() {
        Timecode value = new Timecode(0, 0, 5, 0, framerate).add(new Timecode(0, 0, 5, 0, framerate)).syncedInstance(framerate);
        Timecode excepted = new Timecode(0, 0, 10, 0, framerate);
        assertEquals(excepted, value);
        assertEquals(excepted.millis(), value.millis());
    }
    
    @RepeatedTest(3)
    public void testSubtract() {
        Timecode value = new Timecode(0, 0, 10, 0, framerate).subtract(new Timecode(0, 0, 5, 0, framerate)).syncedInstance(framerate);
        Timecode excepted = new Timecode(0, 0, 5, 0, framerate);
        assertEquals(excepted, value);
        assertEquals(excepted.millis(), value.millis());
    }
}
