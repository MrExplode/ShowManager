package me.sunstorm.showmanager.settings;

import com.google.gson.JsonElement;
import me.sunstorm.showmanager.settings.project.Project;
import org.jetbrains.annotations.NotNull;

public abstract class SettingsHolder {

    public final void load() {
        Project.current().loadSettingsHolder(this);
    }

    @NotNull
    public abstract JsonElement getData();

    public abstract void onLoad(@NotNull JsonElement element);

    public abstract String getName();
}
