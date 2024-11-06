package me.sunstorm.showmanager.modules.scheduler;

import com.google.gson.JsonObject;
import me.sunstorm.showmanager.Constants;
import me.sunstorm.showmanager.util.Timecode;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class AbstractScheduledEvent implements ScheduledEvent {
    private final Timecode executeTime;
    private final String type;
    private final UUID id;

    public AbstractScheduledEvent(Timecode executeTime, String type, @Nullable UUID id) {
        this.executeTime = executeTime;
        this.type = type;
        this.id = id == null ? UUID.randomUUID() : id;
    }

    @Override
    public JsonObject getData() {
        JsonObject data = new JsonObject();
        data.addProperty("type" ,type);
        data.addProperty("id", id.toString());
        data.add("time", Constants.GSON.toJsonTree(executeTime));
        return data;
    }

    @Override
    public Timecode getExecuteTime() {
        return executeTime;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public UUID getId() {
        return id;
    }
}
