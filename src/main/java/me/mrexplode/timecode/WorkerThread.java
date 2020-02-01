package me.mrexplode.timecode;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JOptionPane;

import ch.bildspur.artnet.ArtNetBuffer;
import ch.bildspur.artnet.ArtNetException;
import ch.bildspur.artnet.ArtNetServer;
import ch.bildspur.artnet.events.ArtNetServerEventAdapter;
import ch.bildspur.artnet.packets.ArtDmxPacket;
import ch.bildspur.artnet.packets.ArtNetPacket;
import ch.bildspur.artnet.packets.ArtTimePacket;
import ch.bildspur.artnet.packets.PacketType;

public class WorkerThread implements Runnable {
    
    //artnet
    private ArtNetServer server;
    private ArtTimePacket packet;
    private ArtNetBuffer artBuffer;
    private InetAddress networkAddress;
    
    //ltc
    private AudioInputStream stream = null;
    private AudioFormat format = null; 
    private Mixer mixer = null;
    private Clip clip = null;
    
    //main controls
    private boolean running = true;
    private boolean broadcast = false;
    private boolean playLTC = false;
    private boolean playing = false;
    //start time of playing
    private long start = 0;
    private long elapsed = 0;
    
    //remote
    private boolean remote = false;
    private RemoteState remoteState;
    private int dmxAddress = 1;
    private int universe = 0;
    private int subnet = 0;
    
    private int framerate = 30;
    
    private Thread dataGrabberThread;
    
    public WorkerThread(AudioInputStream stream, Mixer mixer, InetAddress address, Thread grabber) {
        this(stream, mixer, address, grabber, null);
    }
    
    public WorkerThread(AudioInputStream stream, Mixer mixer, InetAddress address, Thread grabber, ArtNetServer server) {
        this.stream = stream;
        this.mixer = mixer;
        this.format = stream.getFormat();
        this.networkAddress = address;
        this.server = (server == null ? new ArtNetServer() : server);
        this.packet = new ArtTimePacket();
        this.artBuffer = new ArtNetBuffer();
        this.remoteState = RemoteState.IDLE;
        this.dataGrabberThread = grabber;
        packet.setFrameNumber(0);
    }

