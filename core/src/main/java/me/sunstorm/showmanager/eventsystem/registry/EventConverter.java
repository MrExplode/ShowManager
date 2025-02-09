package me.sunstorm.showmanager.eventsystem.registry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.sunstorm.showmanager.Constants;
import me.sunstorm.showmanager.eventsystem.events.Event;
import me.sunstorm.showmanager.redis.converter.Converter;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public class EventConverter implements Converter<EventWrapper> {
    @Override
    public byte[] encode(@NotNull EventWrapper message) {
        JsonObject object = new JsonObject();
        object.addProperty("id", message.id());
        object.addProperty("async", message.async());
        object.add("event", Constants.GSON.toJsonTree(message.event()));
        return object.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public EventWrapper decode(byte[] message) {
        JsonObject object = JsonParser.parseString(new String(message, StandardCharsets.UTF_8)).getAsJsonObject();
        int id = object.get("id").getAsInt();
        boolean async = object.get("async").getAsBoolean();
        Event event = Constants.GSON.fromJson(object.get("event").getAsJsonObject(), EventRegistry.REGISTRY.get(id));
        return new EventWrapper(id, async, event);
    }
}
