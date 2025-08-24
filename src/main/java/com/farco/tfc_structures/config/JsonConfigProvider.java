package com.farco.tfc_structures.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

public class JsonConfigProvider {
    private final Path configFolderPath;
    private final Gson gson;

    public JsonConfigProvider(Path configFolderPath) {
        this.configFolderPath = configFolderPath;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
    }

    public Path getConfigPath(String configName) {
        return configFolderPath.resolve(configName);
    }

    public <T> T load(String configName, Codec<T> codec, Supplier<T> defaultConfigSupplier) {
        try {
            Path configPath = getConfigPath(configName);
            if (Files.exists(configPath)) {
                BufferedReader reader = Files.newBufferedReader(configPath);
                JsonElement json = JsonParser.parseReader(reader);
                reader.close();

                return codec.parse(JsonOps.INSTANCE, json).result().orElseThrow();
            } else {
                T config = defaultConfigSupplier.get();
                save(configName, config, codec);
                return config;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void save(String configName, T config, Codec<T> codec) {
        try {
            Files.createDirectories(configFolderPath);

            JsonElement jsonElement = codec.encodeStart(JsonOps.INSTANCE, config).result().orElseThrow();
            String jsonString = gson.toJson(jsonElement);
            Path configPath = getConfigPath(configName);
            Files.writeString(configPath, jsonString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
