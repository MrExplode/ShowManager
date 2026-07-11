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

    /**
     * True if this cue mutates the authoritative show clock and must run on the master only,
     * so followers don't route a duplicate transport command back to it.
     */
    default boolean masterOnly() {
        return false;
    }
}
