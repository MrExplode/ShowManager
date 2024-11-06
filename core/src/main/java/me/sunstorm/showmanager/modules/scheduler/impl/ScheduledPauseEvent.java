package me.sunstorm.showmanager.modules.scheduler.impl;

import me.sunstorm.showmanager.Worker;
import me.sunstorm.showmanager.modules.scheduler.AbstractScheduledEvent;
import me.sunstorm.showmanager.util.Timecode;

import javax.inject.Inject;
import java.util.UUID;

public class ScheduledPauseEvent extends AbstractScheduledEvent {
    @Inject
    private Worker worker;

    public ScheduledPauseEvent(Timecode executeTime, UUID id) {
        super(executeTime, "pause", id);
    }

    @Override
    public void execute() {
        worker.pause();
    }
}
