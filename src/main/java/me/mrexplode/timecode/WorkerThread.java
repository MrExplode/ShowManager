package me.mrexplode.timecode;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.swing.JOptionPane;

import com.illposed.osc.OSCMessage;

import ch.bildspur.artnet.ArtNetBuffer;
import ch.bildspur.artnet.ArtNetException;
import ch.bildspur.artnet.ArtNetServer;
import ch.bildspur.artnet.events.ArtNetServerEventAdapter;
import ch.bildspur.artnet.packets.ArtDmxPacket;
import ch.bildspur.artnet.packets.ArtNetPacket;
import ch.bildspur.artnet.packets.ArtTimePacket;
import ch.bildspur.artnet.packets.PacketType;
import lombok.Getter;
import me.mrexplode.timecode.eventsystem.EventBus;
import me.mrexplode.timecode.eventsystem.events.time.*;
import me.mrexplode.timecode.gui.general.SchedulerTableModel;
import me.mrexplode.timecode.ltc.LtcHandler;
import me.mrexplode.timecode.osc.OscHandler;
import me.mrexplode.timecode.remote.DmxRemoteControl;
import me.mrexplode.timecode.remote.OscRemoteControl;
import me.mrexplode.timecode.schedule.OSCDataType;
import me.mrexplode.timecode.schedule.ScheduledEvent;
import me.mrexplode.timecode.schedule.ScheduledOSC;
import me.mrexplode.timecode.util.Timecode;
import me.mrexplode.timecode.util.Utils;

public class WorkerThread implements Runnable {
    @Getter private static WorkerThread instance;
    @Getter private EventBus eventBus;
    @Getter private OscHandler oscHandler;
    @Getter private LtcHandler ltcHandler;
    
    //artnet
    private ArtNetServer server;
    private ArtTimePacket packet;
    private ArtNetBuffer artBuffer;
    private InetAddress artnetAddress;

    private SchedulerTableModel model = null;
    
    //main controls
    private boolean running = true;
    private boolean artnet = false;
    private boolean playLTC = false;
    private boolean sendOSC = false;
    private boolean playing = false;
    //start time of playing
    private long start = 0;
    private long elapsed = 0;
    /**
     * the timecode value, in milliseconds
     */
    private Timecode timecode;

    private DmxRemoteControl dmxRemote;
    private OscRemoteControl oscRemote;
    
    private static int framerate = 30;
    
    private Thread dataGrabberThread;
    private DataGrabber dataGrabber;
    private Object dataLock;
    
    public WorkerThread(Mixer mixer, InetAddress artnetAddress, SchedulerTableModel model, InetAddress oscAddress, int oscPort, Thread grabber, DataGrabber dataGrabber, Object dataLock) {
        this(mixer, artnetAddress, model, oscAddress, oscPort, grabber, dataGrabber, dataLock, null);
    }
    
    public WorkerThread(Mixer mixer, InetAddress artnetAddress, SchedulerTableModel model, InetAddress oscAddress, int oscPort, Thread grabber, DataGrabber dataGrabber, Object dataLock, ArtNetServer server) {
        instance = this;
        this.mixer = mixer;
        this.model = model;
        this.oscAddress = oscAddress;
        this.oscPort = oscPort;
        this.artnetAddress = artnetAddress;
        this.server = (server == null ? new ArtNetServer() : server);
        this.packet = new ArtTimePacket();
        this.artBuffer = new ArtNetBuffer();
        this.dataGrabberThread = grabber;
        this.dataGrabber = dataGrabber;
        this.dataLock = dataLock;
        this.timecode = new Timecode(0);
    }

