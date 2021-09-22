package me.sunstorm.showmanager.audio;

import lombok.Getter;
import me.sunstorm.showmanager.ShowManager;
import me.sunstorm.showmanager.eventsystem.EventCall;
import me.sunstorm.showmanager.eventsystem.Listener;
import me.sunstorm.showmanager.eventsystem.events.time.*;
import me.sunstorm.showmanager.terminable.Terminable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AudioPlayer implements Terminable, Listener {
    private boolean enabled = false;
    private final List<AudioTrack> tracks = new ArrayList<>();

    public AudioPlayer() {
        register();
        ShowManager.getInstance().getEventBus().register(this);
    }

    @EventCall
    public void onTimeChange(TimecodeChangeEvent e) {
        if (!enabled) return;

    }

    @EventCall
    public void onTimeStart(TimecodeStartEvent e) {
        if (!enabled) return;
    }

    @EventCall
    public void onTimeStop(TimecodeStopEvent e) {
        if (!enabled) return;
    }

    @EventCall
    public void onTimePause(TimecodePauseEvent e) {
        if (!enabled) return;
    }

    @EventCall
    public void onTimeSet(TimecodeSetEvent e) {
        if (!enabled) return;
    }

    @Override
    public void shutdown() throws Exception {
        //shutdown
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
