package me.sunstorm.showmanager.scheduler;

import com.google.gson.JsonObject;
import me.sunstorm.showmanager.util.Timecode;

public interface ScheduledEvent {

    Timecode getExecuteTime();

    String getType();

    JsonObject getData();

    void execute();
}
