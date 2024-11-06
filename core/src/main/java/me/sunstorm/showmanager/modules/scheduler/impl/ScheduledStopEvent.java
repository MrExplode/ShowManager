package me.sunstorm.showmanager.modules.scheduler.impl;

import me.sunstorm.showmanager.Worker;
import me.sunstorm.showmanager.modules.scheduler.AbstractScheduledEvent;
import me.sunstorm.showmanager.util.Timecode;

import javax.inject.Inject;
import java.util.UUID;

public class ScheduledStopEvent extends AbstractScheduledEvent {
    @Inject
    private Worker worker;

    public ScheduledStopEvent(Timecode executeTime, UUID id) {
        super(executeTime, "stop", id);
    }

    @Override
    public void execute() {
        worker.stop();
    }
}
