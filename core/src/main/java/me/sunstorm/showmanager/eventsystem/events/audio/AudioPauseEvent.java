package me.sunstorm.showmanager.eventsystem.events.audio;

import me.sunstorm.showmanager.modules.audio.AudioTrack;
import me.sunstorm.showmanager.eventsystem.events.CancellableEvent;

public class AudioPauseEvent extends CancellableEvent {
    private final AudioTrack track;

    public AudioPauseEvent(AudioTrack track) {
        this.track = track;
    }

    public AudioTrack getTrack() {
        return track;
    }
}
