package me.sunstorm.showmanager.cluster.serial;

import com.google.common.collect.ImmutableMap;
import me.sunstorm.showmanager.eventsystem.events.Event;
import me.sunstorm.showmanager.eventsystem.events.output.OutputToggleEvent;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodePauseEvent;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodeSetEvent;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodeStartEvent;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodeStopEvent;
import me.sunstorm.showmanager.eventsystem.events.time.TimecodeSyncEvent;
import me.sunstorm.showmanager.eventsystem.events.transport.TransportCommandEvent;

import java.util.Map;

/**
 * The cluster-distributable event whitelist: only authoritative state that must cross nodes — the
 * master's timecode/transport beacons, follower-to-master transport commands, and show-wide output
 * arming. Local output and input side-effect events stay on their origin node.
 * <p>
 * Ids are wire format: never renumber an existing entry, only append.
 */
public interface EventRegistry {
    Map<Integer, Class<? extends Event>> REGISTRY = ImmutableMap.<Integer, Class<? extends Event>>builder()
            .put(1, TimecodePauseEvent.class)
            .put(2, TimecodeSetEvent.class)
            .put(3, TimecodeStartEvent.class)
            .put(4, TimecodeStopEvent.class)
            .put(5, TransportCommandEvent.class)
            .put(6, TimecodeSyncEvent.class)
            .put(7, OutputToggleEvent.class).build();

    Map<Class<? extends Event>, Integer> IDS = REGISTRY.entrySet().stream()
            .collect(ImmutableMap.toImmutableMap(Map.Entry::getValue, Map.Entry::getKey));

    static Integer idOf(Event event) {
        return IDS.get(event.getClass());
    }
}
