package me.sunstorm.showmanager.inject;

import me.sunstorm.showmanager.inject.impl.DependentStub;
import me.sunstorm.showmanager.inject.impl.DummyHandler;
import me.sunstorm.showmanager.inject.impl.DummyManager;
import me.sunstorm.showmanager.injection.DependencyInjection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        assertThat(stub.getManager()).isEqualTo(manager);
        assertThat(stub.getHandler()).isNull();
    }

    @Test
    void updateTest() {
        DependencyInjection.registerProvider(DummyHandler.class, () -> null);
        DependentStub stub = new DependentStub();
        assertThat(stub.getManager()).isNull();
        assertThat(stub.getHandler()).isNull();
        DependencyInjection.updateProvider(DummyHandler.class, () -> handler);
        assertThat(stub.getHandler()).isEqualTo(handler);
    }
}
