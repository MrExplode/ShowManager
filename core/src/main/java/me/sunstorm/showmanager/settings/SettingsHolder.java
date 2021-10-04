package me.sunstorm.showmanager.settings;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class SettingsHolder {
    private final String name;

    public abstract JsonObject getData();

    public abstract void onLoad(JsonObject object);
}
