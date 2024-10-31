package me.sunstorm.showmanager.util;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class Sampler {

    public static float[] sample(AudioInputStream in) throws UnsupportedAudioFileException, IOException {
        AudioFormat format = in.getFormat();
        if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
            throw new UnsupportedAudioFileException("unsigned");
        }

        int chans = format.getChannels();
        int bits = format.getSampleSizeInBits();
        int bytes = bits + 7 >> 3;
        int frameLength = (int) in.getFrameLength();
        int bufferLength = chans * bytes * 1024;

        float[] samples = new float[frameLength];
        byte[] buf = new byte[bufferLength];

        int i = 0;
        int bRead;
        while ((bRead = in.read(buf)) > -1) {
            for (int b = 0; b < bRead; ) {
                double sum = 0;
                // (sums to mono if multiple channels)
                for (int c = 0; c < chans; c++) {
                    if (bytes == 1) {
                        sum += buf[b++] << 8;
                    } else {
                        int sample = 0;
                        // (quantizes to 16-bit)
                        if (format.isBigEndian()) {
                            sample |= (buf[b++] & 0xFF) << 8;
                            sample |= (buf[b++] & 0xFF);
                            b += bytes - 2;
                        } else {
                            b += bytes - 2;
                            sample |= (buf[b++] & 0xFF);
                            sample |= (buf[b++] & 0xFF) << 8;
                        }
                        final int sign = 1 << 15;
                        final int mask = -1 << 16;
                        if ((sample & sign) == sign) {
                            sample |= mask;
                        }
                        sum += sample;
                    }
                }

                samples[i++] = (float) (sum / chans);
            }
        }
        in.close();
        return samples;
    }
}
