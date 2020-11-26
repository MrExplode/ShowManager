package me.mrexplode.showmanager.eventsystem.events.time;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.mrexplode.showmanager.eventsystem.events.Event;
import me.mrexplode.showmanager.util.Timecode;

@Getter
@AllArgsConstructor
public class TimecodeChangeEvent extends Event {
    private final Timecode time;
}
