package me.sunstorm.showmanager.eventsystem.events.cluster;

import me.sunstorm.showmanager.eventsystem.events.Event;

/**
 * Fired locally when the shape of the cluster changes. Deliberately absent from the
 * {@code EventRegistry} whitelist: it is derived from cluster traffic, so redistributing it would
 * echo between nodes forever.
 */
public class ClusterStateChangeEvent extends Event {
}
