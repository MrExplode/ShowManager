package me.sunstorm.showmanager;

import me.sunstorm.showmanager.clock.MasterTimeSource;
import me.sunstorm.showmanager.clock.TimeSource;
import me.sunstorm.showmanager.cluster.ClusterService;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.events.time.*;
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
    private final ClusterService cluster;
    private final int framerate;
    private final double frameInterval;
    private boolean running = true;
    private TimeSource timeSource = new MasterTimeSource();

    @Inject
    public Worker(EventBus bus, ClusterService cluster, @Named("framerate") int framerate) {
        this.eventBus = bus;
        this.cluster = cluster;
        if (framerate <= 0) {
            log.warn("Invalid framerate {}, falling back to 25", framerate);
            framerate = 25;
        }
        this.framerate = framerate;
        this.frameInterval = 1000.0 / framerate;
        register();
    }

    @Override
    public void run() {
        log.info("Starting...");
        running = true;
        long time = 0;
        while (running) {
            final long current = System.currentTimeMillis();
            if (current >= time + frameInterval) {
                time = current;
                if (timeSource.isPlaying()) {
                    timeSource.tick(current);
                    TimecodeChangeEvent changeEvent = new TimecodeChangeEvent(timeSource.current().copy());
                    changeEvent.call(eventBus);
                }
            }

            //slowing down the loop
            try {
                if (timeSource.isPlaying())
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
        timeSource.seek(time);
    }

    public void play() {
        log.info("Play");
        TimecodeStartEvent event = new TimecodeStartEvent(timeSource.current());
        event.call(eventBus);
        if (event.isCancelled())
            return;
        timeSource.start();
    }

    public void pause() {
        log.info("Pause");
        TimecodePauseEvent event = new TimecodePauseEvent();
        event.call(eventBus);
        if (event.isCancelled())
            return;
        timeSource.pause();
    }

    public void stop() {
        log.info("Stop");
        TimecodeStopEvent event = new TimecodeStopEvent(timeSource.current());
        event.call(eventBus);
        if (event.isCancelled())
            return;
        timeSource.stop();
    }

    public boolean isMaster() {
        return cluster.isCoordinator();
    }

    @Override
    public void shutdown() {
        running = false;
    }

    // generated

    public Timecode getCurrentTime() {
        return timeSource.current();
    }

    public boolean isPlaying() {
        return timeSource.isPlaying();
    }

}