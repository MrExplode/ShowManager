package me.sunstorm.showmanager.eventsystem.events.osc;

import com.illposed.osc.OSCPacket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sunstorm.showmanager.eventsystem.events.Event;

@Getter
@AllArgsConstructor
public class OscReceiveEvent extends Event {
    private final OSCPacket oscPacket;
}
