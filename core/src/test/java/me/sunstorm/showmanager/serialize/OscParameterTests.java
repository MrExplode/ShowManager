package me.sunstorm.showmanager.serialize;

import me.sunstorm.showmanager.util.serialize.OscParameterType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class OscParameterTests {

    @Test
    void testTypeFind() {
        assertThat(OscParameterType.find(5)).isEqualTo(OscParameterType.INTEGER);
        assertThat(OscParameterType.find(0.5f)).isEqualTo(OscParameterType.FLOAT);
        assertThat(OscParameterType.find(false)).isEqualTo(OscParameterType.BOOLEAN);
        assertThat(OscParameterType.find("hello")).isEqualTo(OscParameterType.STRING);
        assertThat(OscParameterType.find(new Object())).isEqualTo(OscParameterType.EMPTY);
    }

    @Test
    void testDeserialize() {
        assertThat(OscParameterType.INTEGER.convert("5")).isEqualTo(5);
        assertThat(OscParameterType.FLOAT.convert("0.5")).isEqualTo(0.5f);
        assertThat(OscParameterType.BOOLEAN.convert("false")).isEqualTo(false);
        assertThat(OscParameterType.STRING.convert("hello")).isEqualTo("hello");
        assertThat(OscParameterType.EMPTY.convert("empty")).isNull();
    }

    @Test
    void testSerialize() {
        assertThat(OscParameterType.INTEGER.getSerializer().apply(5)).isEqualTo("5");
        assertThat(OscParameterType.FLOAT.getSerializer().apply(0.5f)).isEqualTo("0.5");
        assertThat(OscParameterType.BOOLEAN.getSerializer().apply(false)).isEqualTo("false");
        assertThat(OscParameterType.STRING.getSerializer().apply("hello")).isEqualTo("hello");
        assertThat(OscParameterType.EMPTY.getSerializer().apply("empty")).isEqualTo("");
    }
}
