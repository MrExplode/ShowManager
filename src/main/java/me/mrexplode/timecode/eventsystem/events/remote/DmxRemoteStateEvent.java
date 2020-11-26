package me.mrexplode.timecode.eventsystem.events.remote;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.mrexplode.timecode.eventsystem.events.Event;
import me.mrexplode.timecode.remote.DmxRemoteState;

@Getter
@AllArgsConstructor
public class DmxRemoteStateEvent extends Event {
    private final DmxRemoteState state;
    private final DmxRemoteState previous;
}