    @Override
    public void run() {
        Thread.currentThread().setName("WorkerThread");
        running = true;
        artBuffer.clear();

        setupArtNet();
        try {
            ltcHandler.init();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        try {
            oscHandler.setup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        start = 0;
        long time = start;
        while (running) {
            final long current = System.currentTimeMillis();
            if (current >= time + (1000 / framerate)) {
                time = current;
                
                if (playing) {
                    elapsed = time - start;
                }
                //remote
                dmxRemote.handleData(artBuffer.getDmxData((short) dmxRemote.getAddress().getSubnet(), (short) dmxRemote.getAddress().getUniverse()));
                
                if (playing) {
                    timecode = new Timecode(elapsed, framerate);
                    packet.setTime(timecode.getHour(), timecode.getMin(), timecode.getSec(), timecode.getFrame());
                    ltcHandler.getGenerator().setTime(timecode.getHour(), timecode.getMin(), timecode.getSec(), timecode.getFrame());
                }
                
                if (dataGrabberThread.isAlive()) {
                    synchronized (dataLock) {
                        this.dataLock.notify();
                    }
                }
                
                dataGrabber.update();
                if (playing && sendOSC) {
                    List<ScheduledEvent> events = model.getCurrentFor(getCurrentTimecode());
                    if (events != null) {
                        events.forEach(scheduledEvent -> {
                            if (scheduledEvent instanceof ScheduledOSC && ((ScheduledOSC) scheduledEvent).isReady()) {
                                ScheduledOSC scheduledOSC = (ScheduledOSC) scheduledEvent;
                                OSCMessage packet = new OSCMessage(scheduledOSC.getPath(), Collections.singletonList(OSCDataType.castTo(scheduledOSC.getValue(), scheduledOSC.getDataType())));
                                oscHandler.sendOscPacket(packet);
                            }
                        });
                    }
                }
                
                if (artnet) {
                    server.broadcastPacket(packet);
                }
            } 
        }
    }

    private void setupArtNet() {
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

        //artnet node discovery reply packet
        Utils.setReplyPacket(server, artnetAddress);

        try {
            server.start(artnetAddress);
        } catch (SocketException | ArtNetException e) {
            e.printStackTrace();
        }
    }
    
    public Timecode getCurrentTimecode() {
        return timecode;
    }
    
    public String getFormatted() {
        return getCurrentTimecode().guiFormatted();
    }
    
    public void setTime(Timecode time) {
        TimecodeSetEvent event = new TimecodeSetEvent(time);
        event.call(eventBus);
        if (event.isCancelled())
            return;

        packet.setTime(time.getHour(), time.getMin(), time.getSec(), time.getFrame());
        ltcHandler.getGenerator().setTime(time.getHour(), time.getMin(), time.getSec(), time.getFrame());
        elapsed = time.millis();
        start = System.currentTimeMillis() - elapsed;
        this.timecode = time;
    }
    
    public void play() {
        TimecodeStartEvent event = new TimecodeStartEvent();
        event.call(eventBus);
        if (event.isCancelled())
            return;

        if (start == 0)
            start = System.currentTimeMillis();
        else
            start = System.currentTimeMillis() - elapsed;
        if (playLTC)
            ltcHandler.getGenerator().start();
        this.playing = true;
    }
    
    public boolean isPlaying() {
        return this.playing;
    }
    
    public void pause() {
        TimecodePauseEvent event = new TimecodePauseEvent();
        event.call(eventBus);
        if (event.isCancelled())
            return;
        this.playing = false;
        if (playLTC) {
            ltcHandler.getGenerator().stop();
        }
    }
    
    public void stop() {
        TimecodeStopEvent event = new TimecodeStopEvent();
        event.call(eventBus);
        if (event.isCancelled())
            return;
        this.playing = false;
        if (playLTC) {
            ltcHandler.getGenerator().setTime(0, 0, 0, 0);
            ltcHandler.getGenerator().stop();
        }
        timecode = new Timecode(0);
        start = 0;
    }
    
    public void shutdown() {
        running = false;
        ltcHandler.shutdown();
        server.stop();
    }
    
    private static void displayError(String errorMessage) {
        Thread t = new Thread(() -> {
            JOptionPane.showConfirmDialog(null, errorMessage, "Timecode Generator", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null);
        });
        t.setName("Error display thread");
        t.start();
    }

}