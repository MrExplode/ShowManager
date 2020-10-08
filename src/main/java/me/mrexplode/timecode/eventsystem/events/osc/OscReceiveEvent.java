package me.mrexplode.timecode.eventsystem.events.osc;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.mrexplode.timecode.eventsystem.events.Event;

@Getter
@AllArgsConstructor
public class OscReceiveEvent extends Event {
    private final OSCPacket oscPacket;
}
