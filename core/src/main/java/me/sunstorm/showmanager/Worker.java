package me.sunstorm.showmanager;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.artnet.ArtNetHandler;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.events.time.*;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;
import me.sunstorm.showmanager.ltc.LtcHandler;
import me.sunstorm.showmanager.remote.DmxRemoteControl;
import me.sunstorm.showmanager.terminable.Terminable;
import me.sunstorm.showmanager.util.Timecode;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
public class Worker implements Runnable, Terminable, InjectRecipient {
    @Inject
    private EventBus eventBus;
    @Inject
    private LtcHandler ltcHandler;
    private final DmxRemoteControl dmxRemote;
    private final ArtNetHandler artNetHandler;
    private boolean running = true;
    @Setter private boolean artNet = false;
    @Setter private boolean ltc = false;
    @Setter private boolean osc = false;
    private boolean playing = false;
    private long start = 0;
    private long elapsed = 0;
    private Timecode currentTime = new Timecode(0);
    private final int framerate;
    
    public Worker(InetAddress artNetAddress, int framerate) {
        register();
        inject();
        this.framerate = framerate;
        artNetHandler = new ArtNetHandler(artNetAddress);
        dmxRemote = new DmxRemoteControl();
    }

    @Override
    @SneakyThrows(value = {InterruptedException.class})
    public void run() {
        log.info("Starting...");
        running = true;
        start = 0;
        long time = start;
        while (running) {
            final long current = System.currentTimeMillis();
            if (current >= time + (1000 / framerate)) {
                time = current;
                if (playing) {
                    elapsed = time - start;
                }
                dmxRemote.handleData(artNetHandler.getData(dmxRemote.getAddress().getSubnet(), dmxRemote.getAddress().getUniverse()));
                
                if (playing) {
                    currentTime.set(elapsed, framerate);
                    artNetHandler.setTime(currentTime);
                    ltcHandler.getGenerator().setTime(currentTime.getHour(), currentTime.getMin(), currentTime.getSec(), currentTime.getFrame());
                    TimecodeChangeEvent changeEvent = new TimecodeChangeEvent(currentTime);
                    changeEvent.call(eventBus);
                }
                
                if (artNet) {
                    artNetHandler.broadcast();
                }
            }

            //slowing down the loop
            if (playing)
                TimeUnit.MILLISECONDS.sleep(1);
            else
                TimeUnit.MILLISECONDS.sleep(10);
        }
    }
    
    public void setTime(Timecode time) {
        TimecodeSetEvent event = new TimecodeSetEvent(time);
        event.call(eventBus);
        if (event.isCancelled())
            return;

        artNetHandler.setTime(time);
        ltcHandler.getGenerator().setTime(time.getHour(), time.getMin(), time.getSec(), time.getFrame());
        elapsed = time.millis();
        start = System.currentTimeMillis() - elapsed;
        this.currentTime = time;
    }
    
    public void play() {
        log.info("Play");
        TimecodeStartEvent event = new TimecodeStartEvent(currentTime);
        event.call(eventBus);
        if (event.isCancelled())
            return;

        if (start == 0)
            start = System.currentTimeMillis();
        else
            start = System.currentTimeMillis() - elapsed;
        if (ltc)
            ltcHandler.getGenerator().start();
        this.playing = true;
    }
    
    public void pause() {
        log.info("Pause");
        TimecodePauseEvent event = new TimecodePauseEvent();
        event.call(eventBus);
        if (event.isCancelled())
            return;
        this.playing = false;
        if (ltc) {
            ltcHandler.getGenerator().stop();
        }
    }
    
    public void stop() {
        log.info("Stop");
        TimecodeStopEvent event = new TimecodeStopEvent(currentTime);
        event.call(eventBus);
        if (event.isCancelled())
            return;
        this.playing = false;
        if (ltc) {
            ltcHandler.getGenerator().setTime(0, 0, 0, 0);
            ltcHandler.getGenerator().stop();
        }
        currentTime = new Timecode(0);
        start = 0;
    }

    @Override
    public void shutdown() {
        running = false;
    }
}