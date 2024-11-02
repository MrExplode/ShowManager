package me.sunstorm.showmanager.modules.scheduler.impl;

import me.sunstorm.showmanager.Worker;
import me.sunstorm.showmanager.modules.scheduler.AbstractScheduledEvent;
import me.sunstorm.showmanager.util.Timecode;

import javax.inject.Inject;

public class ScheduledStopEvent extends AbstractScheduledEvent {
    @Inject
    private Worker worker;

    public ScheduledStopEvent(Timecode executeTime) {
        super(executeTime, "stop");
    }

    @Override
    public void execute() {
        worker.stop();
    }
}
