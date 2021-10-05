package me.sunstorm.showmanager.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import io.javalin.http.util.RateLimit;
import io.javalin.plugin.json.JavalinJson;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import me.sunstorm.showmanager.Constants;
import me.sunstorm.showmanager.ShowManager;
import me.sunstorm.showmanager.http.controller.OutputController;
import me.sunstorm.showmanager.terminable.Terminable;
import me.sunstorm.showmanager.util.Timecode;

import java.util.concurrent.TimeUnit;

import static io.javalin.apibuilder.ApiBuilder.*;

@Slf4j
public class HttpHandler implements Terminable {
    private final Javalin javalin;

    public HttpHandler() {
        log.info("Loading HttpHandler...");
        register();
        javalin = Javalin.create(config -> {
            config.requestLogger((ctx, executionTimeMs) -> log.debug("[H] Request from " + ctx.ip() + " to " + ctx.path() + " took " + executionTimeMs + " ms"));
            config.enableCorsForAllOrigins();
            if (System.getenv("showmanager.debug") == null)
                config.addStaticFiles("/", System.getenv("showmanager.dist"), Location.EXTERNAL);
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
            before(ctx -> new RateLimit(ctx).requestPerTimeUnit(100, TimeUnit.MINUTES));
            //before(new AuthController());
            path("control", () -> {
                post("/play", __ -> ShowManager.getInstance().getWorker().play());
                post("/pause", __ -> ShowManager.getInstance().getWorker().pause());
                post("/stop", __ -> ShowManager.getInstance().getWorker().stop());
                post("/set", this::setTime);
            });
            path("output", () -> {
                OutputController controller = new OutputController();
                post("/artnet", controller::handleArtNet);
                post("/ltc", controller::handleLtc);
                post("/audio", controller::handleAudio);
                post("/scheduler", controller::handleScheduler);
            });
        });
    }

    @Override
    public void shutdown() throws Exception {
        log.info("Shutting down HTTP services...");
        javalin.stop();
    }

    //should move all control handling to a separate class
    private void setTime(Context ctx) {
        val data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (!data.has("hour") || !data.has("min") || !data.has("sec") || !data.has("frame"))
            throw new BadRequestResponse();
        ShowManager.getInstance().getWorker().setTime(new Timecode(data.get("hour").getAsInt(), data.get("min").getAsInt(), data.get("sec").getAsInt(), data.get("frame").getAsInt(), ShowManager.getInstance().getWorker().getFramerate()));
    }
}
