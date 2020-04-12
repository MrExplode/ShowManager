package me.mrexplode.timecode.events;

import com.illposed.osc.OSCMessage;

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
