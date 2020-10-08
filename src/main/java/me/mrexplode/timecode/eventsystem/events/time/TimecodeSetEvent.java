package me.mrexplode.timecode.eventsystem.events.time;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.mrexplode.timecode.eventsystem.events.CancellableEvent;
import me.mrexplode.timecode.util.Timecode;

@Getter
@AllArgsConstructor
public class TimecodeSetEvent extends CancellableEvent {
    private final Timecode time;
}
