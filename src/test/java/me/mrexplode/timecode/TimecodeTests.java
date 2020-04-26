package me.mrexplode.timecode;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

public class TimecodeTests {
    
    public int framerate = 25;
    private Timecode time1;
    private Timecode time2;
    private Timecode time3;
    
    @BeforeEach
    public void init(RepetitionInfo repInfo) {
        if (repInfo.getCurrentRepetition() == 1)
            framerate = 24;
        if (repInfo.getCurrentRepetition() == 2)
            framerate = 25;
        if (repInfo.getCurrentRepetition() == 3)
            framerate = 30;
        
        this.time1 = new Timecode(0, 0, 0, 0);
        this.time2 = new Timecode(1, 2, 3, 4);
        this.time3 = new Timecode(0, 15, 28, 16);
    }
    
    @RepeatedTest(1)
    public void equal() {
        Timecode value = new Timecode(1, 2, 3, 4);
        assertTrue(value.equals(time2));
        assertFalse(value.equals(time3));
    }
    
    @RepeatedTest(1)
    public void abs() {
        Timecode negative = new Timecode(2, -15, 25, -23);
        assertEquals(new Timecode(2, 15, 25, 23), negative.abs());
    }
    
    @RepeatedTest(1)
    public void comparison() {
        assertEquals(0, time1.compareTo(new Timecode(0, 0, 0, 0)));
        assertEquals(-1, time1.compareTo(time2));
        assertEquals(1, time2.compareTo(time1));
        assertEquals(1, time2.compareTo(time3));
    }
    
    @RepeatedTest(3)
    public void frames() {
        assertEquals(0, time1.frames(framerate));
        if (framerate == 24) {
            assertEquals(89354, time2.frames(framerate));
        }
        if (framerate == 25) {
            assertEquals(93300, time2.frames(framerate));
        }
        
        if (framerate == 30) {
            assertEquals(111694, time2.frames(framerate));
        }
    }
    
    @RepeatedTest(3)
    public void millis() {
        assertEquals(time1.millis(framerate), 0);
        assertEquals(time2.millis(framerate), 3723160);
    }
    
    @RepeatedTest(3)
    public void add() {
        assertEquals(new Timecode(1, 2, 3, 4), time1.add(time2, framerate));
        assertEquals(new Timecode(1, 17, 31, 20), time2.add(time3, framerate));
    }
    
    @RepeatedTest(3)
    public void subtract() {
        //write more
        Timecode value = new Timecode(0, 0, 0, 0);
        assertEquals(new Timecode(0, 0, 0, 0), time1.subtract(value));
    }
    
    @RepeatedTest(3)
    public void from() {
        assertEquals(time1, Timecode.from(0, framerate));
        assertEquals(time2, Timecode.from(3723160, framerate));
    }

}
