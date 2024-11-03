package me.sunstorm.showmanager.util.serialize;

import com.google.gson.*;
import com.illposed.osc.OSCMessage;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

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
        if (params == null || params.isEmpty()) {
            data.addProperty("parameterType", "EMPTY");
            data.addProperty("parameter", "");
        } else {
            Object parameter = params.getFirst();
            var type = OscParameterType.find(parameter);
            data.addProperty("parameterType", type.toString());
            data.addProperty("parameter", type.getSerializer().apply(parameter));
        }
        return data;
    }

}
