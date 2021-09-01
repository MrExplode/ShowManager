package me.sunstorm.showmanager.scheduler.impl;

import me.sunstorm.showmanager.ShowManager;
import me.sunstorm.showmanager.scheduler.AbstractScheduledEvent;
import me.sunstorm.showmanager.util.Timecode;

public class ScheduledStopEvent extends AbstractScheduledEvent {

    public ScheduledStopEvent(Timecode executeTime) {
        super(executeTime);
    }

    @Override
    public void execute() {
        ShowManager.getInstance().getWorker().stop();
    }
}
