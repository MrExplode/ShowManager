package me.sunstorm.showmanager.modules.scheduler.impl;

import me.sunstorm.showmanager.Worker;
import me.sunstorm.showmanager.modules.scheduler.AbstractScheduledEvent;
import me.sunstorm.showmanager.util.Timecode;

import javax.inject.Inject;

public class ScheduledPauseEvent extends AbstractScheduledEvent {
    @Inject
    private Worker worker;

    public ScheduledPauseEvent(Timecode executeTime) {
        super(executeTime, "pause");
    }

    @Override
    public void execute() {
        worker.pause();
    }
}
