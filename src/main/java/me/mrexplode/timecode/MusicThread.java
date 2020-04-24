package me.mrexplode.timecode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import ch.bildspur.artnet.packets.ArtTimePacket;
import me.mrexplode.timecode.events.EventHandler;
import me.mrexplode.timecode.events.EventType;
import me.mrexplode.timecode.events.MarkerEvent;
import me.mrexplode.timecode.events.OscEvent;
import me.mrexplode.timecode.events.TimeChangeEvent;
import me.mrexplode.timecode.events.TimeEvent;
import me.mrexplode.timecode.events.TimeListener;
import me.mrexplode.timecode.fileio.Music;
import me.mrexplode.timecode.gui.TrackPanel;

public class MusicThread implements Runnable, TimeListener {
    
    private EventHandler eventHandler;
    private TrackPanel trackPanel;
    @SuppressWarnings("unused")
    private JLabel infoLabel;
    private Mixer mixer;
    private AudioInputStream audioStream;
    private AudioFormat format;
    private Clip currentClip;
    private List<Music> trackList;
    private Tracker tracker;
    private int played = 0;
    private boolean running = true;
    private boolean playing = false;
    private boolean enabled = false;
    private int framerate = 0;
    private int timeout = 0;
    private Object lock;
    
    public MusicThread(Mixer mixer, TrackPanel trackPanel, JLabel infoLabel, List<Music> musicList, int framerate, EventHandler eventHandler, Object lock) {
        this.mixer = mixer;
        this.trackPanel = trackPanel;
        this.infoLabel = infoLabel;
        this.trackList = musicList;
        this.framerate = framerate;
        timeout = 1000 / framerate;
        this.eventHandler = eventHandler;
        this.lock = lock;
    }

