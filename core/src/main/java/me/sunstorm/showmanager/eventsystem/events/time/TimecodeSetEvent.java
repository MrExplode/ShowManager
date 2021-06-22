package me.sunstorm.showmanager.eventsystem.events.time;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sunstorm.showmanager.eventsystem.events.CancellableEvent;
import me.sunstorm.showmanager.util.Timecode;

@Getter
@AllArgsConstructor
public class TimecodeSetEvent extends CancellableEvent {
    private final Timecode time;
}
