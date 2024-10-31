package me.sunstorm.showmanager.modules.scheduler;

import com.google.gson.JsonObject;
import me.sunstorm.showmanager.Constants;
import me.sunstorm.showmanager.injection.InjectRecipient;
import me.sunstorm.showmanager.util.Timecode;

public abstract class AbstractScheduledEvent implements ScheduledEvent, InjectRecipient {
    private final Timecode executeTime;
    private final String type;

    public AbstractScheduledEvent(Timecode executeTime, String type) {
        this.executeTime = executeTime;
        this.type = type;
    }

    @Override
    public JsonObject getData() {
        JsonObject data = new JsonObject();
        data.addProperty("type" ,type);
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
}
