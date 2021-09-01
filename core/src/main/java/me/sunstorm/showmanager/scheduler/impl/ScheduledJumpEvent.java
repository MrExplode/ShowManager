package me.sunstorm.showmanager.scheduler.impl;

import me.sunstorm.showmanager.ShowManager;
import me.sunstorm.showmanager.scheduler.AbstractScheduledEvent;
import me.sunstorm.showmanager.util.Timecode;

public class ScheduledJumpEvent extends AbstractScheduledEvent {
    private final Timecode jumpTo;

    public ScheduledJumpEvent(Timecode executeTime, Timecode jumpTo) {
        super(executeTime);
        this.jumpTo = jumpTo;
    }

    @Override
    public void execute() {
        ShowManager.getInstance().getWorker().setTime(jumpTo);
    }
}
