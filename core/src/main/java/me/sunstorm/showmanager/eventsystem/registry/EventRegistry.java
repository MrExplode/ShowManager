package me.sunstorm.showmanager.eventsystem.registry;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import me.sunstorm.showmanager.eventsystem.events.Event;
import me.sunstorm.showmanager.eventsystem.events.audio.AudioLoadEvent;
import me.sunstorm.showmanager.eventsystem.events.audio.AudioPauseEvent;
import me.sunstorm.showmanager.eventsystem.events.audio.AudioStartEvent;
import me.sunstorm.showmanager.eventsystem.events.audio.AudioStopEvent;
import me.sunstorm.showmanager.eventsystem.events.osc.OscDispatchEvent;
import me.sunstorm.showmanager.eventsystem.events.osc.OscReceiveEvent;
import me.sunstorm.showmanager.eventsystem.events.remote.DmxRemoteStateEvent;
import me.sunstorm.showmanager.eventsystem.events.time.*;

import java.util.Map;

public class EventRegistry {
    @Getter private static final Map<Integer, Class<? extends Event>> registry = ImmutableMap.<Integer, Class<? extends Event>>builder()
            .put(1, AudioLoadEvent.class)
            .put(2, AudioPauseEvent.class)
            .put(3, AudioStartEvent.class)
            .put(4, AudioStopEvent.class)
            .put(5, OscDispatchEvent.class)
            .put(6, OscReceiveEvent.class)
            .put(7, DmxRemoteStateEvent.class)
            .put(8, TimecodeChangeEvent.class)
            .put(9, TimecodePauseEvent.class)
            .put(10, TimecodeSetEvent.class)
            .put(11, TimecodeStartEvent.class)
            .put(12, TimecodeStopEvent.class).build();
}
