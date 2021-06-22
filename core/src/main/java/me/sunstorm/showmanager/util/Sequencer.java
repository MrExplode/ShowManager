package me.sunstorm.showmanager.util;

import java.util.ArrayList;
import java.util.List;

public class Sequencer {
    
    public static List<ArraySegment> sequence(float[] data, int maxSize) {
        ArrayList<ArraySegment> segments = new ArrayList<>();
        if (data.length <= maxSize) {
            segments.add(new ArraySegment(1, 1, data));
            return segments;
        }
        
        int segmentCount = (int) Math.ceil((double) data.length / (double) maxSize);
        int segmentLength = (int) Math.floor(data.length / (float) segmentCount);
        
        int offset = 0;
        int remaining = data.length;
        for (int i = 0; i < segmentCount; i++) {
            float[] current = new float[segmentLength];
            System.arraycopy(data, offset, current, 0, segmentLength);
            offset += segmentLength;
            remaining -= segmentLength;
            segments.add(new ArraySegment(i, segmentCount, current));
        }
        if (remaining > 0) {
            handleRemaining(segments, data, segmentCount, remaining, maxSize);
        }
        
        return segments;
    }
    
    public static float[] merge(List<ArraySegment> segments) {
        float[] merged = new float[0];
        for (ArraySegment segment : segments) {
            merged = add(merged, segment.getData());
        }
        return merged;
    }
    
    private static float[] add(float[] a, float[] b) {
        float[] value = new float[a.length + b.length];
        System.arraycopy(a, 0, value, 0, a.length);
        System.arraycopy(b, 0, value, a.length, b.length);
        return value;
    }
    
    private static void handleRemaining(List<ArraySegment> segments, float[] data, final int segmentCount, int remainingData, int maxSize) {
        int count = segmentCount;
        if (remainingData > maxSize) {
            float[] var1 = new float[maxSize];
            System.arraycopy(data, data.length - remainingData, var1, 0, var1.length);
            count++;
            for (ArraySegment segment : segments) {
                segment.setMax(count);
            }
            segments.add(new ArraySegment(segments.get(segments.size() -1).getId() + 1, count, var1));
            handleRemaining(segments, data, count, remainingData - maxSize, maxSize);
        } else {
            count++;
            for (ArraySegment segment : segments) {
                segment.setMax(count);
            }
            float[] rem = new float[remainingData];
            System.arraycopy(data, data.length - remainingData, rem, 0, remainingData);
            segments.add(new ArraySegment(segments.get(segments.size() -1).getId() + 1, count, rem)); 
        }
    }

}
