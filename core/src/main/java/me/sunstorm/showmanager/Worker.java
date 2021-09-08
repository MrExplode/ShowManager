package me.sunstorm.showmanager;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.artnet.ArtNetHandler;
import me.sunstorm.showmanager.eventsystem.events.time.*;
import me.sunstorm.showmanager.remote.DmxRemoteControl;
import me.sunstorm.showmanager.terminable.Terminable;
import me.sunstorm.showmanager.util.Timecode;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
public class Worker implements Runnable, Terminable {
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
                    ShowManager.getInstance().getLtcHandler().getGenerator().setTime(currentTime.getHour(), currentTime.getMin(), currentTime.getSec(), currentTime.getFrame());
                    TimecodeChangeEvent changeEvent = new TimecodeChangeEvent(currentTime);
                    changeEvent.call(ShowManager.getInstance().getEventBus());
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
        event.call(ShowManager.getInstance().getEventBus());
        if (event.isCancelled())
            return;

        artNetHandler.setTime(time);
        ShowManager.getInstance().getLtcHandler().getGenerator().setTime(time.getHour(), time.getMin(), time.getSec(), time.getFrame());
        elapsed = time.millis();
        start = System.currentTimeMillis() - elapsed;
        this.currentTime = time;
    }
    
    public void play() {
        log.info("Play");
        TimecodeStartEvent event = new TimecodeStartEvent(currentTime);
        event.call(ShowManager.getInstance().getEventBus());
        if (event.isCancelled())
            return;

        if (start == 0)
            start = System.currentTimeMillis();
        else
            start = System.currentTimeMillis() - elapsed;
        if (ltc)
            ShowManager.getInstance().getLtcHandler().getGenerator().start();
        this.playing = true;
    }
    
    public void pause() {
        log.info("Pause");
        TimecodePauseEvent event = new TimecodePauseEvent();
        event.call(ShowManager.getInstance().getEventBus());
        if (event.isCancelled())
            return;
        this.playing = false;
        if (ltc) {
            ShowManager.getInstance().getLtcHandler().getGenerator().stop();
        }
    }
    
    public void stop() {
        log.info("Stop");
        TimecodeStopEvent event = new TimecodeStopEvent();
        event.call(ShowManager.getInstance().getEventBus());
        if (event.isCancelled())
            return;
        this.playing = false;
        if (ltc) {
            ShowManager.getInstance().getLtcHandler().getGenerator().setTime(0, 0, 0, 0);
            ShowManager.getInstance().getLtcHandler().getGenerator().stop();
        }
        currentTime = new Timecode(0);
        start = 0;
    }

    @Override
    public void shutdown() {
        running = false;
    }
}