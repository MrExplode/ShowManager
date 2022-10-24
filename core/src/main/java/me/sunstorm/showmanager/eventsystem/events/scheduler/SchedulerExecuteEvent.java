package me.sunstorm.showmanager.eventsystem.events.scheduler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sunstorm.showmanager.eventsystem.events.Event;
import me.sunstorm.showmanager.modules.scheduler.ScheduledEvent;

@Getter
@AllArgsConstructor
public class SchedulerExecuteEvent extends Event {
    private final ScheduledEvent event;
}
