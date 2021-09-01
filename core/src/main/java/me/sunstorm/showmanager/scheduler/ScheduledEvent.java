package me.sunstorm.showmanager.scheduler;

import me.sunstorm.showmanager.util.Timecode;

public interface ScheduledEvent {

    Timecode getExecuteTime();

    void execute();
}
