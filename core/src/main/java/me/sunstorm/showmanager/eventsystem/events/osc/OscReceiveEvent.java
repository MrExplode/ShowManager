package me.sunstorm.showmanager.eventsystem.events.osc;

import com.illposed.osc.OSCPacket;
import me.sunstorm.showmanager.eventsystem.events.Event;

public class OscReceiveEvent extends Event {
    private final OSCPacket oscPacket;

    public OscReceiveEvent(OSCPacket oscPacket) {
        this.oscPacket = oscPacket;
    }

    public OSCPacket getOscPacket() {
        return oscPacket;
    }
}
