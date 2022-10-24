package me.sunstorm.showmanager.modules.http.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.Worker;
import me.sunstorm.showmanager.modules.artnet.ArtNetModule;
import me.sunstorm.showmanager.modules.audio.AudioModule;
import me.sunstorm.showmanager.modules.http.WebSocketHandler;
import me.sunstorm.showmanager.modules.http.routing.annotate.Get;
import me.sunstorm.showmanager.modules.http.routing.annotate.PathPrefix;
import me.sunstorm.showmanager.modules.http.routing.annotate.Post;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;
import me.sunstorm.showmanager.modules.ltc.LtcModule;
import me.sunstorm.showmanager.modules.scheduler.SchedulerModule;
import me.sunstorm.showmanager.util.JsonBuilder;
import org.jetbrains.annotations.NotNull;

@Slf4j
@Inject
@PathPrefix("/output")
public class OutputController implements InjectRecipient {
    private Worker worker;
    private LtcModule ltcModule;
    private ArtNetModule artNetModule;
    private AudioModule player;
    private SchedulerModule scheduler;
    private WebSocketHandler webSocketHandler;

    public OutputController() {
        inject();
    }

    @Get("/artnet")
    public void getArtNet(@NotNull Context ctx) {
        JsonObject data = new JsonObject();
        data.addProperty("enabled", artNetModule.isEnabled());
        ctx.json(data);
    }

    @Post("/artnet")
    public void postArtNet(@NotNull Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (data.get("enabled") == null)
            throw new BadRequestResponse();
        boolean value = data.get("enabled").getAsBoolean();
        log.info("ArtNet " + (value ? "enabled" : "disabled"));
        artNetModule.setEnabled(value);
        update("artnet", value);
    }

    @Get("/ltc")
    public void getLtc(@NotNull Context ctx) {
        JsonObject data = new JsonObject();
        data.addProperty("enabled", ltcModule.isEnabled());
        ctx.json(data);
    }

    @Post("/ltc")
    public void postLtc(@NotNull Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (data.get("enabled") == null)
            throw new BadRequestResponse();
        boolean value = data.get("enabled").getAsBoolean();
        log.info("LTC " + (value ? "enabled" : "disabled"));
        ltcModule.setEnabled(value);
        update("ltc", value);
    }

    @Get("/audio")
    public void getAudio(@NotNull Context ctx) {
        JsonObject data = new JsonObject();
        data.addProperty("enabled", player.isEnabled());
        ctx.json(data);
    }

    @Post("/audio")
    public void postAudio(@NotNull Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (data.get("enabled") == null)
            throw new BadRequestResponse();
        boolean value = data.get("enabled").getAsBoolean();
        log.info("Audio " + (value ? "enabled" : "disabled"));
        player.setEnabled(value);
        update("audio", value);
    }

    @Get("/scheduler")
    public void getScheduler(@NotNull Context ctx) {
        JsonObject data = new JsonObject();
        data.addProperty("enabled", scheduler.isEnabled());
        ctx.json(data);
    }

    @Post("/scheduler")
    public void postScheduler(@NotNull Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (data.get("enabled") == null)
            throw new BadRequestResponse();
        boolean value = data.get("enabled").getAsBoolean();
        log.info("Scheduler " + (value ? "enabled" : "disabled"));
        scheduler.setEnabled(value);
        update("scheduler", value);
    }

    @Get("/all")
    public void getAll(@NotNull Context ctx) {
        JsonObject data = new JsonObject();
        data.addProperty("artnet", artNetModule.isEnabled());
        data.addProperty("ltc", ltcModule.isEnabled());
        data.addProperty("audio", player.isEnabled());
        data.addProperty("scheduler", scheduler.isEnabled());
        ctx.json(data);
    }

    private void update(String name, boolean value) {
        webSocketHandler.broadcast(
                new JsonBuilder()
                .addProperty("type", "output")
                .addProperty("name", name)
                .addProperty("value", value)
                .build()
        );
    }
}
