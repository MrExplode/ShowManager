package me.sunstorm.showmanager.eventsystem.events.remote;

import me.sunstorm.showmanager.eventsystem.events.Event;
import me.sunstorm.showmanager.modules.remote.DmxRemoteState;

public class DmxRemoteStateEvent extends Event {
    private final DmxRemoteState state;
    private final DmxRemoteState previous;

    public DmxRemoteStateEvent(DmxRemoteState state, DmxRemoteState previous) {
        this.state = state;
        this.previous = previous;
    }

    public DmxRemoteState getState() {
        return state;
    }

    public DmxRemoteState getPrevious() {
        return previous;
    }
}
