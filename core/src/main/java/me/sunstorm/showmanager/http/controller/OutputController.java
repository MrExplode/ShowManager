package me.sunstorm.showmanager.http.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.ShowManager;

@Slf4j
public class OutputController {

    public static void handleArtNet(Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (data.get("enabled") == null)
            throw new BadRequestResponse();
        boolean value = data.get("enabled").getAsBoolean();
        log.info("ArtNet " + (value ? "enabled" : "disabled"));
        ShowManager.getInstance().getWorker().setArtNet(value);
    }

    public static void handleLtc(Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (data.get("enabled") == null)
            throw new BadRequestResponse();
        boolean value = data.get("enabled").getAsBoolean();
        log.info("LTC " + (value ? "enabled" : "disabled"));
        ShowManager.getInstance().getWorker().setLtc(value);
    }

    public static void handleAudio(Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (data.get("enabled") == null)
            throw new BadRequestResponse();
        boolean value = data.get("enabled").getAsBoolean();
        log.info("Audio " + (value ? "enabled" : "disabled"));
        ShowManager.getInstance().getAudioPlayer().setEnabled(value);
    }

    public static void handleScheduler(Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (data.get("enabled") == null)
            throw new BadRequestResponse();
        boolean value = data.get("enabled").getAsBoolean();
        log.info("Scheduler " + (value ? "enabled" : "disabled"));
        ShowManager.getInstance().getEventScheduler().setEnabled(value);
    }
}
