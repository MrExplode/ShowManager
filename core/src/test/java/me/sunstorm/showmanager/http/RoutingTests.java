package me.sunstorm.showmanager.http;

import io.javalin.Javalin;
import io.javalin.http.Handler;
import me.sunstorm.showmanager.http.impl.PrefixDummy;
import me.sunstorm.showmanager.http.impl.RouteDummy;
import me.sunstorm.showmanager.modules.http.routing.RoutingManager;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoutingTests {

    @Test
    void testGetRouting() {
        Javalin javalin = mock(Javalin.class);
        RoutingManager.create(javalin, RouteDummy.class);
        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(javalin).get(pathCaptor.capture(), handlerCaptor.capture());
        assertThat(pathCaptor.getValue()).isEqualTo("/test/path/1");
    }

    @Test
    void testPostRouting() {
        Javalin javalin = mock(Javalin.class);
        RoutingManager.create(javalin, RouteDummy.class);
        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(javalin).post(pathCaptor.capture(), handlerCaptor.capture());
        assertThat(pathCaptor.getValue()).isEqualTo("/test/path/2");
    }

    @Test
    void testPathPrefix() {
        Javalin javalin = mock(Javalin.class);
        RoutingManager.create(javalin, PrefixDummy.class);
        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(javalin).get(pathCaptor.capture(), handlerCaptor.capture());
        assertThat(pathCaptor.getValue()).isEqualTo("/prefix/test/path");
    }
}
