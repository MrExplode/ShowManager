package me.sunstorm.showmanager.eventsystem;

import me.sunstorm.showmanager.eventsystem.events.Event;

@FunctionalInterface
public interface EventExecutor {

    void execute(Event event, Object listener);
}
