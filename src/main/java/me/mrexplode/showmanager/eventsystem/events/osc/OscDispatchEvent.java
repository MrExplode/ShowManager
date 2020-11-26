package me.mrexplode.showmanager.eventsystem.events.osc;

import com.illposed.osc.OSCPacket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.mrexplode.showmanager.eventsystem.events.CancellableEvent;

@Getter
@AllArgsConstructor
public class OscDispatchEvent extends CancellableEvent {
    private final OSCPacket oscPacket;
}
