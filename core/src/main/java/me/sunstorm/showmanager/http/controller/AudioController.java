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

    public void postVolume(Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (!data.has("volume"))
            throw new BadRequestResponse();
        player.setVolume(data.get("volume").getAsInt());
    }

    public void getInfo(Context ctx) {
        JsonObject data = new JsonObject();
        if (player.getCurrent() == null) {
            data.addProperty("loaded", "");
            data.addProperty("volume", 100);
            data.addProperty("playing", false);
        } else {
            data.addProperty("loaded", player.getCurrent().getName());
            data.addProperty("volume", (int) (player.getCurrent().getVolume() * 100));
            data.addProperty("playing", player.getCurrent().getClip().isRunning());
        }
        ctx.json(data);
    }
}
