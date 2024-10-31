package me.sunstorm.showmanager.eventsystem.events.audio;

import me.sunstorm.showmanager.eventsystem.events.Event;

public class AudioVolumeChangeEvent extends Event {
    private final int volume;

    public AudioVolumeChangeEvent(int volume) {
        this.volume = volume;
    }

    public int getVolume() {
        return volume;
    }
}
