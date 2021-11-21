package me.sunstorm.showmanager.http;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import me.sunstorm.showmanager.audio.AudioPlayer;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.http.controller.AudioController;
import me.sunstorm.showmanager.injection.DependencyInjection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AudioTests {
    private AudioPlayer player;
    private EventBus eventBus;
    private Context ctx;

    @BeforeAll
    void setupDependencies() {
        player = mock(AudioPlayer.class);
        eventBus = mock(EventBus.class);
        ctx = mock(Context.class);
        DependencyInjection.registerProvider(AudioPlayer.class, () -> player);
        DependencyInjection.registerProvider(EventBus.class, () -> eventBus);
    }

    @Test
    void testVolume() {
        var controller = new AudioController();
        when(ctx.body()).thenReturn("{}");
        assertThatThrownBy(() -> controller.postVolume(ctx)).isInstanceOf(BadRequestResponse.class);
        when(ctx.body()).thenReturn("{\"volume\": 5}");
        controller.postVolume(ctx);
        var captor = ArgumentCaptor.forClass(Integer.class);
        verify(player).setVolume(captor.capture());
        assertThat(captor.getValue()).isEqualTo(5);
    }
}
