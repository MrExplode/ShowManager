package me.sunstorm.showmanager.settings;

import com.google.gson.JsonObject;
import lombok.Getter;
import me.sunstorm.showmanager.settings.project.Project;

@Getter
public abstract class SettingsHolder {
    private final String name;

    public SettingsHolder(String name) {
        this.name = name;
        Project.current().loadSettingsHolder(this);
    }

    public abstract JsonObject getData();

    public abstract void onLoad(JsonObject object);
}
