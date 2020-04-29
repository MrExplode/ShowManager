package me.mrexplode.timecode;

import java.util.ArrayList;
import java.util.List;

import com.illposed.osc.transport.udp.OSCPortIn;

public class Sequencer {
    
    private static final int maxSize = 16000;
    
    public static List<ArraySegment> sequence(float[] data) {
        ArrayList<ArraySegment> segments = new ArrayList<ArraySegment>();
        if (data.length <= maxSize) {
            segments.add(new ArraySegment(1, 1, data));
            return segments;
        }
        
        int segmentCount = (int) Math.ceil((double) data.length / (double) maxSize);
        int segmentLength = (int) Math.floor(data.length / segmentCount);
        
        int offset = 0;
        for (int i = 0; i < segmentCount; i++) {
            float[] current = new float[segmentLength];
            System.arraycopy(data, offset, current, 0, segmentLength);
            offset += segmentLength;
            segments.add(new ArraySegment(i, segmentCount, current));
        }
        
        return segments;
    }
    
    public static float[] merge(List<ArraySegment> segments) {
        float[] merged = new float[0];
        for (int i = 0; i < segments.size(); i++) {
            merged = add(merged, segments.get(i).getData());
        }
        return merged;
    }
    
    private static float[] add(float[] a, float[] b) {
        float[] value = new float[a.length + b.length];
        System.arraycopy(a, 0, value, 0, a.length);
        System.arraycopy(b, 0, value, a.length, b.length);
        return value;
    }

}
