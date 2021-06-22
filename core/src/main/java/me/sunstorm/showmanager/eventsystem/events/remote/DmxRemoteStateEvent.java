package me.sunstorm.showmanager.eventsystem.events.remote;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sunstorm.showmanager.eventsystem.events.Event;
import me.sunstorm.showmanager.remote.DmxRemoteState;

@Getter
@AllArgsConstructor
public class DmxRemoteStateEvent extends Event {
    private final DmxRemoteState state;
    private final DmxRemoteState previous;
}
