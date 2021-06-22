package me.sunstorm.showmanager.eventsystem.events.time;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sunstorm.showmanager.eventsystem.events.Event;
import me.sunstorm.showmanager.util.Timecode;

@Getter
@AllArgsConstructor
public class TimecodeChangeEvent extends Event {
    private final Timecode time;
}
