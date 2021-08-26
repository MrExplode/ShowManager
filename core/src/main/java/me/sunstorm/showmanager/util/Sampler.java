package me.sunstorm.showmanager.util;

import lombok.experimental.UtilityClass;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@UtilityClass
public class Sampler {

    public float[] sample(File audioFile) throws UnsupportedAudioFileException, IOException {
        float[] samples;

        AudioInputStream in = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(audioFile)));
        AudioFormat fmt = in.getFormat();

        if (fmt.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
            throw new UnsupportedAudioFileException("unsigned");
        }

        boolean big = fmt.isBigEndian();
        int chans = fmt.getChannels();
        int bits = fmt.getSampleSizeInBits();
        int bytes = bits + 7 >> 3;

        int frameLength = (int) in.getFrameLength();
        int bufferLength = chans * bytes * 1024;

        samples = new float[frameLength];
        byte[] buf = new byte[bufferLength];

        int i = 0;
        int bRead;
        while ( ( bRead = in.read(buf) ) > -1) {

            for (int b = 0; b < bRead;) {
                double sum = 0;

                // (sums to mono if multiple channels)
                for (int c = 0; c < chans; c++) {
                    if (bytes == 1) {
                        sum += buf[b++] << 8;

                    } else {
                        int sample = 0;

                        // (quantizes to 16-bit)
                        if (big) {
                            sample |= ( buf[b++] & 0xFF ) << 8;
                            sample |= ( buf[b++] & 0xFF );
                            b += bytes - 2;
                        } else {
                            b += bytes - 2;
                            sample |= ( buf[b++] & 0xFF );
                            sample |= ( buf[b++] & 0xFF ) << 8;
                        }

                        final int sign = 1 << 15;
                        final int mask = -1 << 16;
                        if ( ( sample & sign ) == sign) {
                            sample |= mask;
                        }

                        sum += sample;
                    }
                }

                samples[i++] = (float) ( sum / chans );
            }
        }
        in.close();

        return samples;
    }
}
