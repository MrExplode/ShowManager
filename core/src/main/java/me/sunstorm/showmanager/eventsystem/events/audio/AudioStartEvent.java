package me.sunstorm.showmanager.eventsystem.events.audio;

import me.sunstorm.showmanager.modules.audio.AudioTrack;
import me.sunstorm.showmanager.eventsystem.events.CancellableEvent;

public class AudioStartEvent extends CancellableEvent {
    private final AudioTrack track;

    public AudioStartEvent(AudioTrack track) {
        this.track = track;
    }

    public AudioTrack getTrack() {
        return track;
    }
}
