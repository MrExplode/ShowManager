package me.sunstorm.showmanager.util.serialize;

import com.google.gson.*;
import me.sunstorm.showmanager.ShowManager;
import me.sunstorm.showmanager.modules.audio.AudioTrack;
import me.sunstorm.showmanager.modules.audio.marker.Marker;
import me.sunstorm.showmanager.util.Timecode;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

public class AudioTrackSerializer implements JsonSerializer<AudioTrack>, JsonDeserializer<AudioTrack> {
    @Override
    public AudioTrack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject data = json.getAsJsonObject();
        if (!data.has("start")) throw new JsonParseException("Audio track has no 'start' timecode");
        if (!data.has("path")) throw new JsonParseException("Audio track has no 'path'");
        Timecode start = context.deserialize(data.get("start"), Timecode.class);
        List<Marker> markers = data.has("markers") ? context.deserialize(data.get("markers"), List.class) : List.of();
        var instance = new AudioTrack(start, new File(data.get("path").getAsString()), markers);
        if (data.has("volume")) {
            instance.setVolume(Math.round(data.get("volume").getAsFloat() * 100));
        }
        if (ShowManager.FEATHER != null) {
            ShowManager.FEATHER.injectFields(instance);
        }
        return instance;
    }

    @Override
    public JsonElement serialize(AudioTrack track, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject data = new JsonObject();
        data.add("start", context.serialize(track.getStartTime()));
        data.addProperty("volume", track.getVolume());
        data.addProperty("path", track.getFile().getAbsolutePath());
        data.add("markers", context.serialize(track.getMarkers()));
        if (track.getEndTime() != null) {
            data.add("end", context.serialize(track.getEndTime()));
        }
        return data;
    }
}
