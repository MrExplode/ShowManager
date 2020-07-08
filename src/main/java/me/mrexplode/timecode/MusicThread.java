package me.mrexplode.timecode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
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

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCSerializeException;
import com.illposed.osc.transport.udp.OSCPortOut;

import me.mrexplode.timecode.events.EventHandler;
import me.mrexplode.timecode.events.EventType;
import me.mrexplode.timecode.events.MarkerEvent;
import me.mrexplode.timecode.events.MusicEvent;
import me.mrexplode.timecode.events.OscEvent;
import me.mrexplode.timecode.events.TimeChangeEvent;
import me.mrexplode.timecode.events.TimeEvent;
import me.mrexplode.timecode.events.TimeListener;
import me.mrexplode.timecode.fileio.Music;
import me.mrexplode.timecode.gui.general.TrackPanel;

public class MusicThread implements Runnable, TimeListener {
    
    private EventHandler eventHandler;
    private TrackPanel trackPanel;
    private Networking net;
    @SuppressWarnings("unused")
    private JLabel infoLabel;
    private Mixer mixer;
    private AudioInputStream audioStream;
    private AudioFormat format;
    private Clip currentClip;
    private List<Music> trackList;
    private Tracker tracker;
    private int played = 0;
    private int maxSegmentSize;
    private boolean running = true;
    private boolean playing = false;
    private boolean enabled = false;
    private Object lock;
    private OSCPortOut oscOut;
    
    public MusicThread(Mixer mixer, TrackPanel trackPanel, JLabel infoLabel, List<Music> musicList, EventHandler eventHandler, Object lock, int com1Port, int com2Port, InetAddress com2Addr, int maxSegmentSize) {
        this.mixer = mixer;
        this.trackPanel = trackPanel;
        this.infoLabel = infoLabel;
        this.trackList = musicList;
        this.eventHandler = eventHandler;
        this.lock = lock;
        this.net = new Networking(com2Addr, com2Port);
        try {
            this.oscOut = new OSCPortOut(InetAddress.getByName("255.255.255.255"), com1Port);
        } catch (IOException e) {
            err("Failed to start osc sender");
            e.printStackTrace();
        }
        this.maxSegmentSize = maxSegmentSize;
    }

    @Override
    public void run() {
        log("Starting thread...");
        Thread.currentThread().setName("MusicThread");
        eventHandler.addListener(this);
        
        //preload first
        loadTrack(played);
        
        long time = 0;
        while (running) {
            //slow it down. the lock gets notified on every frame change, and thats a bit too fast just to update the progressbar
            if (System.currentTimeMillis() >= time + 100) {
                time = System.currentTimeMillis();
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
                    int rounded = (int) Math.round(progress);
                    try {
                        oscOut.send(new OSCMessage("/timecode/musicplayer/progress", Collections.singletonList("" + (int) Math.round(progress))));
                    } catch (IOException | OSCSerializeException e) {
                        err("Failed to send progress update over network");
                        e.printStackTrace();
                    }
                    progressBarUpdate(trackPanel, rounded);
                }
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
    
    public Tracker getTracker() {
        return this.tracker;
    }
    
    private static void progressBarUpdate(TrackPanel panel, int val) {
        SwingUtilities.invokeLater(() -> {
            panel.setValue(val);
            panel.repaint();
        });
    }
    
    @SuppressWarnings("resource")
    private void loadTrack(int index) {
        if (trackList.size() == 0)
            return;
        log("Loading file: " + trackList.get(index).file);
        long time = System.currentTimeMillis();
        long sampleTime = time;
        float[] samples = null;
        try {
            samples = sampler(new File(trackList.get(index).file));
            trackPanel.setSamples(samples);
            sampleTime = System.currentTimeMillis() - sampleTime;
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
        
        Timecode end = trackList.get(index).startingTime.add(new Timecode(currentClip.getMicrosecondLength() / 1000));
        tracker = new Tracker(index, trackList.get(index).startingTime, end, false);
        
        long netTime = System.currentTimeMillis();
        ArrayList<ArraySegment> segments = (ArrayList<ArraySegment>) Sequencer.sequence(samples, maxSegmentSize);
        for (int i = 0; i < segments.size(); i++) {
            ArraySegment segment = segments.get(i);
            try {
                float[] transportData = new float[2 + segment.getData().length];
                transportData[0] = segment.getId();
                transportData[1] = segment.getMax();
                System.arraycopy(segment.getData(), 0, transportData, 2, segment.getData().length);
                net.broadcastData(transportData);
            } catch (IOException e1) {
                err("Failed to send waveform sequence " + segment.getId() + " of " + segment.getMax());
                e1.printStackTrace();
            }
        }
        netTime = System.currentTimeMillis() - netTime;
        
        MusicEvent event = new MusicEvent(EventType.MUSIC_LOAD, samples, trackList.get(index));
        DataGrabber.getEventHandler().callEvent(event);
        
        log("Loaded file in " + (System.currentTimeMillis() - time) + " ms, sampling took " + sampleTime + " ms, networking took " + netTime + " ms");
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
        net.shutdown();
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
                        DataGrabber.getEventHandler().callEvent(new MusicEvent(EventType.MUSIC_START, null, trackList.get(played)));
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
                DataGrabber.getEventHandler().callEvent(new MusicEvent(EventType.MUSIC_START, null, trackList.get(played)));
                currentClip.start();
            }
        }
        
        if (e.getType() == EventType.TC_STOP) {
            playing = false;
            trackPanel.setValue(0);
            if (currentClip != null) {
                tracker.setnaturalEnd(false);
                currentClip.stop();
                DataGrabber.getEventHandler().callEvent(new MusicEvent(EventType.MUSIC_STOP, null, trackList.get(played)));
            }
            played = 0;
            loadTrack(played);
        }
        
        if (e.getType() == EventType.TC_PAUSE) {
            playing = false;
            if (currentClip != null) {
                tracker.setnaturalEnd(false);
                currentClip.stop();
                DataGrabber.getEventHandler().callEvent(new MusicEvent(EventType.MUSIC_PAUSE, null, trackList.get(played)));
            }
        }
        
        if (e.getType() == EventType.TC_SET) {
            if (tracker.inTrack(e.getValue())) {
                currentClip.setMicrosecondPosition(e.getValue().subtract(tracker.getStart()).millis() * 1000);
            } else {
                boolean preloaded = true;
                for (int i = 0; i < trackList.size(); i++) {
                    Timecode start = trackList.get(i).startingTime;
                    Timecode end = start.add(new Timecode(trackList.get(i).length));
                    Timecode current = e.getValue();
                    played = i;
                    if (current.compareTo(start) >= 0 && current.compareTo(end) <= 0) {
                        preloaded = false;
                        loadTrack(played);
                        currentClip.setMicrosecondPosition(current.subtract(start).millis() * 1000);
                        break;
                    }
                }
                if (preloaded)
                    loadTrack(played);
            }
        }
    }

    @Override
    public void onOscEvent(OscEvent e) {
        //unused event
    }

    @Override
    public void onMarkerEvent(MarkerEvent e) {
        //unused event
    }
    
    @Override
    public void onMusicEvent(MusicEvent e) {
        if (e.getType() == EventType.MUSIC_START) {
            trackPanel.setColor(TrackPanel.playColor);
        }
        if (e.getType() == EventType.MUSIC_PAUSE || e.getType() == EventType.MUSIC_STOP) {
            trackPanel.setColor(TrackPanel.pauseColor);
        }
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
