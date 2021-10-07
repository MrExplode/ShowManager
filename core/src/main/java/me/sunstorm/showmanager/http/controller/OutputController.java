package me.sunstorm.showmanager.http.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.Worker;
import me.sunstorm.showmanager.artnet.ArtNetHandler;
import me.sunstorm.showmanager.audio.AudioPlayer;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;
import me.sunstorm.showmanager.scheduler.EventScheduler;

@Slf4j
@Inject
public class OutputController implements InjectRecipient {
    private Worker worker;
    private ArtNetHandler artNetHandler;
    private AudioPlayer player;
    private EventScheduler scheduler;

    public OutputController() {
        inject();
    }

    public void handleArtNet(Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (data.get("enabled") == null)
            throw new BadRequestResponse();
        boolean value = data.get("enabled").getAsBoolean();
        log.info("ArtNet " + (value ? "enabled" : "disabled"));
        artNetHandler.setEnabled(value);
    }

    public void handleLtc(Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (data.get("enabled") == null)
            throw new BadRequestResponse();
        boolean value = data.get("enabled").getAsBoolean();
        log.info("LTC " + (value ? "enabled" : "disabled"));
        worker.setLtc(value);
    }

    public void handleAudio(Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (data.get("enabled") == null)
            throw new BadRequestResponse();
        boolean value = data.get("enabled").getAsBoolean();
        log.info("Audio " + (value ? "enabled" : "disabled"));
        player.setEnabled(value);
    }

    public void handleScheduler(Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (data.get("enabled") == null)
            throw new BadRequestResponse();
        boolean value = data.get("enabled").getAsBoolean();
        log.info("Scheduler " + (value ? "enabled" : "disabled"));
        scheduler.setEnabled(value);
    }
}
