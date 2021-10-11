package me.sunstorm.showmanager.scheduler;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sunstorm.showmanager.Constants;
import me.sunstorm.showmanager.injection.InjectRecipient;
import me.sunstorm.showmanager.util.Timecode;

@Getter
@AllArgsConstructor
public abstract class AbstractScheduledEvent implements ScheduledEvent, InjectRecipient {
    private final Timecode executeTime;
    private final String type;

    @Override
    public JsonObject getData() {
        JsonObject data = new JsonObject();
        data.addProperty("type" ,type);
        data.add("time", Constants.GSON.toJsonTree(executeTime));
        return data;
    }
}
