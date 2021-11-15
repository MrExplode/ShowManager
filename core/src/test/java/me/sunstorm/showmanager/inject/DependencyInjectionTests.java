package me.sunstorm.showmanager.inject;

import me.sunstorm.showmanager.injection.DependencyInjection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class DependencyInjectionTests {
    private DummyManager manager;
    private DummyHandler handler;

    @BeforeEach
    void setup() {
        manager = mock(DummyManager.class);
        handler = mock(DummyHandler.class);
    }

    @AfterEach
    void clean() {
        DependencyInjection.shutdownStatic();
    }

    @Test
    void injectTest() {
        DependencyInjection.registerProvider(DummyManager.class, () -> manager);
        DependentStub stub = new DependentStub();
        assertEquals(manager, stub.getManager());
        assertNull(stub.getHandler());
    }

    @Test
    void updateTest() {
        DependencyInjection.registerProvider(DummyHandler.class, () -> null);
        DependentStub stub = new DependentStub();
        assertNull(stub.getManager());
        assertNull(stub.getHandler());
        DependencyInjection.updateProvider(DummyHandler.class, () -> handler);
        assertEquals(handler, stub.getHandler());
    }
}
