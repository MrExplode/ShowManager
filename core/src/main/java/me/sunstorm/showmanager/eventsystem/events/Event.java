package me.sunstorm.showmanager.eventsystem.events;

import me.sunstorm.showmanager.eventsystem.EventBus;

public class Event {
    public void call(EventBus e) {
        e.call(this);
    }

    public void call(boolean async, EventBus e) {
        e.call(async, this);
    }
}
