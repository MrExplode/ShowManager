package me.sunstorm.showmanager.cluster.serial;
import me.sunstorm.showmanager.eventsystem.events.Event;

public record EventWrapper(int id, boolean async, Event event) {
}
