package me.sunstorm.showmanager.modules.http.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import me.sunstorm.showmanager.Constants;
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
import me.sunstorm.showmanager.util.Exceptions;
import me.sunstorm.showmanager.util.JsonBuilder;
import me.sunstorm.showmanager.util.Timecode;
import me.sunstorm.showmanager.util.WaveformRunner;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@PathPrefix("/audio")
public class AudioController {
    private static final Logger log = LoggerFactory.getLogger(AudioController.class);

    private final WaveformRunner waveformRunner;

    private final EventBus eventBus;
    private final AudioModule player;
    private final WebSocketHandler wsHandler;

    @Inject
    public AudioController(EventBus eventBus, AudioModule player, WebSocketHandler wsHandler) {
        this.eventBus = eventBus;
        this.player = player;
        this.wsHandler = wsHandler;

        this.waveformRunner = new WaveformRunner(Path.of(System.getenv("showmanager.audiowaveform")));
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
        data.add("availableTracks", Constants.GSON.toJsonTree(player.getTracks()));
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
        var marker = player.getCurrent().getMarkers().stream().filter(m -> m.getLabel().equals(data.get("name").getAsString())).findFirst().orElse(null);
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
        var marker = new Marker(data.get("name").getAsString(), new Timecode(
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

    @Get("/samples")
    public void getSamples(@NotNull Context ctx) {
        if (player.getCurrent() == null) throw new BadRequestResponse("No tracks are loaded");
        try (var out = ctx.outputStream()) {
            out.write(waveformRunner.sample(player.getCurrent().getFile().toPath()));
        } catch (IOException e) {
            Exceptions.sneaky(e);
        }
    }

    // actually not needed for peaks.js, HOWEVER I might do something later on the ui with the sound
    // maybe a sound preview?
    @Get("/raw")
    public void getAudio(@NotNull Context ctx) {
        if (player.getCurrent() == null) throw new BadRequestResponse("No tracks are loaded");
        try (var out = ctx.outputStream()) {
            var data = Files.readAllBytes(player.getCurrent().getFile().toPath());
            ctx.header("Content-Type", "audio/wav");
            ctx.header("Content-Length", data.length + "");
            out.write(data);
        } catch (IOException e) {
            Exceptions.sneaky(e);
        }
    }

    @NotNull
    private JsonArray buildMarkers() {
        JsonArray array = new JsonArray();
        player.getCurrent().getMarkers().forEach(m -> array.add(
                new JsonBuilder()
                .addProperty("label", m.getLabel())
                .addProperty("time", m.getTime().guiFormatted(false))
                .build()
        ));
        return array;
    }
}
