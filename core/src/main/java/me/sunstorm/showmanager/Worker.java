package me.sunstorm.showmanager;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.modules.artnet.ArtNetModule;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.events.time.*;
import me.sunstorm.showmanager.injection.DependencyInjection;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;
import me.sunstorm.showmanager.modules.ltc.LtcModule;
import me.sunstorm.showmanager.modules.remote.DmxRemoteModule;
import me.sunstorm.showmanager.terminable.Terminable;
import me.sunstorm.showmanager.util.Timecode;

import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
public class Worker implements Runnable, Terminable, InjectRecipient {
    @Inject
    private EventBus eventBus;
    @Inject
    private LtcModule ltcModule;
    private final DmxRemoteModule dmxRemote;
    private final ArtNetModule artNetModule;
    private boolean running = true;
    private boolean playing = false;
    private long start = 0;
    private long elapsed = 0;
    private Timecode currentTime = new Timecode(0);
    private final int framerate;
    
    public Worker(int framerate) {
        register();
        inject();
        this.framerate = framerate;
        artNetModule = new ArtNetModule();
        DependencyInjection.updateProvider(ArtNetModule.class, () -> artNetModule);
        dmxRemote = new DmxRemoteModule();
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
                dmxRemote.handleData(artNetModule.getData(dmxRemote.getAddress().subnet(), dmxRemote.getAddress().universe()));
                
                if (playing) {
                    currentTime.set(elapsed);
                    artNetModule.setTime(currentTime);
                    TimecodeChangeEvent changeEvent = new TimecodeChangeEvent(currentTime);
                    changeEvent.call(eventBus);
                }

                artNetModule.broadcast();
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

        artNetModule.setTime(time);
        ltcModule.setTime(time);
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
        ltcModule.start();
        this.playing = true;
    }
    
    public void pause() {
        log.info("Pause");
        TimecodePauseEvent event = new TimecodePauseEvent();
        event.call(eventBus);
        if (event.isCancelled())
            return;
        this.playing = false;
        ltcModule.stop();
    }
    
    public void stop() {
        log.info("Stop");
        TimecodeStopEvent event = new TimecodeStopEvent(currentTime);
        event.call(eventBus);
        if (event.isCancelled())
            return;
        this.playing = false;
        ltcModule.setTime(Timecode.ZERO);
        ltcModule.stop();
        currentTime = new Timecode(0);
        start = 0;
    }

    @Override
    public void shutdown() {
        running = false;
    }
}