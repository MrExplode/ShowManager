package me.sunstorm.showmanager.util.serialize;

import com.google.gson.*;
import com.illposed.osc.OSCMessage;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class OscMessageSerializer implements JsonSerializer<OSCMessage>, JsonDeserializer<OSCMessage> {

    @Override
    public OSCMessage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject data = json.getAsJsonObject();
        String address = data.get("address").getAsString();
        Object parameter = OscParameterType.valueOf(data.get("parameterType").getAsString().toUpperCase()).convert(data.get("parameter").getAsString());
        return new OSCMessage(address, parameter == null ? Collections.emptyList() : Collections.singletonList(parameter));
    }

    @Override
    public JsonElement serialize(OSCMessage src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject data = new JsonObject();
        data.addProperty("address", src.getAddress());
        List<Object> params = src.getArguments();
        if (params == null || params.size() == 0) {
            data.addProperty("parameterType", "EMPTY");
            data.addProperty("parameter", "");
        } else {
            Object parameter = params.get(0);
            val type = OscParameterType.find(parameter);
            data.addProperty("parameterType", type.toString());
            data.addProperty("parameter", type.serialize.apply(parameter));
        }
        return data;
    }

    @AllArgsConstructor
    private enum OscParameterType {
        INTEGER(Integer::parseInt, r -> Integer.toString((Integer) r)),
        FLOAT(Float::parseFloat, r -> Float.toString((Float) r)),
        BOOLEAN(Boolean::parseBoolean, r -> Boolean.toString((Boolean) r)),
        STRING(r -> r, String::valueOf),
        EMPTY(r -> null, r -> "");

        private final Function<String, Object> deserialize;
        private final Function<Object, String> serialize;

        @Nullable
        public Object convert(String raw) {
            if (raw.length() == 0) return null;
            return deserialize.apply(raw);
        }

        public static OscParameterType find(Object obj) {
            if (obj.getClass().equals(Integer.class))
                return INTEGER;
            else if (obj.getClass().equals(Float.class))
                return FLOAT;
            else if (obj.getClass().equals(Boolean.class))
                return BOOLEAN;
            else if (obj.getClass().equals(String.class))
                return STRING;
            else
                return EMPTY;
        }
    }
}
