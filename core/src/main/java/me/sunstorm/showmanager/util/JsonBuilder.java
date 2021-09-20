package me.sunstorm.showmanager.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonBuilder {
    private final JsonObject json = new JsonObject();

    public JsonBuilder addProperty(String property, String value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonBuilder addProperty(String property, Number value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonBuilder addProperty(String property, Boolean value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonBuilder addProperty(String property, Character value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonBuilder add(String property, Object value) {
        if (value instanceof String) {
            addProperty(property, (String) value);
        } else if (value instanceof Number) {
            addProperty(property, (Number) value);
        } else if (value instanceof Boolean) {
            addProperty(property, (Boolean) value);
        } else if (value instanceof Character) {
            addProperty(property, (Character) value);
        }else {
            add(property, value.toString());
        }

        return this;
    }

    public JsonBuilder add(String property, JsonElement element) {
        this.json.add(property, element);
        return this;
    }

    public JsonBuilder addElement(String property, JsonElement element) {
        this.json.add(property, element);
        return this;
    }

    public JsonBuilder addArray(String property, JsonArray array) {
        this.json.add(property, array);
        return this;
    }

    public JsonObject build() {
        return this.json;
    }
}
