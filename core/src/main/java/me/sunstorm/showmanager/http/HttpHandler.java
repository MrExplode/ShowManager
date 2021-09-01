package me.sunstorm.showmanager.http;

import io.javalin.Javalin;
import io.javalin.http.util.RateLimit;
import io.javalin.plugin.json.JavalinJson;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.Constants;
import me.sunstorm.showmanager.ShowManager;
import me.sunstorm.showmanager.eventsystem.EventCall;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodeChangeEvent;
import me.sunstorm.showmanager.http.controller.AuthController;
import me.sunstorm.showmanager.http.controller.OutputController;
import me.sunstorm.showmanager.terminable.Terminable;

import java.util.concurrent.TimeUnit;

import static io.javalin.apibuilder.ApiBuilder.*;

@Slf4j
public class HttpHandler implements Terminable {
    private final Javalin javalin;

    public HttpHandler() {
        register();
        javalin = Javalin.create(config -> {
            config.requestLogger((ctx, executionTimeMs) -> log.info("[H] Request from " + ctx.ip() + " to " + ctx.path() + " took " + executionTimeMs + " ms"));
        });
        JavalinJson.setToJsonMapper(Constants.GSON::toJson);
        JavalinJson.setFromJsonMapper(Constants.GSON::fromJson);
        javalin.start(ShowManager.getInstance().getConfig().getHttpConfig().getPort());
        setupRouting();
    }

    private void setupRouting() {
        javalin.ws("", ws -> {
            WebSocketHandler wsHandler = new WebSocketHandler();
            ws.onConnect(wsHandler);
            ws.onClose(wsHandler);
            ws.onMessage(wsHandler);
            ws.onError(wsHandler);
        });
        javalin.routes(() -> {
            before(ctx -> new RateLimit(ctx).requestPerTimeUnit(15, TimeUnit.MINUTES));
            before(new AuthController());
            path("control", () -> {
                post("/play", __ -> ShowManager.getInstance().getWorker().play());
                post("/pause", __ -> ShowManager.getInstance().getWorker().pause());
                post("/stop", __ -> ShowManager.getInstance().getWorker().stop());
            });
            path("output", () -> {
                post("/artnet", OutputController::handleArtnet);
                post("/ltc", OutputController::handleLtc);
                post("/audio", OutputController::handleAudio);
            });
        });
    }

    @EventCall
    private void onTimeChange(TimecodeChangeEvent e) {

    }

    @Override
    public void shutdown() throws Exception {
        log.info("Shutting down HTTP services...");
        javalin.stop();
    }
}
