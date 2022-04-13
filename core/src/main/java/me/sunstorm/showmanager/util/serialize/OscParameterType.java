package me.sunstorm.showmanager.util.serialize;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum OscParameterType {
    INTEGER(Integer::parseInt, r -> Integer.toString((Integer) r)),
    FLOAT(Float::parseFloat, r -> Float.toString((Float) r)),
    BOOLEAN(Boolean::parseBoolean, r -> Boolean.toString((Boolean) r)),
    STRING(r -> r, String::valueOf),
    EMPTY(r -> null, r -> "");

    private final Function<String, Object> deserializer;
    private final Function<Object, String> serializer;

    @Nullable
    public Object convert(String raw) {
        if (raw.length() == 0) return null;
        return deserializer.apply(raw);
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
