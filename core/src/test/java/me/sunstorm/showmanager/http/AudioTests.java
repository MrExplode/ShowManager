package me.sunstorm.showmanager.http;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import me.sunstorm.showmanager.modules.audio.AudioModule;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.modules.http.controller.AudioController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AudioTests {
    private AudioModule player;
    private EventBus eventBus;
    private Context ctx;

    @BeforeAll
    void setupDependencies() {
        player = mock(AudioModule.class);
        eventBus = mock(EventBus.class);
        ctx = mock(Context.class);

    }

    @Test
    void testVolume() {
        var controller = new AudioController(eventBus, player, null);
        when(ctx.body()).thenReturn("{}");
        assertThatThrownBy(() -> controller.postVolume(ctx)).isInstanceOf(BadRequestResponse.class);
        when(ctx.body()).thenReturn("{\"volume\": 5}");
        controller.postVolume(ctx);
        var captor = ArgumentCaptor.forClass(Integer.class);
        verify(player).setVolume(captor.capture());
        assertThat(captor.getValue()).isEqualTo(5);
    }
}
