package me.mrexplode.showmanager.eventsystem.events;

import me.mrexplode.showmanager.eventsystem.EventBus;

public class Event {
    public void call(EventBus e) {
        e.call(this);
    }

    public void call(boolean async, EventBus e) {
        e.call(async, this);
    }
}
