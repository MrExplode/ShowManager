package me.sunstorm.showmanager.event;

import me.sunstorm.showmanager.event.impl.DummyEvent;
import me.sunstorm.showmanager.event.impl.DummyListener;
import me.sunstorm.showmanager.eventsystem.EventBus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

public class EventBusTests {

    @Test
    void testExecute() {
        EventBus bus = new EventBus();
        DummyListener dummy = mock(DummyListener.class);
        bus.register(dummy);
        new DummyEvent().call(bus);
        ArgumentCaptor<DummyEvent> arg = ArgumentCaptor.forClass(DummyEvent.class);
        verify(dummy).onDummyEvent(arg.capture());
    }

    @Test
    void testUnregister() {
        EventBus bus = new EventBus();
        DummyListener dummy = mock(DummyListener.class);
        bus.register(dummy);
        new DummyEvent().call(bus);
        ArgumentCaptor<DummyEvent> arg = ArgumentCaptor.forClass(DummyEvent.class);
        verify(dummy).onDummyEvent(arg.capture());
        bus.unregister(dummy);
        new DummyEvent().call(bus);
        verifyNoMoreInteractions(dummy);
    }
}
