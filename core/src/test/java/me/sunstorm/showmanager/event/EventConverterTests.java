package me.sunstorm.showmanager.event;

import me.sunstorm.showmanager.eventsystem.events.audio.AudioVolumeChangeEvent;
import me.sunstorm.showmanager.eventsystem.registry.EventConverter;
import me.sunstorm.showmanager.eventsystem.registry.EventWrapper;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.*;

public class EventConverterTests {

    @Test
    void testEncode() {
        EventWrapper wrapper = new EventWrapper(5, false, new AudioVolumeChangeEvent(15));
        String excepted = "{\"id\":5,\"async\":false,\"event\":{\"volume\":15}}";
        EventConverter converter = new EventConverter();
        assertThat(converter.encode(wrapper)).isEqualTo(excepted.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void testDecode() {
        String input = "{\"id\":5,\"async\":false,\"event\":{\"volume\":15}}";
        EventWrapper excepted = new EventWrapper(5, false, new AudioVolumeChangeEvent(15));
        EventConverter converter = new EventConverter();
        assertThat(converter.decode(input.getBytes(StandardCharsets.UTF_8))).satisfies(w -> {
            assertThat(w.getId()).as("event id").isEqualTo(excepted.getId());
            assertThat(w.isAsync()).isEqualTo(excepted.isAsync());
            assertThat(w.getEvent()).isNotNull().isInstanceOf(AudioVolumeChangeEvent.class);
            assertThat(((AudioVolumeChangeEvent) w.getEvent()).getVolume()).isEqualTo(15);
        });
    }
}
