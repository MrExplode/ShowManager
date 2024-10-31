package me.sunstorm.showmanager.eventsystem;

public enum EventPriority {
    HIGHEST(2),
    HIGH(1),
    NORMAL(0),
    LOW(-1),
    LOWEST(-2);

    private final int priority;

    EventPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
