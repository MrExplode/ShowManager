package me.sunstorm.showmanager;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Random;

import me.sunstorm.showmanager.util.ArraySegment;
import me.sunstorm.showmanager.util.Sequencer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class SequencerTests {
    
    private float[] testArray;
    
    @BeforeAll
    public void init() {
        testArray = new float[1000];
        for (int i = 0; i < 1000; i++) {
            testArray[i] = i;
        }
    }
    
    @RepeatedTest(30)
    public void segmentSize() {
        int size = new Random(System.currentTimeMillis()).nextInt(40) + 20;
        ArrayList<ArraySegment> segments = (ArrayList<ArraySegment>) Sequencer.sequence(testArray, size);
        assertNotEquals(null, segments);
        for (ArraySegment segment : segments) {
            int len = segment.getData().length;
            assertTrue(len <= size, "length (" + len + ") greather than max size (" + size + ")");
        }
    }
    
    @RepeatedTest(30)
    public void sequenceSize() {
        int size = new Random(System.currentTimeMillis()).nextInt(40) + 20;
        ArrayList<ArraySegment> segments = (ArrayList<ArraySegment>) Sequencer.sequence(testArray, size);
        int length = 0;
        for (ArraySegment segment : segments) {
            length += segment.getData().length;
        }
        assertEquals(testArray.length, length);
    }
    
    @RepeatedTest(10)
    public void merge() {
        int size = new Random(System.currentTimeMillis()).nextInt(40) + 20;
        ArrayList<ArraySegment> segments = (ArrayList<ArraySegment>) Sequencer.sequence(testArray, size);
        assertArrayEquals(testArray, Sequencer.merge(segments));
    }

}
