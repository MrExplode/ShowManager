package me.sunstorm.showmanager.http;

import com.google.gson.JsonObject;
import io.javalin.websocket.*;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.ShowManager;
import me.sunstorm.showmanager.eventsystem.EventCall;
import me.sunstorm.showmanager.eventsystem.Listener;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodeChangeEvent;
import me.sunstorm.showmanager.util.Timecode;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler, WsErrorHandler, Listener {
    private final Set<WsContext> wsClients = ConcurrentHashMap.newKeySet();

    public WebSocketHandler() {
        ShowManager.getInstance().getEventBus().register(this);
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        log.info("[WS] {} connected", ctx.session.getRemoteAddress().getHostString());
        wsClients.add(ctx);
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

    }

    @EventCall
    private void onTimeChange(TimecodeChangeEvent e) {
        if (wsClients.size() == 0) return;
        JsonObject data = new JsonObject();
        Timecode time = e.getTime();
        data.addProperty("type", "time-change");
        data.addProperty("hour", time.getHour());
        data.addProperty("min", time.getMin());
        data.addProperty("sec", time.getSec());
        data.addProperty("frame", time.getFrame());
        String raw = data.toString();
        wsClients.forEach(client -> client.send(raw));
    }
}
