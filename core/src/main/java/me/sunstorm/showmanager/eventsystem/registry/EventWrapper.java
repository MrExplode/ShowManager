package me.sunstorm.showmanager.eventsystem.registry;
import me.sunstorm.showmanager.eventsystem.events.Event;

public record EventWrapper(int id, boolean async, Event event) {
}
