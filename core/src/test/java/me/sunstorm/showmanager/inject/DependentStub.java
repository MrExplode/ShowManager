package me.sunstorm.showmanager.inject;

import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;

public class DependentStub implements InjectRecipient {
    @Inject
    private DummyManager manager;
    @Inject
    private DummyHandler handler;

    public DependentStub() {
        inject();
    }

    public DummyManager getManager() {
        return manager;
    }

    public DummyHandler getHandler() {
        return handler;
    }
}
