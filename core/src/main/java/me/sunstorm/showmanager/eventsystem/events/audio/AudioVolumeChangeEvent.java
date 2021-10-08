package me.sunstorm.showmanager.eventsystem.events.audio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sunstorm.showmanager.eventsystem.events.Event;

@Getter
@AllArgsConstructor
public class AudioVolumeChangeEvent extends Event {
    private final int volume;
}
