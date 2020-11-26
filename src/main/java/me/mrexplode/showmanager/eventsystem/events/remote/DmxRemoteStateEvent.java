package me.mrexplode.showmanager.eventsystem.events.remote;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.mrexplode.showmanager.eventsystem.events.Event;
import me.mrexplode.showmanager.remote.DmxRemoteState;

@Getter
@AllArgsConstructor
public class DmxRemoteStateEvent extends Event {
    private final DmxRemoteState state;
    private final DmxRemoteState previous;
}
