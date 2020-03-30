package me.mrexplode.timecode.events;

import com.illposed.osc.OSCPacket;

public class OscEvent extends TimecodeEvent {
    
    private OSCPacket oscPacket;

    public OscEvent(EventType type, OSCPacket oscPacket) {
        super(type, "OscEvent");
        this.oscPacket = oscPacket;
    }
    
    public OSCPacket getPacket() {
        return oscPacket;
    }

}
