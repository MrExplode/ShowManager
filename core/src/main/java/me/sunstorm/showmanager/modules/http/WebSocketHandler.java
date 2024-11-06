package me.sunstorm.showmanager.modules.http;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.javalin.websocket.*;
import me.sunstorm.showmanager.Constants;
import me.sunstorm.showmanager.Worker;
import me.sunstorm.showmanager.modules.audio.AudioModule;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.EventCall;
import me.sunstorm.showmanager.eventsystem.Listener;
import me.sunstorm.showmanager.eventsystem.events.audio.*;
import me.sunstorm.showmanager.eventsystem.events.marker.MarkerCreateEvent;
import me.sunstorm.showmanager.eventsystem.events.marker.MarkerDeleteEvent;
import me.sunstorm.showmanager.eventsystem.events.osc.OscRecordStartEvent;
import me.sunstorm.showmanager.eventsystem.events.osc.OscRecordStopEvent;
import me.sunstorm.showmanager.eventsystem.events.scheduler.EventAddEvent;
import me.sunstorm.showmanager.eventsystem.events.scheduler.EventDeleteEvent;
import me.sunstorm.showmanager.eventsystem.events.scheduler.SchedulerExecuteEvent;
import me.sunstorm.showmanager.eventsystem.events.time.*;
import me.sunstorm.showmanager.modules.scheduler.SchedulerModule;
import me.sunstorm.showmanager.util.Timecode;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler, WsErrorHandler, Listener {
    private static final Logger log = LoggerFactory.getLogger(WebSocketHandler.class);

    public static WebSocketHandler INSTANCE;
    private final Set<WsContext> wsClients = ConcurrentHashMap.newKeySet();
    private Timecode lastDispatchedTime = null;

    private final EventBus eventBus;
    private final Worker worker;
    private final AudioModule player;
    private final SchedulerModule scheduler;

    @Inject
    public WebSocketHandler(EventBus eventBus, Worker worker, AudioModule player, SchedulerModule scheduler) {
        INSTANCE = this;
        this.eventBus = eventBus;
        this.worker = worker;
        this.player = player;
        this.scheduler = scheduler;

        eventBus.register(this);
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        var host = ctx.session.getRemoteAddress() instanceof InetSocketAddress r ? r.getHostString() : "unknown";
        log.info("[WS] {} connected", host);
        wsClients.add(ctx);
        JsonArray logs = new JsonArray();
        WebSocketLogger.getLogCache().forEach(logs::add);
        JsonObject data = new JsonObject();
        data.addProperty("type", "init");
        data.add("logs", logs);
        ctx.send(data.toString());
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        if (ctx.session.getRemoteAddress() instanceof InetSocketAddress remote) {
            log.info("[WS] {} disconnected", remote.getHostString());
        } else {
            log.info("[WS] unknown disconnect");
        }
        wsClients.remove(ctx);
    }

    @Override
    public void handleError(@NotNull WsErrorContext ctx) {
        if (ctx.session.getRemoteAddress() instanceof InetSocketAddress remote) {
            log.error("[WS] Error on {}", remote.getHostString(), ctx.error());
        } else {
            log.error("[WS] Error on unknown client", ctx.error());
        }
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        //log.debug("[WS] Received: {}", ctx.message());
    }

    @EventCall
    public void onTimeChange(TimecodeChangeEvent e) {
        //slow down the ws dispatch
        if (wsClients.isEmpty() || (lastDispatchedTime != null && e.getTime().millis() - lastDispatchedTime.millis() < 10)) return;
        JsonObject data = new JsonObject();
        Timecode time = e.getTime();
        data.addProperty("type", "time");
        data.addProperty("action", "change");
        data.add("time", Constants.GSON.toJsonTree(time));
        broadcast(data);
    }

    @EventCall
    public void onTimeStart(TimecodeStartEvent e) {
        JsonObject data = new JsonObject();
        data.addProperty("type", "time");
        data.addProperty("action", "start");
        broadcast(data);
    }

    @EventCall
    public void onTimePause(TimecodePauseEvent e) {
        JsonObject data = new JsonObject();
        data.addProperty("type", "time");
        data.addProperty("action", "pause");
        broadcast(data);
    }

    @EventCall
    public void onTimeStop(TimecodeStopEvent e) {
        //notify UI about the time reset
        onTimeChange(new TimecodeChangeEvent(Timecode.ZERO));
        JsonObject data = new JsonObject();
        data.addProperty("type", "time");
        data.addProperty("action", "stop");
        broadcast(data);
    }

    @EventCall
    public void onTimeSet(TimecodeSetEvent e) {
        //I don't see any case where set event needs to be distinguished from change event on frontend (yet)
        onTimeChange(new TimecodeChangeEvent(e.getTime()));
//        JsonObject data = new JsonObject();
//        data.addProperty("type", "time");
//        data.addProperty("action", "set");
    }

    @EventCall
    public void onAudioLoad(AudioLoadEvent e) {
        JsonObject data = new JsonObject();
        data.addProperty("type", "audio");
        data.addProperty("action", "load");
        data.addProperty("name", e.getTrack().getName());
        broadcast(data);
    }

    @EventCall
    public void onAudioStart(AudioStartEvent e) {
        JsonObject data = new JsonObject();
        data.addProperty("type", "audio");
        data.addProperty("action", "start");
        data.addProperty("name", e.getTrack().getName());
        broadcast(data);
    }

    @EventCall
    public void onAudioPause(AudioPauseEvent e) {
        JsonObject data = new JsonObject();
        data.addProperty("type", "audio");
        data.addProperty("action", "pause");
        broadcast(data);
    }

    @EventCall
    public void onAudioStop(AudioStopEvent e) {
        JsonObject data = new JsonObject();
        data.addProperty("type", "audio");
        data.addProperty("action", "stop");
        broadcast(data);
    }

    @EventCall
    public void onAudioVolume(AudioVolumeChangeEvent e) {
        JsonObject data = new JsonObject();
        data.addProperty("type", "audio");
        data.addProperty("action", "volume");
        data.addProperty("volume", e.getVolume());
        broadcast(data);
    }

    @EventCall
    public void onOscRecordStart(OscRecordStartEvent e) {
        sendRecord(true);
    }

    @EventCall
    public void onOscRecordStop(OscRecordStopEvent e) {
        sendRecord(false);
    }

    @EventCall
    public void onMarkerCreate(MarkerCreateEvent e) {
        sendMarkerSync();
    }

    @EventCall
    public void onMarkerDelete(MarkerDeleteEvent e) {
        sendMarkerSync();
    }

    @EventCall
    public void onEventAdd(EventAddEvent e) {
        JsonObject data = new JsonObject();
        data.addProperty("type", "scheduler");
        data.addProperty("action", "eventAdd");
        data.add("event", e.getEvent().getData());
        broadcast(data);
    }

    @EventCall
    public void onEventDelete(EventDeleteEvent e) {
        JsonObject data = new JsonObject();
        data.addProperty("type", "scheduler");
        data.addProperty("action", "syncEvents");
        broadcast(data);
    }

    @EventCall
    public void onSchedulerExec(SchedulerExecuteEvent e) {
        JsonObject data = new JsonObject();
        data.addProperty("type", "scheduler");
        data.addProperty("action", "eventExecuted");
        data.add("event", e.getEvent().getData());
        broadcast(data);
    }

    private void sendMarkerSync() {
        JsonObject data = new JsonObject();
        data.addProperty("type", "audio");
        data.addProperty("action", "marker");
        broadcast(data);
    }

    private void sendRecord(boolean enabled) {
        JsonObject data = new JsonObject();
        data.addProperty("type", "scheduler");
        data.addProperty("action", "record");
        data.addProperty("record", enabled);
        broadcast(data);
    }

    public void consumeLog(String log) {
        JsonObject data = new JsonObject();
        data.addProperty("type", "log");
        data.addProperty("log", log);
        broadcast(data);
    }

    public void broadcast(JsonObject data) {
        String raw = data.toString();
        wsClients.forEach(client -> client.send(raw));
    }
}
