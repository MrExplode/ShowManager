package me.sunstorm.showmanager.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.Constants;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
@UtilityClass
public class JsonLoader {

    public JsonElement getJsonElement(File file) throws FileNotFoundException {
        return JsonParser.parseReader(new InputStreamReader(new FileInputStream(file)));
    }

    public <T> T loadOrDefault(String name, Class<T> configType) throws IOException {
        return loadOrDefault(new File(Constants.BASE_DIRECTORY, name), configType);
    }

    public <T> T loadOrDefault(@NotNull File configFile, Class<T> configType) throws IOException {
        if (configFile.exists())
            return loadConfig(configFile, configType);
        T config;
        try {
            config = configType.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        if (!new File("config").exists())
            new File("config").mkdirs();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8));
        Constants.GSON.toJson(config, writer);
        writer.flush();
        writer.close();
        return config;
    }

    public <T> T loadConfig(String name, Class<T> configType) throws IOException {
        return loadConfig(new File(Constants.BASE_DIRECTORY, name), configType);
    }

    public <T> T loadConfig(File file, Class<T> configType) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        T object = Constants.GSON.fromJson(reader, configType);
        reader.close();
        return object;
    }

    public void saveConfig(String name, Object config) throws IOException {
        saveConfig(new File(Constants.BASE_DIRECTORY, name), config);
    }

    public void saveConfig(File file, Object config) throws IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
        Constants.GSON.toJson(config, writer);
        writer.flush();
        writer.close();
    }
}
