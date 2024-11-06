package me.sunstorm.showmanager.modules.scheduler.impl;

import com.google.gson.JsonObject;
import me.sunstorm.showmanager.Constants;
import me.sunstorm.showmanager.Worker;
import me.sunstorm.showmanager.modules.scheduler.AbstractScheduledEvent;
import me.sunstorm.showmanager.util.Timecode;

import javax.inject.Inject;
import java.util.UUID;

public class ScheduledJumpEvent extends AbstractScheduledEvent {
    @Inject private Worker worker;
    private final Timecode jumpTo;

    public ScheduledJumpEvent(Timecode executeTime, Timecode jumpTo, UUID id) {
        super(executeTime, "jump", id);
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
        worker.setTime(jumpTo.copy());
    }
}
