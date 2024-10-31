package me.sunstorm.showmanager.eventsystem.events.osc;

import com.illposed.osc.OSCPacket;
import me.sunstorm.showmanager.eventsystem.events.CancellableEvent;

public class OscDispatchEvent extends CancellableEvent {
    private final OSCPacket oscPacket;

    public OscDispatchEvent(OSCPacket oscPacket) {
        this.oscPacket = oscPacket;
    }

    public OSCPacket getOscPacket() {
        return oscPacket;
    }
}
