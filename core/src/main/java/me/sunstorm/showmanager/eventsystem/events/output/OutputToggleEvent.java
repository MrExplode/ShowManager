package me.sunstorm.showmanager.eventsystem.events.output;

import me.sunstorm.showmanager.eventsystem.events.Event;

/**
 * Arms or disarms an output show-wide. Distributed, so a toggle from any node's UI reaches every
 * node: ownership decides which node physically emits an output, this decides whether it is armed
 * at all. {@code output} is the REST name of the output ({@code artnet}, {@code ltc}, {@code audio},
 * {@code osc}, {@code scheduler}).
 */
public class OutputToggleEvent extends Event {
    private final String output;
    private final boolean enabled;

    public OutputToggleEvent(String output, boolean enabled) {
        this.output = output;
        this.enabled = enabled;
    }

    public String getOutput() {
        return output;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
