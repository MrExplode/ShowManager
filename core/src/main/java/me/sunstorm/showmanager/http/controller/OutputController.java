package me.sunstorm.showmanager.http.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import io.javalin.http.ServiceUnavailableResponse;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.Worker;
import me.sunstorm.showmanager.artnet.ArtNetHandler;
import me.sunstorm.showmanager.audio.AudioPlayer;
import me.sunstorm.showmanager.http.WebSocketHandler;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;
import me.sunstorm.showmanager.scheduler.EventScheduler;
import me.sunstorm.showmanager.util.JsonBuilder;

@Slf4j
@Inject
public class OutputController implements InjectRecipient {
    private Worker worker;
    private ArtNetHandler artNetHandler;
    private AudioPlayer player;
    private EventScheduler scheduler;
    private WebSocketHandler webSocketHandler;

    public OutputController() {
        inject();
    }

    public void getArtNet(Context ctx) {
        JsonObject data = new JsonObject();
        data.addProperty("enabled", artNetHandler.isEnabled());
        ctx.json(data);
    }

    public void postArtNet(Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (data.get("enabled") == null)
            throw new BadRequestResponse();
        boolean value = data.get("enabled").getAsBoolean();
        log.info("ArtNet " + (value ? "enabled" : "disabled"));
        artNetHandler.setEnabled(value);
        update("artnet", value);
    }

    public void getLtc(Context ctx) {
        throw new ServiceUnavailableResponse();
    }

    public void postLtc(Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (data.get("enabled") == null)
            throw new BadRequestResponse();
        boolean value = data.get("enabled").getAsBoolean();
        log.info("LTC " + (value ? "enabled" : "disabled"));
        worker.setLtc(value);
        update("ltc", value);
    }

    public void getAudio(Context ctx) {
        JsonObject data = new JsonObject();
        data.addProperty("enabled", player.isEnabled());
        ctx.json(data);
    }

    public void postAudio(Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (data.get("enabled") == null)
            throw new BadRequestResponse();
        boolean value = data.get("enabled").getAsBoolean();
        log.info("Audio " + (value ? "enabled" : "disabled"));
        player.setEnabled(value);
        update("audio", value);
    }

    public void getScheduler(Context ctx) {
        JsonObject data = new JsonObject();
        data.addProperty("enabled", scheduler.isEnabled());
        ctx.json(data);
    }

    public void postScheduler(Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (data.get("enabled") == null)
            throw new BadRequestResponse();
        boolean value = data.get("enabled").getAsBoolean();
        log.info("Scheduler " + (value ? "enabled" : "disabled"));
        scheduler.setEnabled(value);
        update("scheduler", value);
    }

    public void getAll(Context ctx) {
        JsonObject data = new JsonObject();
        data.addProperty("artnet", artNetHandler.isEnabled());
        data.addProperty("ltc", false);
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
