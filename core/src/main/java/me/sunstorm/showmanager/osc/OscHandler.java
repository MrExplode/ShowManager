package me.sunstorm.showmanager.osc;

import com.google.gson.JsonObject;
import com.illposed.osc.*;
import com.illposed.osc.transport.OSCPortIn;
import com.illposed.osc.transport.OSCPortOut;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.events.osc.OscDispatchEvent;
import me.sunstorm.showmanager.eventsystem.events.osc.OscReceiveEvent;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;
import me.sunstorm.showmanager.settings.SettingsHolder;
import me.sunstorm.showmanager.terminable.Terminable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@Getter
public class OscHandler extends SettingsHolder implements Terminable, InjectRecipient {
    @Inject
    private EventBus eventBus;
    private InetAddress address;
    private int outgoingPort = 8000;
    private int incomingPort = 8001;

    private OSCPortOut portOut;
    private OSCPortIn portIn;

    public OscHandler() {
        super("osc-dispatcher");
        log.info("Starting OSCHandler...");
        register();
        inject();
        try {
            portOut = new OSCPortOut(address, outgoingPort);
            portIn = new OSCPortIn(incomingPort);
            portIn.addPacketListener(new OSCPacketListener() {
                @Override
                public void handlePacket(OSCPacketEvent event) {
                    if (event.getPacket() instanceof OSCMessage && ((OSCMessage) event.getPacket()).getAddress().startsWith("/timecode/")) {
                        OscReceiveEvent oscEvent = new OscReceiveEvent(event.getPacket());
                        oscEvent.call(eventBus);
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
        event.call(eventBus);
        if (event.isCancelled())
            return;
        try {
            if (portOut != null) portOut.send(packet);
        } catch (IOException | OSCSerializeException e) {
            log.error("Failed to send OSC packet", e);
        }
    }

    @Override
    public JsonObject getData() {
        JsonObject data = new JsonObject();
        data.addProperty("port-out", outgoingPort);
        data.addProperty("port-in", incomingPort);
        data.addProperty("target-address", address == null ? "127.0.0.1" : address.getHostAddress());
        return data;
    }

    @Override
    public void onLoad(JsonObject object) {
        incomingPort = object.get("port-in").getAsInt();
        outgoingPort = object.get("port-out").getAsInt();
        try {
            address = InetAddress.getByName(object.get("target-address").getAsString());
        } catch (UnknownHostException e) {
            log.error("Failed to find OSC target address", e);
        }
    }
}
