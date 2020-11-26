package me.mrexplode.showmanager.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.*;
import java.nio.charset.StandardCharsets;

@UtilityClass
public class JsonLoader {
    public Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public <T> T loadOrDefault(File configFile, Class<T> configType) {
        if (configFile.exists())
            return loadConfig(configFile, configType);

        T config;

        try {
            config = configType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            //HadesProxy.getLogger().severe("Failed to do something");
            e.printStackTrace();
            return null;
        }

        try {
            if (!new File("config").exists())
                new File("config").mkdirs();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8));
            gson.toJson(config, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            //HadesProxy.getLogger().severe("Failed to create new json file");
            e.printStackTrace();
        }

        return config;
    }

    public <T> T loadOrDefault(String name, Class<T> configType) {
        File configFile = new File("config", name);
        return loadOrDefault(configFile, configType);
    }

    @SneakyThrows
    public <T> T loadConfig(String name, Class<T> configType) {
        File configFile = new File("config", name);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8));
        T asd = gson.fromJson(reader, configType);
        reader.close();
        return asd;
    }

    @SneakyThrows
    public <T> T loadConfig(File file, Class<T> configType) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        T asd = gson.fromJson(reader, configType);
        reader.close();
        return asd;
    }

    @SneakyThrows
    public void saveConfig(String name, Object config) {
        File configFile = new File("config", name);
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8));
        gson.toJson(config, writer);
        writer.flush();
        writer.close();
    }

    @SneakyThrows
    public void saveConfig(File file, Object config) {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
        gson.toJson(config, writer);
        writer.flush();
        writer.close();
    }
}
