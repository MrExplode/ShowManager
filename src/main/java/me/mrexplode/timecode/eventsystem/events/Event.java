package me.mrexplode.timecode.eventsystem.events;

import me.mrexplode.timecode.eventsystem.EventBus;

public class Event {
    public void call(EventBus e) {
        e.call(this);
    }
}
