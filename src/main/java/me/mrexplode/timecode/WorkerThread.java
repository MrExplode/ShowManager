package me.mrexplode.timecode;

import java.net.SocketException;

import ch.bildspur.artnet.ArtNetBuffer;
import ch.bildspur.artnet.ArtNetException;
import ch.bildspur.artnet.ArtNetServer;
import ch.bildspur.artnet.events.ArtNetServerEventAdapter;
import ch.bildspur.artnet.packets.ArtDmxPacket;
import ch.bildspur.artnet.packets.ArtNetPacket;
import ch.bildspur.artnet.packets.ArtTimePacket;
import ch.bildspur.artnet.packets.PacketType;

public class WorkerThread implements Runnable {
    
    private ArtNetServer server;
    private ArtTimePacket packet;
    private ArtNetBuffer artBuffer;
    
    private boolean running = true;
    private boolean broadcast = false;
    private boolean playing = false;
    private long start = 0;
    
    private boolean remote = false;
    private RemoteState remoteState;
    private int dmxAddress;
    private int universe;
    private int subnet;
    
    private int framerate = 30;
    
    private Thread dataGrabberThread;
    
    public WorkerThread(Thread grabber) {
        this(grabber, null);
    }
    
    public WorkerThread(Thread grabber, ArtNetServer server) {
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
                if (packet.getType() != PacketType.ART_TIMECODE)
                    return;
                
                ArtDmxPacket dmxPacket = (ArtDmxPacket) packet;
                int subnet = dmxPacket.getSubnetID();
                int universe = dmxPacket.getUniverseID();

                artBuffer.setDmxData((short) subnet, (short) universe, dmxPacket.getDmxData());
            }
        });
        
        try {
            server.start();
        } catch (SocketException | ArtNetException e) {
            System.err.println("Failed to start ArtNet server. Shutting down WorkerThread...");
            e.printStackTrace();
            return;
        }
        
        start = System.currentTimeMillis();
        long time = start;
        while (running) {
            long current = System.currentTimeMillis();
            if (current >= time + (1000 / framerate)) {
                time = current;
                
                if (remote) {
                    byte[] data = artBuffer.getDmxData((short) subnet, (short) universe);
                    switch (data[dmxAddress]) {
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
                    packet.setFrameNumber((time - start) / (1000 / framerate));
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
        if (start == 0) {
            start = System.currentTimeMillis();
        }
        this.playing = true;
    }
    
    public boolean isPlaying() {
        return this.playing;
    }
    
    public void pause() {
        this.playing = false;
    }
    
    public void stop() {
        this.playing = false;
        packet.setFrameNumber(0);
        start = 0;
    }
    
    public void shutdown() {
        running = false;
    }
    
    public void setBroadcast(boolean value) {
        this.broadcast = value;
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

}
