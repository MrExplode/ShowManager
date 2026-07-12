package me.sunstorm.showmanager.modules.http.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.EventCall;
import me.sunstorm.showmanager.eventsystem.Listener;
import me.sunstorm.showmanager.eventsystem.events.output.OutputToggleEvent;
import me.sunstorm.showmanager.modules.ToggleableModule;
import me.sunstorm.showmanager.modules.artnet.ArtNetModule;
import me.sunstorm.showmanager.modules.audio.AudioModule;
import me.sunstorm.showmanager.modules.http.WebSocketHandler;
import me.sunstorm.showmanager.modules.http.routing.annotate.Get;
import me.sunstorm.showmanager.modules.http.routing.annotate.PathPrefix;
import me.sunstorm.showmanager.modules.http.routing.annotate.Post;
import me.sunstorm.showmanager.modules.ltc.LtcModule;
import me.sunstorm.showmanager.modules.osc.OscModule;
import me.sunstorm.showmanager.modules.scheduler.SchedulerModule;
import me.sunstorm.showmanager.util.JsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;


@Singleton
@PathPrefix("/output")
public class OutputController implements Listener {
    private static final Logger log = LoggerFactory.getLogger(OutputController.class);

    private final EventBus eventBus;
    private final WebSocketHandler webSocketHandler;
    private final Map<String, ToggleableModule> outputs;

    @Inject
    public OutputController(EventBus eventBus, LtcModule ltcModule, ArtNetModule artNetModule, AudioModule player,
                            OscModule osc, SchedulerModule scheduler, WebSocketHandler webSocketHandler) {
        this.eventBus = eventBus;
        this.webSocketHandler = webSocketHandler;
        this.outputs = Map.of(
                "artnet", artNetModule,
                "ltc", ltcModule,
                "audio", player,
                "osc", osc,
                "scheduler", scheduler
        );
        eventBus.register(this);
    }

    @Get("/artnet")
    public void getArtNet(@NotNull Context ctx) {
        state(ctx, "artnet");
    }

    @Post("/artnet")
    public void postArtNet(@NotNull Context ctx) {
        toggle(ctx, "artnet");
    }

    @Get("/ltc")
    public void getLtc(@NotNull Context ctx) {
        state(ctx, "ltc");
    }

    @Post("/ltc")
    public void postLtc(@NotNull Context ctx) {
        toggle(ctx, "ltc");
    }

    @Get("/audio")
    public void getAudio(@NotNull Context ctx) {
        state(ctx, "audio");
    }

    @Post("/audio")
    public void postAudio(@NotNull Context ctx) {
        toggle(ctx, "audio");
    }

    @Get("/osc")
    public void getOsc(@NotNull Context ctx) {
        state(ctx, "osc");
    }

    @Post("/osc")
    public void postOsc(@NotNull Context ctx) {
        toggle(ctx, "osc");
    }

    @Get("/scheduler")
    public void getScheduler(@NotNull Context ctx) {
        state(ctx, "scheduler");
    }

    @Post("/scheduler")
    public void postScheduler(@NotNull Context ctx) {
        toggle(ctx, "scheduler");
    }

    @Get("/all")
    public void getAll(@NotNull Context ctx) {
        JsonObject data = new JsonObject();
        outputs.forEach((name, module) -> data.addProperty(name, module.isEnabled()));
        ctx.json(data);
    }

    /**
     * Runs on every node, for toggles from this node's UI and from peers alike.
     */
    @EventCall
    public void onOutputToggle(OutputToggleEvent event) {
        ToggleableModule module = outputs.get(event.getOutput());
        if (module == null) {
            log.warn("Ignoring toggle for unknown output '{}'", event.getOutput());
            return;
        }
        boolean value = event.isEnabled();
        log.info("{} {}", event.getOutput(), value ? "enabled" : "disabled");
        module.setEnabled(value);
        webSocketHandler.broadcast(
                new JsonBuilder()
                        .addProperty("type", "output")
                        .addProperty("name", event.getOutput())
                        .addProperty("value", value)
                        .build()
        );
    }

    private void state(@NotNull Context ctx, String output) {
        JsonObject data = new JsonObject();
        data.addProperty("enabled", outputs.get(output).isEnabled());
        ctx.json(data);
    }

    private void toggle(@NotNull Context ctx, String output) {
        JsonObject body = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (body.get("enabled") == null)
            throw new BadRequestResponse();
        new OutputToggleEvent(output, body.get("enabled").getAsBoolean()).call(eventBus);
    }
}
