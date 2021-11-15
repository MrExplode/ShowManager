package me.sunstorm.showmanager.audio;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.Constants;
import me.sunstorm.showmanager.eventsystem.EventBus;
import me.sunstorm.showmanager.eventsystem.EventCall;
import me.sunstorm.showmanager.eventsystem.Listener;
import me.sunstorm.showmanager.eventsystem.events.audio.AudioStopEvent;
import me.sunstorm.showmanager.eventsystem.events.time.*;
import me.sunstorm.showmanager.injection.Inject;
import me.sunstorm.showmanager.injection.InjectRecipient;
import me.sunstorm.showmanager.settings.SettingsHolder;
import me.sunstorm.showmanager.settings.SettingsStore;
import me.sunstorm.showmanager.terminable.Terminable;
import me.sunstorm.showmanager.util.Timecode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sound.sampled.Mixer;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
public class AudioPlayer extends SettingsHolder implements Terminable, Listener, InjectRecipient {
    private final List<AudioTrack> tracks = new ArrayList<>();
    @Inject private EventBus eventBus;
    @Inject private SettingsStore store;
    private Mixer mixer;
    private boolean enabled = false;
    private int index = 0;
    @Nullable private AudioTrack current;

    public AudioPlayer() {
        super("audio-player");
        inject();
        load();
        register();
        eventBus.register(this);
        if (tracks.size() > 0) {
            current = tracks.get(index).loadTrack(mixer);
        }
    }

    @EventCall
    public void onTimeChange(TimecodeChangeEvent e) {
        if (!enabled) return;
        if (current != null && current.getStartTime().equals(e.getTime())) {
            log.info("play1");
            current.play();
        }
    }

    @EventCall
    public void onTimeStart(TimecodeStartEvent e) {
        if (!enabled) return;
        if (current != null && e.getTime().isBetween(current.getStartTime(), current.getEndTime())) {
            log.info("play2");
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
    public void shutdown() {
        tracks.forEach(AudioTrack::discard);
        mixer.close();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        tracks.forEach(AudioTrack::discard);
    }

    public void setVolume(int volume) {
        if (volume < 0 || volume > 100) throw new IllegalArgumentException("Invalid volume");
        if (current != null)
            current.setVolume(volume);
    }

    @NotNull
    @Override
    public JsonObject getData() {
        JsonObject data = new JsonObject();
        data.addProperty("enabled", enabled);
        data.addProperty("mixer", mixer != null ? mixer.getMixerInfo().getName() : store.getMixerByName("").getMixerInfo().getName());
        JsonArray trackArray = new JsonArray();
        tracks.forEach(t -> trackArray.add(Constants.GSON.toJsonTree(t)));
        data.add("tracks", trackArray);
        return data;
    }

    @Override
    public void onLoad(@NotNull JsonObject object) {
        enabled = object.get("enabled").getAsBoolean();
        mixer = store.getMixerByName(object.get("mixer").getAsString());
        object.get("tracks").getAsJsonArray().forEach(e -> tracks.add(Constants.GSON.fromJson(e, AudioTrack.class)));
    }
}
