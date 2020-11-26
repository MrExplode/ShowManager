package me.mrexplode.showmanager.eventsystem.events.time;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.mrexplode.showmanager.eventsystem.events.CancellableEvent;
import me.mrexplode.showmanager.util.Timecode;

@Getter
@AllArgsConstructor
public class TimecodeSetEvent extends CancellableEvent {
    private final Timecode time;
}
