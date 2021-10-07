package me.sunstorm.showmanager.scheduler.impl;

import com.illposed.osc.OSCPacket;
import me.sunstorm.showmanager.ShowManager;
import me.sunstorm.showmanager.Worker;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.osc.OscHandler;
import me.sunstorm.showmanager.scheduler.AbstractScheduledEvent;
import me.sunstorm.showmanager.util.Timecode;

public class ScheduledOscEvent extends AbstractScheduledEvent {
    private final OSCPacket packet;
    @Inject
    private OscHandler oscHandler;

    public ScheduledOscEvent(Timecode executeTime, OSCPacket packet) {
        super(executeTime);
        inject(false);
        this.packet = packet;
    }

    @Override
    public void execute() {
        oscHandler.sendOscPacket(packet);
    }
}
