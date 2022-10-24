package me.sunstorm.showmanager.modules.http.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import me.sunstorm.showmanager.modules.audio.AudioModule;
import me.sunstorm.showmanager.modules.audio.marker.Marker;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.events.marker.MarkerCreateEvent;
import me.sunstorm.showmanager.eventsystem.events.marker.MarkerDeleteEvent;
import me.sunstorm.showmanager.eventsystem.events.marker.MarkerJumpEvent;
import me.sunstorm.showmanager.modules.http.WebSocketHandler;
import me.sunstorm.showmanager.modules.http.routing.annotate.Get;
import me.sunstorm.showmanager.modules.http.routing.annotate.PathPrefix;
import me.sunstorm.showmanager.modules.http.routing.annotate.Post;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;
import me.sunstorm.showmanager.util.JsonBuilder;
import me.sunstorm.showmanager.util.Timecode;
import org.jetbrains.annotations.NotNull;

@Slf4j
@PathPrefix("/audio")
public class AudioController implements InjectRecipient {
    @Inject
    private AudioModule player;
    @Inject
    private EventBus eventBus;
    @Inject
    private WebSocketHandler wsHandler;

    public AudioController() {
        inject();
    }

    @Post("/volume")
    public void postVolume(@NotNull Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (!data.has("volume"))
            throw new BadRequestResponse();
        player.setVolume(data.get("volume").getAsInt());
    }

    @Get("/info")
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

    @Get("/markers")
    public void getMarkers(@NotNull Context ctx) {
        JsonObject data = new JsonObject();
        data.add("markers", buildMarkers());
        ctx.json(data);
    }

    @Post("/markers/jump")
    public void markerJump(@NotNull Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (player.getCurrent() == null || !data.has("name"))
            throw new BadRequestResponse();
        val marker = player.getCurrent().getMarkers().stream().filter(m -> m.getLabel().equals(data.get("name").getAsString())).findFirst().orElse(null);
        if (marker != null) {
            log.info("Jumping to marker {} - {}", marker.getLabel(), marker.getTime().guiFormatted(false));
            new MarkerJumpEvent(marker).call(eventBus);
            marker.jump();
        }
    }

    @Post("/markers/add")
    public void addMarker(@NotNull Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (player.getCurrent() == null)
            throw new BadRequestResponse();
        val marker = new Marker(data.get("name").getAsString(), new Timecode(
                data.get("hour").getAsInt(),
                data.get("min").getAsInt(),
                data.get("sec").getAsInt(),
                data.get("frame").getAsInt()
        ));
        log.info("Adding marker {} - {}", marker.getLabel(), marker.getTime().guiFormatted(true));
        player.getCurrent().getMarkers().add(marker);
        new MarkerCreateEvent(marker).call(eventBus);
    }

    @Post("/markers/delete")
    public void deleteMarker(@NotNull Context ctx) {
        JsonObject data = JsonParser.parseString(ctx.body()).getAsJsonObject();
        if (player.getCurrent() == null || !data.has("name"))
            throw new BadRequestResponse();
        boolean success = player.getCurrent().getMarkers().removeIf(m -> m.getLabel().equals(data.get("name").getAsString()));
        if (success)
            log.info("Deleted marker {}", data.get("name").getAsString());
        else
            log.warn("Attempted to delete non-existing marker: {}", data.get("name").getAsString());
        new MarkerDeleteEvent().call(eventBus);
    }

    @NotNull
    private JsonArray buildMarkers() {
        JsonArray array = new JsonArray();
        player.getCurrent().getMarkers().forEach(m -> array.add(new JsonBuilder().addProperty("label", m.getLabel()).addProperty("time", m.getTime().guiFormatted(false)).build()));
        return array;
    }
}
