package me.sunstorm.showmanager.modules.scheduler.impl;

import com.google.gson.JsonObject;
import com.illposed.osc.OSCMessage;
import me.sunstorm.showmanager.Constants;
import me.sunstorm.showmanager.modules.osc.OscModule;
import me.sunstorm.showmanager.modules.scheduler.AbstractScheduledEvent;
import me.sunstorm.showmanager.util.Timecode;

import javax.inject.Inject;

public class ScheduledOscEvent extends AbstractScheduledEvent {
    private final OSCMessage packet;
    @Inject private OscModule oscModule;

    public ScheduledOscEvent(Timecode executeTime, OSCMessage packet) {
        super(executeTime, "osc");
        this.packet = packet;
    }

    @Override
    public JsonObject getData() {
        JsonObject data = super.getData();
        data.add("packet", Constants.GSON.toJsonTree(packet));
        return data;
    }

    @Override
    public void execute() {
        oscModule.sendOscPacket(packet);
    }
}
