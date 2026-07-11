package me.sunstorm.showmanager;

import me.sunstorm.showmanager.modules.artnet.ArtNetModule;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.events.time.*;
import me.sunstorm.showmanager.modules.remote.DmxRemoteModule;
import me.sunstorm.showmanager.terminable.Terminable;
import me.sunstorm.showmanager.util.Exceptions;
import me.sunstorm.showmanager.util.Timecode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

@Singleton
public class Worker implements Runnable, Terminable {
    private static final Logger log = LoggerFactory.getLogger(Worker.class);

    private final EventBus eventBus;
    private final DmxRemoteModule dmxRemote;
    private final ArtNetModule artNetModule;
    private final int framerate;
    private final double frameInterval;
    private boolean running = true;
    private boolean playing = false;
    private long start = 0;
    private long elapsed = 0;
    private Timecode currentTime = new Timecode(0);

    @Inject
    public Worker(EventBus bus, ArtNetModule artNetModule, DmxRemoteModule dmxRemoteModule, @Named("framerate") int framerate) {
        this.eventBus = bus;
        if (framerate <= 0) {
            log.warn("Invalid framerate {}, falling back to 25", framerate);
            framerate = 25;
        }
        this.framerate = framerate;
        this.frameInterval = 1000.0 / framerate;
        this.artNetModule = artNetModule;
        this.dmxRemote = dmxRemoteModule;
        register();
    }

    @Override
    public void run() {
        log.info("Starting...");
        running = true;
        start = 0;
        long time = start;
        while (running) {
            final long current = System.currentTimeMillis();
            if (current >= time + frameInterval) {
                time = current;
                if (playing) {
                    elapsed = time - start;
                }
                dmxRemote.handleData(artNetModule.getData(dmxRemote.getAddress().subnet(), dmxRemote.getAddress().universe()));
                
                if (playing) {
                    currentTime.set(elapsed);
                    TimecodeChangeEvent changeEvent = new TimecodeChangeEvent(currentTime.copy());
                    changeEvent.call(eventBus);
                }
            }

            //slowing down the loop
            try {
                if (playing)
                    TimeUnit.MILLISECONDS.sleep(1);
                else
                    TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                Exceptions.sneaky(e);
            }
        }
    }
    
    public void setTime(Timecode time) {
        TimecodeSetEvent event = new TimecodeSetEvent(time);
        event.call(eventBus);
        if (event.isCancelled())
            return;

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
        this.playing = true;
    }
    
    public void pause() {
        log.info("Pause");
        TimecodePauseEvent event = new TimecodePauseEvent();
        event.call(eventBus);
        if (event.isCancelled())
            return;
        this.playing = false;
    }

    public void stop() {
        log.info("Stop");
        TimecodeStopEvent event = new TimecodeStopEvent(currentTime);
        event.call(eventBus);
        if (event.isCancelled())
            return;
        this.playing = false;
        currentTime = new Timecode(0);
        start = 0;
    }

    @Override
    public void shutdown() {
        running = false;
    }

    // generated

    public Timecode getCurrentTime() {
        return currentTime;
    }

    public boolean isPlaying() {
        return playing;
    }

}