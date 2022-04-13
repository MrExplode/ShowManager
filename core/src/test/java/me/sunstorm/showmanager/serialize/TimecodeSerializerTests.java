package me.sunstorm.showmanager.serialize;

import com.google.gson.JsonObject;
import me.sunstorm.showmanager.Constants;
import me.sunstorm.showmanager.util.Framerate;
import me.sunstorm.showmanager.util.Timecode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TimecodeSerializerTests {

    @Test
    void testTimecodeSerialize() {
        Framerate.set(25);
        Timecode tc = new Timecode(1, 2, 3, 4);
        JsonObject data = Constants.GSON.toJsonTree(tc).getAsJsonObject();
        assertThat(data.has("hour")).isTrue();
        assertThat(data.has("min")).isTrue();
        assertThat(data.has("sec")).isTrue();
        assertThat(data.has("frame")).isTrue();
        assertThat(data.has("millisecLength")).isTrue();
        assertThat(data.get("hour").getAsInt()).isEqualTo(1);
        assertThat(data.get("min").getAsInt()).isEqualTo(2);
        assertThat(data.get("sec").getAsInt()).isEqualTo(3);
        assertThat(data.get("frame").getAsInt()).isEqualTo(4);
        assertThat(data.get("millisecLength").getAsInt()).isEqualTo(3723160);
    }

    @Test
    void testTimecodeDeserialize() {
        Framerate.set(25);
        Timecode tc = Constants.GSON.fromJson("{\"hour\":1,\"min\":2,\"sec\":3,\"frame\":4,\"millisecLength\":3723160}", Timecode.class);
        assertThat(tc).isNotNull();
        assertThat(tc.getHour()).isEqualTo(1);
        assertThat(tc.getMin()).isEqualTo(2);
        assertThat(tc.getSec()).isEqualTo(3);
        assertThat(tc.getFrame()).isEqualTo(4);
    }
}
