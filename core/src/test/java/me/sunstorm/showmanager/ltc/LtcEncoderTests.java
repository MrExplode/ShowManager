package me.sunstorm.showmanager.ltc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LtcEncoderTests {

    @ParameterizedTest
    @CsvSource({"48000, 24, 2000", "48000, 25, 1920", "48000, 30, 1600", "96000, 25, 3840"})
    @DisplayName("Frames that divide evenly are always the exact same length")
    void testExactFrameLength(int sampleRate, int framerate, int expected) {
        LtcEncoder encoder = new LtcEncoder(sampleRate, framerate, -6, 0);
        short[] buffer = new short[encoder.maxFrameSamples()];
        for (int f = 0; f < framerate; f++)
            assertThat(encoder.encode(0, 0, 0, f, buffer)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"44100, 24", "44100, 25", "44100, 30", "48000, 25"})
    @DisplayName("The carried remainder keeps uneven rates drift free over a minute")
    void testNoDrift(int sampleRate, int framerate) {
        LtcEncoder encoder = new LtcEncoder(sampleRate, framerate, -6, 0);
        short[] buffer = new short[encoder.maxFrameSamples()];
        long total = 0;
        for (int f = 0; f < framerate * 60; f++)
            total += encoder.encode(0, 0, f / framerate, f % framerate, buffer);
        // a minute of timecode is a minute of audio, give or take the sample we are mid-way through
        assertThat(total).isCloseTo(sampleRate * 60L, org.assertj.core.data.Offset.offset(1L));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, 40})
    @DisplayName("Encoded audio decodes back to the timecode that went in")
    void testRoundTrip(double riseTime) {
        int sampleRate = 48000;
        int framerate = 25;
        LtcEncoder encoder = new LtcEncoder(sampleRate, framerate, -6, riseTime);
        short[] buffer = new short[encoder.maxFrameSamples()];

        List<int[]> written = new ArrayList<>();
        List<Short> signal = new ArrayList<>();
        // 10 seconds across a minute boundary
        for (int i = 0; i < framerate * 10; i++) {
            int frame = i % framerate;
            int sec = 59 + i / framerate;
            int[] tc = {1, 2 + sec / 60, sec % 60, frame};
            written.add(tc);
            int samples = encoder.encode(tc[0], tc[1], tc[2], tc[3], buffer);
            for (int s = 0; s < samples; s++)
                signal.add(buffer[s]);
        }

        List<int[]> decoded = decode(signal, sampleRate, framerate);
        // the last frame is cut off, the decoder needs the transition that follows its last bit
        assertThat(decoded).hasSizeGreaterThanOrEqualTo(written.size() - 1);
        for (int i = 0; i < decoded.size(); i++)
            assertThat(decoded.get(i)).containsExactly(written.get(i));
    }

    /**
     * Reference decoder: recovers the transitions of the biphase mark signal, turns them back into
     * bits, then reads the timecode out of every word that follows a sync word.
     */
    private static List<int[]> decode(List<Short> signal, int sampleRate, int framerate) {
        double samplesPerBit = (double) sampleRate / (framerate * LtcFrame.BIT_COUNT);
        int threshold = Short.MAX_VALUE / 8;

        List<Integer> transitions = new ArrayList<>();
        Boolean high = null;
        for (int i = 0; i < signal.size(); i++) {
            short value = signal.get(i);
            if (high == null && Math.abs(value) > threshold) {
                high = value > 0;
                transitions.add(i);
            } else if (high != null && (high ? value < -threshold : value > threshold)) {
                high = !high;
                transitions.add(i);
            }
        }

        List<Boolean> bits = new ArrayList<>();
        for (int i = 1; i < transitions.size() - 1; i++) {
            int length = transitions.get(i) - transitions.get(i - 1);
            if (length > samplesPerBit * 0.75) {
                bits.add(false);
            } else {
                // a one is two half length cells, consume the second one as well
                assertThat(transitions.get(i + 1) - transitions.get(i)).isLessThan((int) (samplesPerBit * 0.75));
                bits.add(true);
                i++;
            }
        }

        List<int[]> frames = new ArrayList<>();
        for (int i = 0; i + LtcFrame.BIT_COUNT <= bits.size(); i++) {
            if (!isSyncWord(bits, i + 64))
                continue;
            frames.add(new int[]{
                    bcd(bits, i + 48, 4) + bcd(bits, i + 56, 2) * 10,
                    bcd(bits, i + 32, 4) + bcd(bits, i + 40, 3) * 10,
                    bcd(bits, i + 16, 4) + bcd(bits, i + 24, 3) * 10,
                    bcd(bits, i, 4) + bcd(bits, i + 8, 2) * 10
            });
            i += LtcFrame.BIT_COUNT - 1;
        }
        return frames;
    }

    private static boolean isSyncWord(List<Boolean> bits, int offset) {
        boolean[] sync = {false, false, true, true, true, true, true, true, true, true, true, true, true, true, false, true};
        for (int i = 0; i < sync.length; i++)
            if (bits.get(offset + i) != sync[i])
                return false;
        return true;
    }

    private static int bcd(List<Boolean> bits, int offset, int length) {
        int value = 0;
        for (int i = 0; i < length; i++)
            if (bits.get(offset + i))
                value |= 1 << i;
        return value;
    }
}
