package me.sunstorm.showmanager.scheduler.impl;

import com.google.gson.JsonObject;
import me.sunstorm.showmanager.Constants;
import me.sunstorm.showmanager.Worker;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.scheduler.AbstractScheduledEvent;
import me.sunstorm.showmanager.util.Timecode;

public class ScheduledJumpEvent extends AbstractScheduledEvent {
    private final Timecode jumpTo;
    @Inject
    private Worker worker;

    public ScheduledJumpEvent(Timecode executeTime, Timecode jumpTo) {
        super(executeTime, "jump");
        inject(false);
        this.jumpTo = jumpTo;
    }

    @Override
    public JsonObject getData() {
        JsonObject data = super.getData();
        data.add("jumpTime", Constants.GSON.toJsonTree(jumpTo));
        return data;
    }

    @Override
    public void execute() {
        worker.setTime(jumpTo.clone());
    }
}
