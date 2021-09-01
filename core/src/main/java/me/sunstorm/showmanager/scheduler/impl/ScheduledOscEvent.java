package me.sunstorm.showmanager.scheduler.impl;

import com.illposed.osc.OSCPacket;
import me.sunstorm.showmanager.ShowManager;
import me.sunstorm.showmanager.scheduler.AbstractScheduledEvent;
import me.sunstorm.showmanager.util.Timecode;

public class ScheduledOscEvent extends AbstractScheduledEvent {
    private final OSCPacket packet;

    public ScheduledOscEvent(Timecode executeTime, OSCPacket packet) {
        super(executeTime);
        this.packet = packet;
    }

    @Override
    public void execute() {
        ShowManager.getInstance().getOscHandler().sendOscPacket(packet);
    }
}
