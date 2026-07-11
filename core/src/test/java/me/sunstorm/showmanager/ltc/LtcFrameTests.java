package me.sunstorm.showmanager.ltc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LtcFrameTests {

    @Test
    @DisplayName("BCD fields and sync word land on the bits SMPTE 12M asks for")
    void testLayout() {
        LtcFrame frame = new LtcFrame();
        frame.set(1, 2, 3, 4, 25);

        assertThat(frame.data()).containsExactly(
                0x04, // bits 0..7   frame units = 4
                0x00, // bits 8..15  frame tens = 0
                0x03, // bits 16..23 sec units = 3
                0x00, // bits 24..31 sec tens = 0
                0x02, // bits 32..39 min units = 2
                0x00, // bits 40..47 min tens = 0
                0x01, // bits 48..55 hour units = 1
                0x00, // bits 56..63 hour tens = 0
                0xFC, // bits 64..71 sync word, first half
                0xBF  // bits 72..79 sync word, second half
        );
    }

    @Test
    @DisplayName("Every word holds an even number of ones")
    void testParity() {
        LtcFrame frame = new LtcFrame();
        for (int framerate : new int[]{24, 25, 30}) {
            for (int sec = 0; sec < 60; sec++) {
                for (int f = 0; f < framerate; f++) {
                    frame.set(23, 59, sec, f, framerate);
                    int ones = 0;
                    for (int bit = 0; bit < LtcFrame.BIT_COUNT; bit++)
                        if (frame.get(bit)) ones++;
                    assertThat(ones % 2).as("%d fps, frame %d", framerate, f).isZero();
                }
            }
        }
    }

    @Test
    @DisplayName("The parity bit moves to 59 at 25 fps")
    void testParityBit() {
        LtcFrame frame = new LtcFrame();
        // 3 frame units plus the 13 ones of the sync word is odd, so the parity bit has to be set
        frame.set(0, 0, 0, 3, 30);
        assertThat(frame.get(27)).isTrue();
        assertThat(frame.get(59)).isFalse();

        frame.set(0, 0, 0, 3, 25);
        assertThat(frame.get(59)).isTrue();
        assertThat(frame.get(27)).isFalse();
    }
}
