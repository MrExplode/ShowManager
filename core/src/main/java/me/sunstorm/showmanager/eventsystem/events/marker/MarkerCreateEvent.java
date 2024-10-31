package me.sunstorm.showmanager.eventsystem.events.marker;

import me.sunstorm.showmanager.modules.audio.marker.Marker;
import me.sunstorm.showmanager.eventsystem.events.Event;

public class MarkerCreateEvent extends Event {
    private final Marker marker;

    public MarkerCreateEvent(Marker marker) {
        this.marker = marker;
    }

    public Marker getMarker() {
        return marker;
    }
}
