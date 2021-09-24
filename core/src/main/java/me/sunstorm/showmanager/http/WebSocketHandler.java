package me.sunstorm.showmanager.http;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.javalin.websocket.*;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.ShowManager;
import me.sunstorm.showmanager.eventsystem.EventCall;
import me.sunstorm.showmanager.eventsystem.Listener;
import me.sunstorm.showmanager.eventsystem.events.audio.AudioLoadEvent;
import me.sunstorm.showmanager.eventsystem.events.audio.AudioPauseEvent;
import me.sunstorm.showmanager.eventsystem.events.audio.AudioStartEvent;
import me.sunstorm.showmanager.eventsystem.events.audio.AudioStopEvent;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodeChangeEvent;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodePauseEvent;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodeStartEvent;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodeStopEvent;
import me.sunstorm.showmanager.util.Timecode;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler, WsErrorHandler, Listener {
    public static WebSocketHandler INSTANCE;
    private final Set<WsContext> wsClients = ConcurrentHashMap.newKeySet();
    private Timecode lastDispatchedTime = null;

    public WebSocketHandler() {
        INSTANCE = this;
        ShowManager.getInstance().getEventBus().register(this);
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        log.info("[WS] {} connected", ctx.session.getRemoteAddress().getHostString());
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
        log.info("[WS] {} disconnected", ctx.session.getRemoteAddress().getHostString());
        wsClients.remove(ctx);
    }

    @Override
    public void handleError(@NotNull WsErrorContext ctx) {
        log.error("[WS] Error on " + ctx.session.getRemoteAddress().getHostString(), ctx.error());
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        log.debug("[WS] Received: {}", ctx.message());
    }

    @EventCall
    public void onTimeChange(TimecodeChangeEvent e) {
        //slow down the ws dispatch
        if (wsClients.size() == 0 || (lastDispatchedTime != null && e.getTime().millis() - lastDispatchedTime.millis() < 10)) return;
        JsonObject data = new JsonObject();
        Timecode time = e.getTime();
        data.addProperty("type", "time");
        data.addProperty("action", "change");
        String hour = time.getHour() < 10 ? "0" + time.getHour() : String.valueOf(time.getHour());
        String min = time.getMin() < 10 ? "0" + time.getMin() : String.valueOf(time.getMin());
        String sec = time.getSec() < 10 ? "0" + time.getSec() : String.valueOf(time.getSec());
        String frame = time.getFrame() < 10 ? "0" + time.getFrame() : String.valueOf(time.getFrame());
        data.addProperty("hour", hour);
        data.addProperty("min", min);
        data.addProperty("sec", sec);
        data.addProperty("frame", frame);
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
        onTimeChange(new TimecodeChangeEvent(e.ZERO));
        JsonObject data = new JsonObject();
        data.addProperty("type", "time");
        data.addProperty("action", "stop");
        broadcast(data);
    }

    @EventCall
    public void onMusicLoad(AudioLoadEvent e) {
        JsonObject data = new JsonObject();
        data.addProperty("type", "audio");
        data.addProperty("action", "load");
        data.addProperty("name", e.getTrack().getName());
        broadcast(data);
    }

    @EventCall
    public void onMusicStart(AudioStartEvent e) {
        JsonObject data = new JsonObject();
        data.addProperty("type", "audio");
        data.addProperty("action", "start");
        data.addProperty("name", e.getTrack().getName());
        broadcast(data);
    }

    @EventCall
    public void onMusicPause(AudioPauseEvent e) {
        JsonObject data = new JsonObject();
        data.addProperty("type", "audio");
        data.addProperty("action", "pause");
        broadcast(data);
    }

    @EventCall
    public void onMusicStop(AudioStopEvent e) {
        JsonObject data = new JsonObject();
        data.addProperty("type", "audio");
        data.addProperty("action", "stop");
        broadcast(data);
    }

    public void consumeLog(String log) {
        JsonObject data = new JsonObject();
        data.addProperty("type", "log");
        data.addProperty("log", log);
        broadcast(data);
    }

    private void broadcast(JsonObject data) {
        String raw = data.toString();
        wsClients.forEach(client -> client.send(raw));
    }
}
