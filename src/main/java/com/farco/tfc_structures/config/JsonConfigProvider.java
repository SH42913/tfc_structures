package com.farco.tfc_structures.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

public class JsonConfigProvider {
    private final Path configFolderPath;
    private final Gson GSON;

    public JsonConfigProvider(Path configFolderPath) {
        this.configFolderPath = configFolderPath;

        GSON = new GsonBuilder().setPrettyPrinting().create();
    }

    public Path getConfigPath(String configName) {
        return configFolderPath.resolve(configName);
    }

    public <T> T load(String configName, Class<T> configType, Supplier<T> defaultConfigSupplier) {
        try {
            Path configPath = getConfigPath(configName);
            if (Files.exists(configPath)) {
                String json = Files.readString(configPath);
                return GSON.fromJson(json, configType);
            } else {
                T config = defaultConfigSupplier.get();
                save(configName, config);
                return config;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void save(String configName, T config) {
        try {
            Files.createDirectories(configFolderPath);

            Path configPath = getConfigPath(configName);
            String json = GSON.toJson(config);
            Files.writeString(configPath, json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
