package me.sunstorm.showmanager.http.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import me.sunstorm.showmanager.audio.AudioPlayer;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;

public class AudioController implements InjectRecipient {
    @Inject
    private AudioPlayer player;

    public AudioController() {
        inject();
    }

    public void handleVolume(Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (!data.has("volume"))
            throw new BadRequestResponse();
        player.setVolume(data.get("volume").getAsInt());
    }
}
