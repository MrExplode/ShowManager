package me.sunstorm.showmanager;

import me.sunstorm.showmanager.util.Framerate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class FramerateTests {

    @Test
    void testValidation() {
        assertThatThrownBy(() -> Framerate.set(32)).isInstanceOf(IllegalArgumentException.class);
    }
}
