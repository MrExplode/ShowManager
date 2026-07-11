package me.sunstorm.showmanager.eventsystem.events.transport;

import me.sunstorm.showmanager.eventsystem.events.Event;
import me.sunstorm.showmanager.util.Timecode;

public class TransportCommandEvent extends Event {
    public enum Action {
        PLAY, PAUSE, STOP, SET
    }

    private final Action action;
    private final Timecode time;

    public TransportCommandEvent(Action action, Timecode time) {
        this.action = action;
        this.time = time;
    }

    public Action getAction() {
        return action;
    }

    public Timecode getTime() {
        return time;
    }
}
