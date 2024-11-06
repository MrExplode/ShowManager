package me.sunstorm.showmanager.modules.osc;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.illposed.osc.*;
import com.illposed.osc.transport.OSCPortIn;
import com.illposed.osc.transport.OSCPortOut;
import me.sunstorm.showmanager.Worker;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.events.osc.OscDispatchEvent;
import me.sunstorm.showmanager.eventsystem.events.osc.OscReceiveEvent;
import me.sunstorm.showmanager.eventsystem.events.osc.OscRecordStartEvent;
import me.sunstorm.showmanager.eventsystem.events.osc.OscRecordStopEvent;
import me.sunstorm.showmanager.modules.Module;
import me.sunstorm.showmanager.modules.scheduler.SchedulerModule;
import me.sunstorm.showmanager.modules.scheduler.impl.ScheduledOscEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class OscModule extends Module {
    private static final Logger log = LoggerFactory.getLogger(OscModule.class);

    private final Worker worker;
    private final Provider<SchedulerModule> schedulerProvider;
    @Nullable
    private SchedulerModule scheduler;
    private InetAddress address;
    private int outgoingPort = 8000;
    private int incomingPort = 8001;

    private OSCPortOut portOut;
    private OSCPortIn portIn;

    private boolean recording = false;
    private final Map<String, Object> recordCache = new HashMap<>();

    @Inject
    public OscModule(EventBus bus, Worker worker, Provider<SchedulerModule> schedulerProvider) {
        super(bus);
        this.worker = worker;
        this.schedulerProvider = schedulerProvider;

        init();
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

                    if (recording && worker.isPlaying()) {
                        OSCMessage message = (OSCMessage) event.getPacket();
                        Object property = message.getArguments() == null ? null : (!message.getArguments().isEmpty() ? message.getArguments().getFirst() : null);
                        if (!recordCache.containsKey(message.getAddress()) && recordCache.get(message.getAddress()) != property) {
                            log.info("adding: {} stuff: {}", message.getAddress(), message.getArguments().toString());
                            loadSchedulerModule().addEvent(new ScheduledOscEvent(worker.getCurrentTime(), new OSCMessage(message.getAddress(), message.getArguments()), null));
                            recordCache.put(message.getAddress(), property);
                        }
                    }
                }

                @Override
                public void handleBadData(OSCBadDataEvent event) {
                    log.warn("Received bad OSC data", event.getException());
                }
            });
            portIn.startListening();
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

    public void setRecording(boolean value) {
        recording = value;
        if (recording) {
            OscRecordStartEvent event = new OscRecordStartEvent();
            event.call(eventBus);
        } else {
            OscRecordStopEvent event = new OscRecordStopEvent();
            event.call(eventBus);
        }
    }

    @NotNull
    @Override
    public JsonObject getData() {
        JsonObject data = new JsonObject();
        data.addProperty("port-out", outgoingPort);
        data.addProperty("port-in", incomingPort);
        data.addProperty("target-address", address == null ? "127.0.0.1" : address.getHostAddress());
        return data;
    }

    @Override
    public void onLoad(@NotNull JsonElement element) {
        var object = element.getAsJsonObject();
        incomingPort = object.get("port-in").getAsInt();
        outgoingPort = object.get("port-out").getAsInt();
        try {
            address = InetAddress.getByName(object.get("target-address").getAsString());
        } catch (UnknownHostException e) {
            log.error("Failed to find OSC target address", e);
        }
    }

    @Override
    public String getName() {
        return "osc-dispatcher";
    }

    private SchedulerModule loadSchedulerModule() {
        if (scheduler == null) {
            scheduler = schedulerProvider.get();
        }
        return scheduler;
    }

    // generated

    public boolean isRecording() {
        return recording;
    }
}
