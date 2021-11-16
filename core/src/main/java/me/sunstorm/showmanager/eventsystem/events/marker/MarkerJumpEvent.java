package me.sunstorm.showmanager.eventsystem.events.marker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sunstorm.showmanager.audio.marker.Marker;
import me.sunstorm.showmanager.eventsystem.events.Event;

@Getter
@AllArgsConstructor
public class MarkerJumpEvent extends Event {
    private final Marker marker;
}