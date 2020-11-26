package me.mrexplode.showmanager.events.impl.osc;

import com.illposed.osc.OSCMessage;
import me.mrexplode.showmanager.events.EventType;
import me.mrexplode.showmanager.events.TimecodeEvent;

public class OscEvent extends TimecodeEvent {
    
    private OSCMessage oscPacket;

    public OscEvent(EventType type, OSCMessage oscPacket) {
        super(type, "OscEvent");
        this.oscPacket = oscPacket;
    }
    
    public OSCMessage getPacket() {
        return oscPacket;
    }

}
