package me.sunstorm.showmanager.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import io.javalin.http.util.RateLimit;
import io.javalin.plugin.json.JavalinJson;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import me.sunstorm.showmanager.Constants;
import me.sunstorm.showmanager.Worker;
import me.sunstorm.showmanager.http.controller.AudioController;
import me.sunstorm.showmanager.http.controller.OutputController;
import me.sunstorm.showmanager.http.controller.SchedulerController;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;
import me.sunstorm.showmanager.settings.SettingsHolder;
import me.sunstorm.showmanager.terminable.Terminable;
import me.sunstorm.showmanager.util.JsonBuilder;
import me.sunstorm.showmanager.util.Timecode;

import java.util.concurrent.TimeUnit;

import static io.javalin.apibuilder.ApiBuilder.*;

@Slf4j
public class HttpHandler extends SettingsHolder implements Terminable, InjectRecipient {
    private final Javalin javalin;
    private int port = 7000;
    @Getter
    private String header = "secret";
    @Getter
    private String secret = "XXXXXXXXXX";
    @Inject
    private Worker worker;

    public HttpHandler() {
        super("http-server");
        log.info("Loading HttpHandler...");
        inject();
        load();
        register();
        javalin = Javalin.create(config -> {
            config.requestLogger((ctx, executionTimeMs) -> log.debug("[H] Request from " + ctx.ip() + " to " + ctx.path() + " took " + executionTimeMs + " ms"));
            config.enableCorsForAllOrigins();
            if (System.getenv("showmanager.debug") == null)
                config.addStaticFiles("/", System.getenv("showmanager.dist"), Location.EXTERNAL);
        });
        JavalinJson.setToJsonMapper(Constants.GSON::toJson);
        JavalinJson.setFromJsonMapper(Constants.GSON::fromJson);
        javalin.start(port);
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
                get("/play", ctx -> ctx.json(new JsonBuilder().addProperty("playing", worker.isPlaying()).build()));
                post("/play", __ -> worker.play());
                post("/pause", __ -> worker.pause());
                post("/stop", __ -> worker.stop());
                post("/set", this::setTime);
            });
            path("output", () -> {
                OutputController controller = new OutputController();
                get("/artnet", controller::getArtNet);
                post("/artnet", controller::postArtNet);
                get("/ltc", controller::getLtc);
                post("/ltc", controller::postLtc);
                get("/audio", controller::getAudio);
                post("/audio", controller::postAudio);
                get("/scheduler", controller::getScheduler);
                post("/scheduler", controller::postScheduler);
                get("/all", controller::getAll);
            });
            path("scheduler", () -> {
                SchedulerController controller = new SchedulerController();
                get("/record", controller::getRecording);
                post("/record", controller::postRecording);
            });
            path("audio", () -> {
                AudioController controller = new AudioController();
                post("/volume", controller::postVolume);
                get("/info", controller::getInfo);
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
        worker.setTime(new Timecode(data.get("hour").getAsInt(), data.get("min").getAsInt(), data.get("sec").getAsInt(), data.get("frame").getAsInt(), worker.getFramerate()));
    }

    @Override
    public JsonObject getData() {
        JsonObject data = new JsonObject();
        data.addProperty("port", port);
        data.addProperty("header", header);
        data.addProperty("secret", secret);
        return data;
    }

    @Override
    public void onLoad(JsonObject object) {
        port = object.get("port").getAsInt();
        header = object.get("header").getAsString();
        secret = object.get("secret").getAsString();
    }
}
