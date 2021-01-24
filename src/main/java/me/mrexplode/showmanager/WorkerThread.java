package me.mrexplode.showmanager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.LineUnavailableException;

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
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.mrexplode.showmanager.eventsystem.EventBus;
import me.mrexplode.showmanager.eventsystem.events.time.*;
import me.mrexplode.showmanager.gui.general.SchedulerTableModel;
import me.mrexplode.showmanager.ltc.LtcHandler;
import me.mrexplode.showmanager.osc.OscHandler;
import me.mrexplode.showmanager.remote.DmxRemoteControl;
import me.mrexplode.showmanager.remote.OscRemoteControl;
import me.mrexplode.showmanager.schedule.OSCDataType;
import me.mrexplode.showmanager.schedule.ScheduledEvent;
import me.mrexplode.showmanager.schedule.ScheduledOSC;
import me.mrexplode.showmanager.util.Timecode;
import me.mrexplode.showmanager.util.Utils;

@Slf4j
@Getter
public class WorkerThread implements Runnable {
    @Getter private static WorkerThread instance;
    private final EventBus eventBus;
    private final OscHandler oscHandler;
    private final LtcHandler ltcHandler;
    
    //artnet
    private final ArtNetServer server;
    private final ArtTimePacket packet;
    private final ArtNetBuffer artBuffer;
    private final InetAddress artnetAddress;

    private SchedulerTableModel model;
    
    //main controls
    private boolean running = true;
    @Setter private boolean artnet = false;
    @Setter private boolean playLTC = false;
    @Setter private boolean sendOSC = false;
    private boolean playing = false;
    //start time of playing
    private long start = 0;
    private long elapsed = 0;

    private Timecode timecode;

    private final DmxRemoteControl dmxRemote;
    private final OscRemoteControl oscRemote;
    
    private final int framerate;
    
    public WorkerThread(OscHandler oscHandler, LtcHandler ltcHandler, SchedulerTableModel model, ArtNetServer server, InetAddress artnetAddress, int framerate) {
        instance = this;
        this.eventBus = new EventBus();
        this.oscHandler = oscHandler;
        this.ltcHandler = ltcHandler;
        this.model = model;
        this.artnetAddress = artnetAddress;
        this.server = (server == null ? new ArtNetServer() : server);
        this.framerate = framerate;
        packet = new ArtTimePacket();
        artBuffer = new ArtNetBuffer();
        timecode = new Timecode(0);
        dmxRemote = new DmxRemoteControl();
        oscRemote = new OscRemoteControl();
    }

    @Override
    @SneakyThrows(value = {InterruptedException.class})
    public void run() {
        Thread.currentThread().setName("WorkerThread");
        log.info("Starting...");
        running = true;
        artBuffer.clear();

        setupArtNet();
        try {
            ltcHandler.init();
        } catch (LineUnavailableException e) {
            log.error("Failed to initialize LTC handler", e);
        }
        try {
            oscHandler.setup();
        } catch (IOException e) {
            log.error("Failed to initialize OSC handler", e);
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

                if (playing && sendOSC) {
                    List<ScheduledEvent> events = model.getCurrentFor(getTimecode());
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

            //slowing down the loop
            if (playing)
                Thread.sleep(1);
            else
                Thread.sleep(10);
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
}