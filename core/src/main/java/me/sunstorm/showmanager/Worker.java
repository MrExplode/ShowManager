package me.sunstorm.showmanager;

import me.sunstorm.showmanager.clock.FollowerTimeSource;
import me.sunstorm.showmanager.clock.MasterTimeSource;
import me.sunstorm.showmanager.clock.TimeSource;
import me.sunstorm.showmanager.cluster.ClusterService;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.EventCall;
import me.sunstorm.showmanager.eventsystem.Listener;
import me.sunstorm.showmanager.eventsystem.events.time.*;
import me.sunstorm.showmanager.eventsystem.events.transport.TransportCommandEvent;
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
public class Worker implements Runnable, Terminable, Listener {
    private static final Logger log = LoggerFactory.getLogger(Worker.class);

    private final EventBus eventBus;
    private final ClusterService cluster;
    private final int framerate;
    private final double frameInterval;
    private boolean running = true;
    private final MasterTimeSource master = new MasterTimeSource();
    private final FollowerTimeSource follower = new FollowerTimeSource();

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
        eventBus.register(this);
    }

    @Override
    public void run() {
        log.info("Starting...");
        running = true;
        long time = 0;
        while (running) {
            final long current = System.currentTimeMillis();
            boolean driving = isMaster() && master.isPlaying();
            if (current >= time + frameInterval) {
                time = current;
                if (driving) {
                    master.tick(current);
                    TimecodeChangeEvent changeEvent = new TimecodeChangeEvent(master.current().copy());
                    changeEvent.call(eventBus);
                }
            }

            //slowing down the loop
            try {
                TimeUnit.MILLISECONDS.sleep(driving ? 1 : 10);
            } catch (InterruptedException e) {
                Exceptions.sneaky(e);
            }
        }
    }

    public void play() {
        if (isMaster())
            executePlay();
        else
            new TransportCommandEvent(TransportCommandEvent.Action.PLAY, null).call(eventBus);
    }

    public void pause() {
        if (isMaster())
            executePause();
        else
            new TransportCommandEvent(TransportCommandEvent.Action.PAUSE, null).call(eventBus);
    }

    public void stop() {
        if (isMaster())
            executeStop();
        else
            new TransportCommandEvent(TransportCommandEvent.Action.STOP, null).call(eventBus);
    }

    public void setTime(Timecode time) {
        if (isMaster())
            executeSet(time);
        else
            new TransportCommandEvent(TransportCommandEvent.Action.SET, time).call(eventBus);
    }

    @EventCall
    public void onCommand(TransportCommandEvent event) {
        if (!isMaster())
            return;
        switch (event.getAction()) {
            case PLAY -> executePlay();
            case PAUSE -> executePause();
            case STOP -> executeStop();
            case SET -> executeSet(event.getTime());
        }
    }

    private void executePlay() {
        log.info("Play");
        TimecodeStartEvent event = new TimecodeStartEvent(master.current());
        event.call(eventBus);
        if (event.isCancelled())
            return;
        master.start();
    }

    private void executePause() {
        log.info("Pause");
        TimecodePauseEvent event = new TimecodePauseEvent();
        event.call(eventBus);
        if (event.isCancelled())
            return;
        master.pause();
    }

    private void executeStop() {
        log.info("Stop");
        TimecodeStopEvent event = new TimecodeStopEvent(master.current());
        event.call(eventBus);
        if (event.isCancelled())
            return;
        master.stop();
    }

    private void executeSet(Timecode time) {
        TimecodeSetEvent event = new TimecodeSetEvent(time);
        event.call(eventBus);
        if (event.isCancelled())
            return;
        master.seek(time);
    }

    @EventCall
    public void onTimeChange(TimecodeChangeEvent event) {
        if (!isMaster())
            follower.feed(event.getTime());
    }

    @EventCall
    public void onTimeStart(TimecodeStartEvent event) {
        if (!isMaster())
            follower.start();
    }

    @EventCall
    public void onTimePause(TimecodePauseEvent event) {
        if (!isMaster())
            follower.pause();
    }

    @EventCall
    public void onTimeStop(TimecodeStopEvent event) {
        if (!isMaster())
            follower.stop();
    }

    @EventCall
    public void onTimeSet(TimecodeSetEvent event) {
        if (!isMaster())
            follower.seek(event.getTime());
    }

    public boolean isMaster() {
        return cluster.isCoordinator();
    }

    private TimeSource active() {
        return isMaster() ? master : follower;
    }

    @Override
    public void shutdown() {
        running = false;
    }

    // generated

    public Timecode getCurrentTime() {
        return active().current();
    }

    public boolean isPlaying() {
        return active().isPlaying();
    }

}
