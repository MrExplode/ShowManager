package me.sunstorm.showmanager.eventsystem.registry;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import me.sunstorm.showmanager.eventsystem.events.Event;
import me.sunstorm.showmanager.eventsystem.events.audio.*;
import me.sunstorm.showmanager.eventsystem.events.marker.MarkerCreateEvent;
import me.sunstorm.showmanager.eventsystem.events.marker.MarkerDeleteEvent;
import me.sunstorm.showmanager.eventsystem.events.marker.MarkerJumpEvent;
import me.sunstorm.showmanager.eventsystem.events.osc.OscDispatchEvent;
import me.sunstorm.showmanager.eventsystem.events.osc.OscReceiveEvent;
import me.sunstorm.showmanager.eventsystem.events.osc.OscRecordStartEvent;
import me.sunstorm.showmanager.eventsystem.events.osc.OscRecordStopEvent;
import me.sunstorm.showmanager.eventsystem.events.remote.DmxRemoteStateEvent;
import me.sunstorm.showmanager.eventsystem.events.scheduler.EventAddEvent;
import me.sunstorm.showmanager.eventsystem.events.scheduler.EventDeleteEvent;
import me.sunstorm.showmanager.eventsystem.events.time.*;

import java.util.Map;

public class EventRegistry {
    @Getter private static final Map<Integer, Class<? extends Event>> registry = ImmutableMap.<Integer, Class<? extends Event>>builder()
            .put(1, AudioLoadEvent.class)
            .put(2, AudioPauseEvent.class)
            .put(3, AudioStartEvent.class)
            .put(4, AudioStopEvent.class)
            .put(5, AudioVolumeChangeEvent.class)
            .put(6, OscDispatchEvent.class)
            .put(7, OscReceiveEvent.class)
            .put(8, OscRecordStartEvent.class)
            .put(9, OscRecordStopEvent.class)
            .put(10, DmxRemoteStateEvent.class)
            .put(11, TimecodeChangeEvent.class)
            .put(12, TimecodePauseEvent.class)
            .put(13, TimecodeSetEvent.class)
            .put(14, TimecodeStartEvent.class)
            .put(15, TimecodeStopEvent.class)
            .put(16, MarkerCreateEvent.class)
            .put(17, MarkerDeleteEvent.class)
            .put(18, MarkerJumpEvent.class)
            .put(19, EventAddEvent.class)
            .put(20, EventDeleteEvent.class).build();
}
