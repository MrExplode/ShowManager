package me.sunstorm.showmanager.eventsystem.events.scheduler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sunstorm.showmanager.eventsystem.events.Event;
import me.sunstorm.showmanager.scheduler.ScheduledEvent;

@Getter
@AllArgsConstructor
public class EventAddEvent extends Event {
    private final ScheduledEvent event;
}
