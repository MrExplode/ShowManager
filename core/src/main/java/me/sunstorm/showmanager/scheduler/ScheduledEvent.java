package me.sunstorm.showmanager.scheduler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sunstorm.showmanager.util.Timecode;

@Getter
@AllArgsConstructor
public abstract class ScheduledEvent implements Comparable<ScheduledEvent> {
    private final Timecode executeTime;

    public boolean isComplete() {
        return true;
    }
}
