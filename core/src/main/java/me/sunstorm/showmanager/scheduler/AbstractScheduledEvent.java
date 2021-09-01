package me.sunstorm.showmanager.scheduler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sunstorm.showmanager.util.Timecode;

@Getter
@AllArgsConstructor
public abstract class AbstractScheduledEvent implements ScheduledEvent {
    private final Timecode executeTime;
}
