package me.sunstorm.showmanager.cluster.serial;

import com.google.common.collect.ImmutableMap;
import me.sunstorm.showmanager.eventsystem.events.Event;
import me.sunstorm.showmanager.eventsystem.events.time.*;
import me.sunstorm.showmanager.eventsystem.events.transport.TransportCommandEvent;

import java.util.Map;

/**
 * The cluster-distributable event whitelist: only authoritative state that must cross nodes —
 * the master's timecode/transport beacons and follower-to-master transport commands. Local output
 * and input side-effect events stay on their origin node.
 */
public interface EventRegistry {
    Map<Integer, Class<? extends Event>> REGISTRY = ImmutableMap.<Integer, Class<? extends Event>>builder()
            .put(11, TimecodeChangeEvent.class)
            .put(1, TimecodePauseEvent.class)
            .put(2, TimecodeSetEvent.class)
            .put(3, TimecodeStartEvent.class)
            .put(4, TimecodeStopEvent.class)
            .put(5, TransportCommandEvent.class).build();

    Map<Class<? extends Event>, Integer> IDS = REGISTRY.entrySet().stream()
            .collect(ImmutableMap.toImmutableMap(Map.Entry::getValue, Map.Entry::getKey));

    static Integer idOf(Event event) {
        return IDS.get(event.getClass());
    }
}
