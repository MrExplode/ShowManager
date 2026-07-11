package me.sunstorm.showmanager.ltc;

import java.util.Arrays;

/**
 * Generates SMPTE 12M linear timecode audio, one frame at a time, as signed 16 bit mono samples.
 * Biphase mark encoded: the signal flips at every bit cell, and a {@code 1} flips it mid cell too.
 * Cell lengths stay fractional and the leftover carries across frames, so rates that don't divide
 * evenly (44100 / 25) don't drift. Not thread safe.
 */
public final class LtcEncoder {
    private final int framerate;
    private final double samplesPerBit;
    private final double samplesPerHalfBit;
    private final double filterConst;
    private final int maxFrameSamples;
    private final short high;
    private final short low;

    private final LtcFrame frame = new LtcFrame();
    private double remainder = 0.5;
    private boolean state = false;

    /**
     * @param sampleRate     output sample rate
     * @param framerate      24, 25 or 30
     * @param levelDbfs      signal level, in dBFS. 0 is full scale
     * @param riseTimeMicros transition rise time in microseconds, 0 for an unfiltered square wave.
     *                       SMPTE asks for 25, EBU for 50
     */
    public LtcEncoder(int sampleRate, int framerate, double levelDbfs, double riseTimeMicros) {
        this.framerate = framerate;
        this.samplesPerBit = (double) sampleRate / (framerate * LtcFrame.BIT_COUNT);
        this.samplesPerHalfBit = samplesPerBit / 2;
        // a frame may run one sample long, depending on the carried remainder
        this.maxFrameSamples = (int) Math.ceil((double) sampleRate / framerate) + 1;
        short peak = (short) Math.round(Short.MAX_VALUE * Math.pow(10, levelDbfs / 20));
        this.high = peak;
        this.low = (short) -peak;
        this.filterConst = riseTimeMicros <= 0
                ? 0
                : 1 - Math.exp(-1 / (sampleRate * riseTimeMicros / 2_000_000d / Math.E));
    }

    /**
     * @return the largest sample count {@link #encode} can produce, ie. the buffer size it needs
     */
    public int maxFrameSamples() {
        return maxFrameSamples;
    }

    /**
     * Encodes a single timecode frame into {@code out}.
     *
     * @return the number of samples written
     */
    public int encode(int hour, int min, int sec, int frameNumber, short[] out) {
        frame.set(hour, min, sec, frameNumber, framerate);
        int offset = 0;
        for (int bit = 0; bit < LtcFrame.BIT_COUNT; bit++) {
            if (frame.get(bit)) {
                offset = segment(out, offset, samplesPerHalfBit);
                offset = segment(out, offset, samplesPerHalfBit);
            } else {
                offset = segment(out, offset, samplesPerBit);
            }
        }
        return offset;
    }

    /**
     * Drops the carried remainder and the polarity. Only when the output is cut, never mid stream.
     */
    public void reset() {
        remainder = 0.5;
        state = false;
    }

    private int segment(short[] out, int offset, double length) {
        int n = (int) (length + remainder);
        remainder = length + remainder - n;
        state = !state;
        short target = state ? high : low;
        if (filterConst <= 0) {
            Arrays.fill(out, offset, offset + n, target);
        } else {
            // the transition sits on the segment boundary, so slew up to the target and mirror back
            double value = 0;
            int half = (n + 1) >> 1;
            for (int i = 0; i < half; i++) {
                value += filterConst * (target - value);
                short sample = (short) Math.round(value);
                out[offset + i] = sample;
                out[offset + n - i - 1] = sample;
            }
        }
        return offset + n;
    }
}
