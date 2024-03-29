package me.sunstorm.showmanager.eventsystem;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventPriority {
    HIGHEST(2),
    HIGH(1),
    NORMAL(0),
    LOW(-1),
    LOWEST(-2);

    private final int priority;
}
