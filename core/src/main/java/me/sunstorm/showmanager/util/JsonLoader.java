package me.sunstorm.showmanager.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.showmanager.Constants;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
@UtilityClass
public class JsonLoader {

    public <T> T loadOrDefault(String name, Class<T> configType) {
        return loadOrDefault(new File(Constants.BASE_DIRECTORY, name), configType);
    }

    public <T> T loadOrDefault(File configFile, Class<T> configType) {
        if (configFile.exists())
            return loadConfig(configFile, configType);
        T config;
        try {
            config = configType.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            log.error("Failed to create instance for " + configType.getSimpleName(), e);
            return null;
        }
        try {
            if (!new File("config").exists())
                new File("config").mkdirs();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8));
            Constants.GSON.toJson(config, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            log.error("Failed to write default config for " + configType.getSimpleName(), e);
        }
        return config;
    }

    @SneakyThrows
    public <T> T loadConfig(String name, Class<T> configType) {
        return loadConfig(new File(Constants.BASE_DIRECTORY, name), configType);
    }

    @SneakyThrows
    public <T> T loadConfig(File file, Class<T> configType) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        T object = Constants.GSON.fromJson(reader, configType);
        reader.close();
        return object;
    }

    @SneakyThrows
    public void saveConfig(String name, Object config) {
        saveConfig(new File(Constants.BASE_DIRECTORY, name), config);
    }

    @SneakyThrows
    public void saveConfig(File file, Object config) {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
        Constants.GSON.toJson(config, writer);
        writer.flush();
        writer.close();
    }
}
