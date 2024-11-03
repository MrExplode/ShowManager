package me.sunstorm.showmanager.util.serialize;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public enum OscParameterType {
    INTEGER(Integer::parseInt, r -> Integer.toString((Integer) r)),
    FLOAT(Float::parseFloat, r -> Float.toString((Float) r)),
    BOOLEAN(Boolean::parseBoolean, r -> Boolean.toString((Boolean) r)),
    STRING(r -> r, String::valueOf),
    EMPTY(r -> null, r -> "");

    private final Function<String, Object> deserializer;
    private final Function<Object, String> serializer;

    OscParameterType(Function<String, Object> deserializer, Function<Object, String> serializer) {
        this.deserializer = deserializer;
        this.serializer = serializer;
    }

    @Nullable
    public Object convert(String raw) {
        if (raw.isEmpty()) return null;
        return deserializer.apply(raw);
    }

    public Function<String, Object> getDeserializer() {
        return deserializer;
    }

    public Function<Object, String> getSerializer() {
        return serializer;
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
