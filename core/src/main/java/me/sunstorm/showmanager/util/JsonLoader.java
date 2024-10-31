package me.sunstorm.showmanager.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import me.sunstorm.showmanager.Constants;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

public interface JsonLoader {
    Logger log = LoggerFactory.getLogger(JsonLoader.class);

    static JsonElement getJsonElement(File file) throws FileNotFoundException {
        return JsonParser.parseReader(new InputStreamReader(new FileInputStream(file)));
    }

    static <T> T loadOrDefault(String name, Class<T> configType) throws IOException {
        return loadOrDefault(new File(Constants.BASE_DIRECTORY, name), configType);
    }

    static <T> T loadOrDefault(@NotNull File configFile, Class<T> configType) throws IOException {
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
        saveConfig(configFile, config);
        return config;
    }

    static <T> T loadConfig(String name, Class<T> configType) throws IOException {
        return loadConfig(new File(Constants.BASE_DIRECTORY, name), configType);
    }

    static <T> T loadConfig(File file, Class<T> configType) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        T object = Constants.GSON.fromJson(reader, configType);
        reader.close();
        return object;
    }

    static void saveConfig(String name, Object config) throws IOException {
        saveConfig(new File(Constants.BASE_DIRECTORY, name), config);
    }

    static void saveConfig(File file, Object config) throws IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
        Constants.GSON.toJson(config, writer);
        writer.flush();
        writer.close();
    }
}
