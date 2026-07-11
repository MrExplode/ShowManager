package me.sunstorm.showmanager.ltc;

import java.util.Arrays;

/**
 * A single 80 bit SMPTE 12M / EBU 3097 timecode word, packed into 10 bytes LSB first: bit 0 is the
 * low bit of byte 0 and goes out first. BCD fields are LSB first too.
 * <p>
 * EBU (25 fps) and SMPTE (24, 30 fps) disagree on where these land:
 * <pre>
 *            25 fps   24, 30 fps
 *  BGF0        27         43
 *  BGF1        58         58
 *  BGF2        43         59
 *  parity      59         27
 * </pre>
 * Only the parity bit is written, the group flags and user bits stay zero.
 */
public final class LtcFrame {
    public static final int BIT_COUNT = 80;
    public static final int BYTE_COUNT = BIT_COUNT / 8;

    private static final int FRAME_UNITS = 0;
    private static final int FRAME_TENS = 8;
    private static final int SEC_UNITS = 16;
    private static final int SEC_TENS = 24;
    private static final int MIN_UNITS = 32;
    private static final int MIN_TENS = 40;
    private static final int HOUR_UNITS = 48;
    private static final int HOUR_TENS = 56;

    // sync word 0011111111111101 in bits 64..79, LSB first
    private static final byte SYNC_LOW = (byte) 0xFC;
    private static final byte SYNC_HIGH = (byte) 0xBF;

    private final byte[] data = new byte[BYTE_COUNT];

    public void set(int hour, int min, int sec, int frame, int framerate) {
        Arrays.fill(data, (byte) 0);
        writeBcd(FRAME_UNITS, frame % 10, 4);
        writeBcd(FRAME_TENS, frame / 10, 2);
        writeBcd(SEC_UNITS, sec % 10, 4);
        writeBcd(SEC_TENS, sec / 10, 3);
        writeBcd(MIN_UNITS, min % 10, 4);
        writeBcd(MIN_TENS, min / 10, 3);
        writeBcd(HOUR_UNITS, hour % 10, 4);
        writeBcd(HOUR_TENS, hour / 10, 2);
        data[8] = SYNC_LOW;
        data[9] = SYNC_HIGH;
        // even number of zeros per word, so every word starts on the same polarity. 80 bits, so
        // even zeros is the same as even ones
        setBit(parityBit(framerate), countOnes() % 2 == 1);
    }

    public boolean get(int bit) {
        return (data[bit >> 3] & (1 << (bit & 7))) != 0;
    }

    /**
     * @return the backing 10 bytes, not a copy
     */
    public byte[] data() {
        return data;
    }

    static int parityBit(int framerate) {
        return framerate == 25 ? 59 : 27;
    }

    private void writeBcd(int offset, int value, int bits) {
        for (int i = 0; i < bits; i++)
            setBit(offset + i, (value & (1 << i)) != 0);
    }

    private void setBit(int bit, boolean value) {
        if (value)
            data[bit >> 3] |= (byte) (1 << (bit & 7));
        else
            data[bit >> 3] &= (byte) ~(1 << (bit & 7));
    }

    private int countOnes() {
        int count = 0;
        for (byte b : data)
            count += Integer.bitCount(b & 0xFF);
        return count;
    }
}
