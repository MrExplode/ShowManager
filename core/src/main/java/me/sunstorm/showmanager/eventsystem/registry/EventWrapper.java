package me.sunstorm.showmanager.eventsystem.registry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sunstorm.showmanager.eventsystem.events.Event;

@Getter
@AllArgsConstructor
public class EventWrapper {
    private final int id;
    private final boolean async;
    private final Event event;
}
