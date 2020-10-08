package me.mrexplode.timecode.eventsystem.events.time;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.mrexplode.timecode.eventsystem.events.Event;
import me.mrexplode.timecode.util.Timecode;

@Getter
@AllArgsConstructor
public class TimecodeChangeEvent extends Event {
    private final Timecode time;
}
