package me.sunstorm.showmanager.modules.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.http.util.NaiveRateLimit;
import io.javalin.json.JsonMapper;
import io.javalin.plugin.bundled.CorsPluginConfig;
import me.sunstorm.showmanager.Constants;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.modules.http.controller.AudioController;
import me.sunstorm.showmanager.modules.http.routing.RoutingManager;
import me.sunstorm.showmanager.modules.Module;
import me.sunstorm.showmanager.modules.http.controller.ControlController;
import me.sunstorm.showmanager.modules.http.controller.OutputController;
import me.sunstorm.showmanager.modules.http.controller.SchedulerController;
import org.codejargon.feather.Feather;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

public class HttpModule extends Module {
    private static final Logger log = LoggerFactory.getLogger(HttpModule.class);

    private final Javalin javalin;
    private String host = "127.0.0.1";
    private int port = 7000;
    private String header = "secret";
    private String secret = "XXXXXXXXXX";

    private final Feather feather;

    @Inject
    public HttpModule(EventBus bus, Feather feather) {
        super(bus);
        this.feather = feather;
        init();

        javalin = Javalin.create(config -> {
            config.requestLogger.http((ctx, ms) -> log.debug("[H] Request from {} to {} took {} ms", ctx.ip(), ctx.path(), ms));
            config.jsonMapper(new JsonMapper() {
                @NotNull
                @Override
                public String toJsonString(@NotNull Object obj, @NotNull Type type) {
                    return Constants.GSON.toJson(obj);
                }

                @NotNull
                @Override
                public <T> T fromJsonStream(@NotNull InputStream json, @NotNull Type targetType) {
                    return Constants.GSON.fromJson(new BufferedReader(new InputStreamReader(json)), targetType);
                }
            });
            if (System.getenv("showmanager.debug") == null) {
                var directory = System.getenv("showmanager.dist") != null ? System.getenv("showmanager.dist") : System.getProperty("showmanager.dist");
                if (directory != null) {
                    config.staticFiles.add(directory, Location.EXTERNAL);
                } else {
                    log.warn("Couldn't find frontend location, did you specify it correctly?");
                }
            } else {
                log.info("showmanager.debug environment value present, starting without serving frontend");
            }

            config.bundledPlugins.enableCors(cors -> cors.addRule(CorsPluginConfig.CorsRule::anyHost));
        });
        javalin.start(host, port);
        setupRouting();
    }

    private void setupRouting() {
        javalin.ws("", ws -> {
            WebSocketHandler wsHandler = feather.instance(WebSocketHandler.class);
            ws.onConnect(wsHandler);
            ws.onClose(wsHandler);
            ws.onMessage(wsHandler);
            ws.onError(wsHandler);
        });
        javalin.before(ctx -> NaiveRateLimit.requestPerTimeUnit(ctx, 100, TimeUnit.MINUTES));
        RoutingManager.create(javalin,
                feather::instance,
                AudioController.class,
                ControlController.class,
                OutputController.class,
                SchedulerController.class
        );
    }

    @Override
    public void shutdown() {
        log.info("Shutting down HTTP services...");
        javalin.stop();
    }

    @NotNull
    @Override
    public JsonObject getData() {
        JsonObject data = new JsonObject();
        data.addProperty("host", host);
        data.addProperty("port", port);
        data.addProperty("header", header);
        data.addProperty("secret", secret);
        return data;
    }

    @Override
    public void onLoad(@NotNull JsonElement element) {
        var object = element.getAsJsonObject();
        host = object.get("host").getAsString();
        port = object.get("port").getAsInt();
        header = object.get("header").getAsString();
        secret = object.get("secret").getAsString();
    }

    @Override
    public String getName() {
        return "http-server";
    }

    // generated

    public String getHeader() {
        return header;
    }

    public String getSecret() {
        return secret;
    }
}
