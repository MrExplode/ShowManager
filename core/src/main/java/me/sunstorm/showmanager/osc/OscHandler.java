package me.sunstorm.showmanager.osc;

import com.illposed.osc.*;
import com.illposed.osc.transport.OSCPortIn;
import com.illposed.osc.transport.OSCPortOut;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.Worker;
import me.sunstorm.showmanager.eventsystem.events.osc.OscDispatchEvent;
import me.sunstorm.showmanager.eventsystem.events.osc.OscReceiveEvent;

import java.io.IOException;
import java.net.InetAddress;

@Slf4j
@Getter
@RequiredArgsConstructor
public class OscHandler {
    private final InetAddress address;
    private final int outgoingPort;
    private final int incomingPort;

    private OSCPortOut portOut;
    private OSCPortIn portIn;

    public void setup() throws IOException {
        log.info("Starting OSCHandler...");
        portOut = new OSCPortOut(address, outgoingPort);
        portIn = new OSCPortIn(incomingPort);
        portIn.addPacketListener(new OSCPacketListener() {
            @Override
            public void handlePacket(OSCPacketEvent event) {
                if (event.getPacket() instanceof OSCMessage && ((OSCMessage) event.getPacket()).getAddress().startsWith("/timecode/")) {
                    OscReceiveEvent oscEvent = new OscReceiveEvent(event.getPacket());
                    oscEvent.call(Worker.getInstance().getEventBus());
                }
            }

            @Override
            public void handleBadData(OSCBadDataEvent event) {
                log.warn("Received bad OSC data", event.getException());
            }
        });
    }

    public void shutdown() throws IOException {
        portOut.close();
        portIn.close();
    }

    public void sendOscPacket(OSCPacket packet) {
        OscDispatchEvent event = new OscDispatchEvent(packet);
        event.call(Worker.getInstance().getEventBus());
        if (event.isCancelled())
            return;
        try {
            portOut.send(packet);
        } catch (IOException | OSCSerializeException e) {
            log.error("Failed to send OSC packet", e);
        }
    }
}
