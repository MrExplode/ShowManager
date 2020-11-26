package me.mrexplode.timecode.util;

import ch.bildspur.artnet.ArtNetServer;
import ch.bildspur.artnet.PortDescriptor;
import ch.bildspur.artnet.packets.ArtPollReplyPacket;
import lombok.experimental.UtilityClass;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.io.*;
import java.net.InetAddress;

@UtilityClass
public class Utils {

    public void displayError(String errorMessage) {
        Thread t = new Thread(() -> JOptionPane.showConfirmDialog(null, errorMessage, "Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null));
        t.setName("Error display thread");
        t.start();
    }

    public void setReplyPacket(ArtNetServer server, InetAddress ip) {
        ArtPollReplyPacket replyPacket = new ArtPollReplyPacket();
        replyPacket.setIp(ip);
        replyPacket.setShortName("TimecodeGen Node");
        replyPacket.setLongName("Timecode Generator Node by MrExplode");
        replyPacket.setVersionInfo(1);
        replyPacket.setSubSwitch(1);
        replyPacket.setOemCode(5);
        PortDescriptor port = new PortDescriptor();
        port.setCanInput(true);
        port.setCanOutput(true);
        replyPacket.setPorts(new PortDescriptor[] {port});

        replyPacket.translateData();
        server.setDefaultReplyPacket(replyPacket);
    }

    public float[] sampler(File audioFile) throws UnsupportedAudioFileException, IOException {
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