    @Override
    public void run() {
        Thread.currentThread().setName("WorkerThread");
        running = true;
        artBuffer.clear();
        server.addListener(new ArtNetServerEventAdapter() {
            @Override
            public void artNetPacketReceived(ArtNetPacket packet) {
                if (packet.getType() != PacketType.ART_OUTPUT)
                    return;
                
                ArtDmxPacket dmxPacket = (ArtDmxPacket) packet;
                int subnet = dmxPacket.getSubnetID();
                int universe = dmxPacket.getUniverseID();

                artBuffer.setDmxData((short) subnet, (short) universe, dmxPacket.getDmxData());
            }
        });
        
        try {
            server.start(networkAddress);
        } catch (SocketException | ArtNetException e) {
            System.err.println("Failed to start ArtNet server");
            displayError("Failed to start ArtNetServer: " + e.getMessage() + "\n Please restart the internals!");
            e.printStackTrace();
        }
        
        //setup the ltc
        if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
            format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(), format.getSampleSizeInBits() * 2, format.getChannels(), format.getFrameSize() * 2, format.getFrameRate(), true); // big endian
            stream = AudioSystem.getAudioInputStream(format, stream);
        }
        SourceDataLine.Info sourceInfo = new DataLine.Info(Clip.class, format, ((int) stream.getFrameLength() * format.getFrameSize()));
        try {
            clip = (Clip) mixer.getLine(sourceInfo);
            clip.flush();
            clip.open(stream);
        } catch (LineUnavailableException e) {
            System.err.println("Failed to access specified audio output");
            displayError("Failed to access specified audio output: " + e.getMessage() + "\n Please restart the internals!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Failed to read LTC source");
            displayError("Failed to read LTC source file: " + e.getMessage() + "\n Please restart the internals!");
            e.printStackTrace();
        }
        
        start = 0;
        long time = start;
        while (running) {
            long current = System.currentTimeMillis();
            if (current >= time + (1000 / framerate)) {
                time = current;
                
                if (playing) {
                    System.out.println("inc elapsed");
                    elapsed = time - start;
                }
                if (remote) {
                    byte[] data = artBuffer.getDmxData((short) subnet, (short) universe);
                    switch (data[dmxAddress - 1]) {
                        default:
                            remoteState = RemoteState.IDLE;
                            break;
                        case 25:
                            remoteState = RemoteState.FORCE_IDLE;
                            break;
                        case 51:
                            remoteState = RemoteState.PLAYING;
                            play();
                            break;
                        case 76:
                            remoteState = RemoteState.PAUSE;
                            pause();
                            break;
                        case 102:
                            remoteState = RemoteState.STOPPED;  
                            stop();
                            break;
                    }
                } else {
                    remoteState = RemoteState.DISABLED;
                }
                
                if (playing) {
                    packet.setFrameNumber(elapsed / (1000 / framerate));
                }
                
                if (broadcast) {
                    server.broadcastPacket(packet);
                }
                
                if (dataGrabberThread.isAlive()) {
                    synchronized (this.dataGrabberThread) {
                        this.dataGrabberThread.notify();
                    }
                }
            }
            
        }
    }
    
    public String getCurrentTime() {
        int[] var = packet.decode(packet.encoded, packet.getFrameType());
        return (var[0] < 10 ? "0" + var[0] : "" + var[0]) + " : " + (var[1] < 10 ? "0" + var[1] : "" + var[1]) + " : " + (var[2] < 10 ? "0" + var[2] : "" + var[2]) + " / " + (var[3] < 10 ? "0" + var[3] : "" + var[3]);
    }
    
    public void play() {
        //starting first
        if (start == 0) {
            start = System.currentTimeMillis();
        } else {
            start = System.currentTimeMillis() - elapsed;
        }
        if (playLTC) {
            clip.start();
        }
        this.playing = true;
    }
    
    public boolean isPlaying() {
        return this.playing;
    }
    
    public void pause() {
        this.playing = false;
        if (playLTC) {
            this.clip.stop();
        }
    }
    
    public void stop() {
        this.playing = false;
        if (playLTC) {
            clip.setFramePosition(0);
            clip.stop();
        }
        packet.setFrameNumber(0);
        start = 0;
    }
    
    public void shutdown() {
        System.out.println("Shutting down WorkThread...");
        running = false;
        clip.stop();
        //clip.flush();
        clip.close();
        clip = null;
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stream = null;
        mixer.close();
        mixer = null;
        server.stop();
    }
    
    public void setBroadcast(boolean value) {
        this.broadcast = value;
    }
    
    public boolean setLTC(boolean value) {
        if (clip != null) {
            this.playLTC = value;
            if (value) {
                //start with the actual position
                clip.setMicrosecondPosition(elapsed * 1000);
                clip.start();
            } else {
                //stop the running clip
                clip.stop();
            }
            return true;
        } else {
            return false;
        }
    }
    
    public void setFramerate(int framerate) {
        if (framerate == 24 || framerate == 25 || framerate == 30) {
            this.framerate = framerate;
        } else {
            throw new IllegalArgumentException("Not valid framerate! Framerate must be 24, 25 or 30");
        }
    }
    
    public void setRemoteControl(boolean mode) {
        this.remote = mode;
    }
    
    public RemoteState getRemoteState() {
        return remoteState;
    }
    
    public int getDmxAddress() {
        return dmxAddress;
    }

    
    public void setDmxAddress(int dmxAddress) {
        if (dmxAddress < 1 || dmxAddress > 512) {
            throw new IllegalArgumentException("Dmx address must be between 1 and 512");
        } else {
            this.dmxAddress = dmxAddress;
        }
    }

    
    public int getUniverse() {
        return universe;
    }

    
    public void setUniverse(int universe) {
        this.universe = universe;
    }

    
    public int getSubnet() {
        return subnet;
    }

    
    public void setSubnet(int subnet) {
        this.subnet = subnet;
    }
    
    private static void displayError(String errorMessage) {
        Thread t = new Thread(() -> {
            JOptionPane.showConfirmDialog(null, errorMessage, "Timecode Generator", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null);
        });
        t.setName("Error display thread");
        t.start();
    }

}
