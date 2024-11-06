package me.sunstorm.showmanager.modules.scheduler;

import com.google.gson.JsonObject;
import me.sunstorm.showmanager.util.Timecode;

import java.util.UUID;

public interface ScheduledEvent {

    Timecode getExecuteTime();

    String getType();

    JsonObject getData();

    UUID getId();

    void execute();
}
