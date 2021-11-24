package me.sunstorm.showmanager.http;

import com.google.gson.JsonObject;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.http.util.NaiveRateLimit;
import io.javalin.http.util.RateLimiter;
import io.javalin.plugin.json.JsonMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.Constants;
import me.sunstorm.showmanager.Worker;
import me.sunstorm.showmanager.http.controller.AudioController;
import me.sunstorm.showmanager.http.controller.ControlController;
import me.sunstorm.showmanager.http.controller.OutputController;
import me.sunstorm.showmanager.http.controller.SchedulerController;
import me.sunstorm.showmanager.http.routing.RoutingManager;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;
import me.sunstorm.showmanager.settings.SettingsHolder;
import me.sunstorm.showmanager.terminable.Terminable;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

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
            config.requestLogger((ctx, executionTimeMs) -> log.debug("[H] Request from {} to {} took {} ms", ctx.ip(), ctx.path(), executionTimeMs));
            config.enableCorsForAllOrigins();
            config.jsonMapper(new JsonMapper() {
                @NotNull
                @Override
                public String toJsonString(@NotNull Object obj) {
                    return Constants.GSON.toJson(obj);
                }

                @NotNull
                @Override
                public <T> T fromJsonStream(@NotNull InputStream json, @NotNull Class<T> targetClass) {
                    return Constants.GSON.fromJson(new BufferedReader(new InputStreamReader(json)), targetClass);
                }
            });
            if (System.getenv("showmanager.debug") == null) {
                config.addStaticFiles(staticConfig -> {
                    staticConfig.hostedPath = "/";
                    staticConfig.location = Location.EXTERNAL;
                    staticConfig.directory = System.getenv("showmanager.dist") != null ? System.getenv("showmanager.dist") : System.getProperty("showmanager.dist");
                });
            }
        });
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
        javalin.before(ctx -> NaiveRateLimit.requestPerTimeUnit(ctx, 100, TimeUnit.MINUTES));
        RoutingManager.create(javalin,
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
        data.addProperty("port", port);
        data.addProperty("header", header);
        data.addProperty("secret", secret);
        return data;
    }

    @Override
    public void onLoad(@NotNull JsonObject object) {
        port = object.get("port").getAsInt();
        header = object.get("header").getAsString();
        secret = object.get("secret").getAsString();
    }
}
