package me.sunstorm.showmanager.eventsystem.events.audio;

import me.sunstorm.showmanager.modules.audio.AudioTrack;
import me.sunstorm.showmanager.eventsystem.events.Event;

public class AudioLoadEvent extends Event {
    private final AudioTrack track;

    public AudioLoadEvent(AudioTrack track) {
        this.track = track;
    }

    public AudioTrack getTrack() {
        return track;
    }
}
