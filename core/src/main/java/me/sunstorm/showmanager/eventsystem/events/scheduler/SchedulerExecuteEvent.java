package me.sunstorm.showmanager.eventsystem.events.scheduler;

import me.sunstorm.showmanager.eventsystem.events.Event;
import me.sunstorm.showmanager.modules.scheduler.ScheduledEvent;

public class SchedulerExecuteEvent extends Event {
    private final ScheduledEvent event;

    public SchedulerExecuteEvent(ScheduledEvent event) {
        this.event = event;
    }

    public ScheduledEvent getEvent() {
        return event;
    }
}
