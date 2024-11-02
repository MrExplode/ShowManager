package me.sunstorm.showmanager.settings.project;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import me.sunstorm.showmanager.Constants;
import me.sunstorm.showmanager.settings.SettingsHolder;
import me.sunstorm.showmanager.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Project {
    private static final Logger log = LoggerFactory.getLogger(Project.class);

    private final List<SettingsHolder> settings = new ArrayList<>();
    private final File file;
    private String name = "unknown";
    private JsonObject data;

    public Project(File file) {
        this.file = file;
    }

    public void loadJson() throws JsonParseException {
        if (!file.isFile()) {
            log.warn("Tried to load project {} but it's file doesn't exists. Loading default config", name);
            save();
        }
        try {
            data = JsonParser.parseReader(new FileReader(file)).getAsJsonObject();
            if (data.has("name"))
                name = data.get("name").getAsString();
        } catch (FileNotFoundException e) {
            Exceptions.sneaky(e);
        }
    }

    public void save() {
        log.info("Saving project {}", name);
        JsonObject data = new JsonObject();
        if (!data.has("name")) data.addProperty("name", name);
        settings.forEach(s -> data.add(s.getName(), s.getData()));
        try {
            var writer = new FileWriter(file);
            Constants.GSON.toJson(data, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            log.error("Failed to save project {}", name, e);
        }
    }

    public void loadSettingsHolder(SettingsHolder holder) {
        String name = holder.getName();
        if (data.has(name)) {
            try {
                holder.onLoad(data.get(name));
                settings.add(holder);
                return;
            } catch (Exception e) {
                log.error("Failed to load settings for {}, falling back to default", holder.getClass().getSimpleName());
            }
        }
        var settingsData = holder.getData();
        data.add(name, settingsData);
        holder.onLoad(settingsData);
        settings.add(holder);
    }

    public static Project current() {
        return ProjectManager.currentProject;
    }

    // generated

    public List<SettingsHolder> getSettings() {
        return settings;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }
}
