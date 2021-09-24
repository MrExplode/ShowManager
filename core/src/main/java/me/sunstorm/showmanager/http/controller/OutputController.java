package me.sunstorm.showmanager.http.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import me.sunstorm.showmanager.ShowManager;

public class OutputController {

    public static void handleArtNet(Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (data.get("enabled") == null)
            throw new BadRequestResponse();
        ShowManager.getInstance().getWorker().setArtNet(data.get("enabled").getAsBoolean());
    }

    public static void handleLtc(Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (data.get("enabled") == null)
            throw new BadRequestResponse();
        ShowManager.getInstance().getWorker().setLtc(data.get("enabled").getAsBoolean());
    }

    public static void handleAudio(Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (data.get("enabled") == null)
            throw new BadRequestResponse();
        ShowManager.getInstance().getAudioPlayer().setEnabled(data.get("enabled").getAsBoolean());
    }

    public static void handleScheduler(Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (data.get("enabled") == null)
            throw new BadRequestResponse();
        ShowManager.getInstance().getEventScheduler().setEnabled(data.get("enabled").getAsBoolean());
    }
}
