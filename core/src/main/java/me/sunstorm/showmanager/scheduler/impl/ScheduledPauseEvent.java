package me.sunstorm.showmanager.scheduler.impl;

import me.sunstorm.showmanager.Worker;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.scheduler.AbstractScheduledEvent;
import me.sunstorm.showmanager.util.Timecode;

public class ScheduledPauseEvent extends AbstractScheduledEvent {
    @Inject
    private Worker worker;

    public ScheduledPauseEvent(Timecode executeTime) {
        super(executeTime);
        inject(false);
    }

    @Override
    public void execute() {
        worker.pause();
    }
}