    @Override
    public void run() {
        log("Starting thread...");
        Thread.currentThread().setName("MusicThread");
        eventHandler.addListener(this);
        
        //preload first
        loadTrack(played);
        
        while (running) {
            if (currentClip != null && currentClip.isRunning()) {
                playing =  true;
                /*SwingUtilities.invokeLater(() -> {
                    infoLabel.setText("Current track: " + new File(trackList.get(played).file).getName());
                });*/
            } else {
                playing = false;
                /*SwingUtilities.invokeLater(() -> {
                    infoLabel.setText("Current track");
                });*/
                
            }
            if (playing) {
                int pos = currentClip.getFramePosition();
                int frameLength = currentClip.getFrameLength();
                double progress = (double) pos / frameLength * 1000;
                //trackPanel.setValue((int) Math.round(progress));
                progressBarUpdate(trackPanel, (int) Math.round(progress));
            }
            
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    err("Interrupted while waiting on lock! Please restart internals!");
                    e.printStackTrace();
                    shutdown();
                }
            }
        }
    }
    
    /**
     * 
     * @param volume volume between 0.0f and 1.0f
     */
    public void setVolume(float volume) {
        FloatControl floatControl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);
        floatControl.setValue(20f * (float) Math.log10(volume));
    }
    
    public void setEnabled(boolean value) {
        enabled = value;
    }
    
    private static void progressBarUpdate(TrackPanel panel, int val) {
        SwingUtilities.invokeLater(() -> {
            panel.setValue(val);
        });
    }
    
    @SuppressWarnings("resource")
    private void loadTrack(int index) {
        if (trackList.size() == 0)
            return;
        log("Loading file: " + trackList.get(index).file);
        try {
            trackPanel.setSamples(sampler(new File(trackList.get(index).file)));
        } catch (UnsupportedAudioFileException | IOException e) {
            displayError("Failed to sample the upcoming track: " + trackList.get(index).file + "\n" + e.getMessage());
            err("Failed sampling track");
            e.printStackTrace();
        }
        try {
            if (currentClip != null) {
                currentClip.stop();
                currentClip.close();
                currentClip = null;
            }
            InputStream in = new BufferedInputStream(new FileInputStream(new File(trackList.get(index).file)));
            audioStream = AudioSystem.getAudioInputStream(in);
            format = audioStream.getFormat();
            if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(), format.getSampleSizeInBits() * 2, format.getChannels(), format.getFrameSize() * 2, format.getFrameRate(), true); // big endian
                audioStream = AudioSystem.getAudioInputStream(format, audioStream);
            }
            SourceDataLine.Info sourceInfo = new DataLine.Info(Clip.class, format, ((int) audioStream.getFrameLength() * format.getFrameSize()));
            currentClip = (Clip) mixer.getLine(sourceInfo);
            currentClip.flush();
            currentClip.open(audioStream);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e1) {
            displayError("Failed to load the upcoming track: " + trackList.get(index).file + "\n" + e1.getMessage());
            err("Error loading track");
            e1.printStackTrace();
        }
        
        currentClip.addLineListener(e -> {
            if (e.getType() == LineEvent.Type.STOP) {
                if (tracker.isNaturalEnd()) {
                    currentClip.close();
                    currentClip = null;
                    played++;
                    if (played < trackList.size()) {
                        //when clip stops, load next
                        loadTrack(played);
                    }
                }
            }
        });
        
        int[] val = ArtTimePacket.decode(currentClip.getMicrosecondLength() / 1000 / timeout, toType(framerate));
        Timecode end = trackList.get(index).startingTime.add(new Timecode(val[0], val[1], val[2], val[3]), framerate);
        tracker = new Tracker(index, trackList.get(index).startingTime, end);
        
        log("Loaded file");
    }
    
    @SuppressWarnings("resource")
    private static float[] sampler(File audioFile) throws FileNotFoundException, UnsupportedAudioFileException, IOException {
        log("Sampling file: " + audioFile.getName());
        float[] samples = null;
        
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
    
    private static int toType(int framerate) {
        if (framerate == 24)
            return 0;
        if (framerate == 25)
            return 1;
        if (framerate == 30)
            return 3;
        return 0;
    }
    
    public void shutdown() {
        running = false;
        playing = false;
        if (currentClip != null) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
        }
        if (audioStream != null) {
            try {
                audioStream.close();
            } catch (IOException e) {
                err("Error during closing music stream, you can ignore this error.");
                e.printStackTrace();
            }
        }
        mixer.close();
        synchronized (lock) {
            lock.notify();
        }
    }
    
    @Override
    public void onTimeChangeEvent(TimeChangeEvent e) {
        if (enabled) {
            for (int i = 0; i < trackList.size(); i++) {
                if (trackList.get(i).startingTime.equals(e.getTime())) {
                    if (i == played) {
                        playing = true;
                        tracker.setnaturalEnd(true);
                        currentClip.start();
                        log("Start playing " + trackList.get(i).file);
                    }
                }
            }
        }
    }

    @Override
    public void onTimeEvent(TimeEvent e) {
        if (e.getType() == EventType.TC_START) {
            if (enabled && currentClip != null && tracker.inTrack(e.getValue())) {
                playing = true;
                tracker.setnaturalEnd(true);
                log("Continue playing " + trackList.get(played).file);
                currentClip.start();
            }
        }
        
        if (e.getType() == EventType.TC_STOP) {
            playing = false;
            trackPanel.setValue(0);
            if (currentClip != null)
                tracker.setnaturalEnd(false);
                currentClip.stop();
            played = 0;
            loadTrack(played);
        }
        
        if (e.getType() == EventType.TC_PAUSE) {
            playing = false;
            if (currentClip != null)
                tracker.setnaturalEnd(false);
                currentClip.stop();
        }
        
        if (e.getType() == EventType.TC_SET) {
            if (tracker.inTrack(e.getValue())) {
                currentClip.setMicrosecondPosition(e.getValue().subtract(tracker.getStart()).millis(framerate) * 1000);
            }
        }
    }

    @Override
    public void onOscEvent(OscEvent e) {
    }

    @Override
    public void onMarkerEvent(MarkerEvent e) {
    }
    
    private static void log(String msg) {
        System.out.println("[MusicThread] " + msg);
    }
    
    private static void err(String msg) {
        System.err.println("[MusicThread] " + msg);
    }
    
    private static void displayError(String errorMessage) {
        Thread t = new Thread(() -> {
            JOptionPane.showConfirmDialog(null, errorMessage, "Timecode Generator", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null);
        });
        t.setName("Error display thread");
        t.start();
    }

}
