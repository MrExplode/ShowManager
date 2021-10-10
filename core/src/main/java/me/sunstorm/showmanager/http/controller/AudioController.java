package me.sunstorm.showmanager.http.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import io.javalin.http.MethodNotAllowedResponse;
import me.sunstorm.showmanager.audio.AudioPlayer;
import me.sunstorm.showmanager.audio.marker.Marker;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;
import me.sunstorm.showmanager.util.JsonBuilder;
import me.sunstorm.showmanager.util.Timecode;

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
            data.add("markers", new JsonArray());
        } else {
            data.addProperty("loaded", player.getCurrent().getName());
            data.addProperty("volume", (int) (player.getCurrent().getVolume() * 100));
            data.addProperty("playing", player.getCurrent().getClip().isRunning());
            data.add("markers", buildMarkers());
        }
        ctx.json(data);
    }

    public void getMarkers(Context ctx) {
        JsonObject data = new JsonObject();
        data.add("markers", buildMarkers());
        ctx.json(data);
    }

    public void markerJump(Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (player.getCurrent() == null || !data.has("name"))
            throw new BadRequestResponse();
        player.getCurrent().getMarkers().stream().filter(m -> m.getLabel().equals(data.get("name").getAsString())).findFirst().get().jump();
    }

    public void addMarker(Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (player.getCurrent() == null)
            throw new BadRequestResponse();
        player.getCurrent().getMarkers().add(new Marker(data.get("name").getAsString(), new Timecode(
                data.get("hour").getAsInt(),
                data.get("min").getAsInt(),
                data.get("sec").getAsInt(),
                data.get("frame").getAsInt(),
                25
        )));
    }

    public void deleteMarker(Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (player.getCurrent() == null || !data.has("name"))
            throw new BadRequestResponse();
        player.getCurrent().getMarkers().removeIf(m -> m.getLabel().equals(data.get("name").getAsString()));
    }

    private JsonArray buildMarkers() {
        JsonArray array = new JsonArray();
        player.getCurrent().getMarkers().forEach(m -> array.add(new JsonBuilder().addProperty("label", m.getLabel()).addProperty("time", m.getTime().guiFormatted(false)).build()));
        return array;
    }
}
