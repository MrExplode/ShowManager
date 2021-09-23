package me.sunstorm.showmanager.eventsystem.events.audio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sunstorm.showmanager.audio.AudioTrack;
import me.sunstorm.showmanager.eventsystem.events.CancellableEvent;

@Getter
@AllArgsConstructor
public class AudioPauseEvent extends CancellableEvent {
    private final AudioTrack track;
}
