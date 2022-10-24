package me.sunstorm.showmanager.eventsystem.events.marker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sunstorm.showmanager.modules.audio.marker.Marker;
import me.sunstorm.showmanager.eventsystem.events.Event;

@Getter
@AllArgsConstructor
public class MarkerCreateEvent extends Event {
    private final Marker marker;
}
