package me.sunstorm.showmanager.settings.project;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.Constants;
import me.sunstorm.showmanager.settings.SettingsHolder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public class Project {
    private final List<SettingsHolder> settings = new ArrayList<>();
    private final File file;
    private String name = "unknown";
    private JsonObject data;

    @SneakyThrows(FileNotFoundException.class)
    public void loadJson() throws JsonParseException {
        if (!file.isFile()) {
            log.warn("Tried to load project {} but it's file doesn't exists", name);
            return;
        }
        data = JsonParser.parseReader(new FileReader(file)).getAsJsonObject();
        if (data.has("name"))
            name = data.get("name").getAsString();
    }

    public void save() {
        JsonObject data = new JsonObject();
        if (!data.has("name")) data.addProperty("name", name);
        settings.forEach(s -> data.add(s.getName(), s.getData()));
        try {
            Constants.GSON.toJson(data, new FileWriter(file));
        } catch (IOException e) {
            log.error("Failed to save project " + name, e);
        }
    }
}
