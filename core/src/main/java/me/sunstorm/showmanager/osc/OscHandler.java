package me.sunstorm.showmanager.osc;

import com.illposed.osc.*;
import com.illposed.osc.transport.OSCPortIn;
import com.illposed.osc.transport.OSCPortOut;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.ShowManager;
import me.sunstorm.showmanager.eventsystem.events.osc.OscDispatchEvent;
import me.sunstorm.showmanager.eventsystem.events.osc.OscReceiveEvent;
import me.sunstorm.showmanager.settings.config.OscDispatchConfig;
import me.sunstorm.showmanager.terminable.Terminable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@Getter
public class OscHandler implements Terminable {
    private InetAddress address = null;
    private final int outgoingPort;
    private final int incomingPort;

    private OSCPortOut portOut;
    private OSCPortIn portIn;

    public OscHandler(OscDispatchConfig config) {
        log.info("Starting OSCHandler...");
        register();
        try {
            address = InetAddress.getByName(config.getTarget());
        } catch (UnknownHostException e) {
            log.info("Unknown outgoing OSC host", e);
        }
        outgoingPort = config.getPort();
        incomingPort = config.getPort() + 1;

        try {
            portOut = new OSCPortOut(address, outgoingPort);
            portIn = new OSCPortIn(incomingPort);
            portIn.addPacketListener(new OSCPacketListener() {
                @Override
                public void handlePacket(OSCPacketEvent event) {
                    if (event.getPacket() instanceof OSCMessage && ((OSCMessage) event.getPacket()).getAddress().startsWith("/timecode/")) {
                        OscReceiveEvent oscEvent = new OscReceiveEvent(event.getPacket());
                        oscEvent.call(ShowManager.getInstance().getEventBus());
                    }
                }

                @Override
                public void handleBadData(OSCBadDataEvent event) {
                    log.warn("Received bad OSC data", event.getException());
                }
            });
        } catch (IOException e) {
            log.error("Failed to start OSC dispatcher", e);
        }
    }

    @Override
    public void shutdown() throws IOException {
        log.info("Shutting down OSC...");
        if (portOut != null) portOut.close();
        if (portIn != null) portIn.close();
    }

    public void sendOscPacket(OSCPacket packet) {
        OscDispatchEvent event = new OscDispatchEvent(packet);
        event.call(ShowManager.getInstance().getEventBus());
        if (event.isCancelled())
            return;
        try {
            if (portOut != null) portOut.send(packet);
        } catch (IOException | OSCSerializeException e) {
            log.error("Failed to send OSC packet", e);
        }
    }
}
