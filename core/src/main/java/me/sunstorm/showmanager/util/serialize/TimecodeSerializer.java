package me.sunstorm.showmanager.util.serialize;

import com.google.gson.*;
import me.sunstorm.showmanager.util.Timecode;

import java.lang.reflect.Type;

public class TimecodeSerializer implements JsonSerializer<Timecode>, JsonDeserializer<Timecode> {

    @Override
    public Timecode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject data = json.getAsJsonObject();
        int hour = data.get("hour").getAsInt();
        int min = data.get("min").getAsInt();
        int sec = data.get("sec").getAsInt();
        int frame = data.get("frame").getAsInt();
        return new Timecode(hour, min, sec, frame);
    }

    @Override
    public JsonElement serialize(Timecode src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject data = new JsonObject();
        data.addProperty("hour", src.getHour());
        data.addProperty("min", src.getMin());
        data.addProperty("sec", src.getSec());
        data.addProperty("frame", src.getFrame());
        data.addProperty("millisecLength", src.getMillisecLength());
        return data;
    }
}
