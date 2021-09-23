package me.sunstorm.showmanager.audio;

import lombok.Getter;
import me.sunstorm.showmanager.ShowManager;
import me.sunstorm.showmanager.eventsystem.EventCall;
import me.sunstorm.showmanager.eventsystem.Listener;
import me.sunstorm.showmanager.eventsystem.events.audio.AudioStopEvent;
import me.sunstorm.showmanager.eventsystem.events.time.*;
import me.sunstorm.showmanager.settings.SettingsStore;
import me.sunstorm.showmanager.settings.config.AudioPlayerConfig;
import me.sunstorm.showmanager.terminable.Terminable;
import me.sunstorm.showmanager.util.Timecode;
import org.jetbrains.annotations.Nullable;

import javax.sound.sampled.Mixer;
import java.util.ArrayList;
import java.util.List;

@Getter
public class AudioPlayer implements Terminable, Listener {
    private final List<AudioTrack> tracks = new ArrayList<>();
    private final AudioPlayerConfig config;
    private final Mixer mixer;
    private boolean enabled = false;
    private int index = -1;
    @Nullable private AudioTrack current;

    public AudioPlayer(AudioPlayerConfig config) {
        this.config = config;
        register();
        ShowManager.getInstance().getEventBus().register(this);
        mixer = ShowManager.getInstance().getSettingsStore().getMixerByName(config.getAudioOutput());
        tracks.addAll(config.getTracks());
        if (tracks.size() > 0) {
            current = tracks.get(index).loadTrack(mixer);
        }
    }

    @EventCall
    public void onTimeChange(TimecodeChangeEvent e) {
        if (!enabled) return;
        if (current != null && current.getStartTime().equals(e.getTime())) {
            current.play();
        }
    }

    @EventCall
    public void onTimeStart(TimecodeStartEvent e) {
        if (!enabled) return;
        if (current != null && e.getTime().isBetween(current.getStartTime(), current.getEndTime())) {
            current.play();
        }
    }

    @EventCall
    public void onTimeStop(TimecodeStopEvent e) {
        if (!enabled) return;
        if (current != null) {
            current.discard();
        }
        index = 0;
        if (tracks.size() > 0) {
            current = tracks.get(index).loadTrack(mixer);
        }
    }

    @EventCall
    public void onTimePause(TimecodePauseEvent e) {
        if (!enabled) return;
        if (current != null) {
            current.pause();
        }
    }

    @EventCall
    public void onTimeSet(TimecodeSetEvent e) {
        if (!enabled) return;
        Timecode time = e.getTime();
        if (current != null && current.isLoaded() && time.isBetween(current.getStartTime(), current.getEndTime())) {
            current.getClip().setMicrosecondPosition(time.subtract(current.getStartTime()).millis() * 1000);
        } else {
            for (int i = 0; i < tracks.size(); i++) {
                Timecode start = tracks.get(i).getStartTime();
                Timecode end = tracks.get(i).getEndTime();
                if (time.isBetween(start, end)) {
                    index = i;
                    current = tracks.get(index).loadTrack(mixer);
                    current.getClip().setMicrosecondPosition(time.subtract(current.getStartTime()).millis() * 1000);
                    break;
                }
            }
        }
    }

    @EventCall
    public void onAudioStop(AudioStopEvent e) {
        if (++index < tracks.size()) {
            current = tracks.get(index).loadTrack(mixer);
        }
    }

    @Override
    public void shutdown() throws Exception {
        tracks.forEach(AudioTrack::discard);
        mixer.close();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        tracks.forEach(AudioTrack::discard);
    }
}
