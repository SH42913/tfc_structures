package com.farco.tfc_structures.config;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Supplier;

public class JsonConfigProvider {
    public interface HasFieldsToSort {
        boolean needSort(String fieldName);
    }

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
            jsonElement = sortIfNeed(config, jsonElement);
            String jsonString = gson.toJson(jsonElement);
            Path configPath = getConfigPath(configName);
            Files.writeString(configPath, jsonString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> JsonElement sortIfNeed(T config, JsonElement jsonElement) {
        if (config instanceof HasFieldsToSort hasFieldsToSort && jsonElement.isJsonObject()) {
            var sortedCopy = new JsonObject();

            for (var entry : jsonElement.getAsJsonObject().entrySet()) {
                var fieldName = entry.getKey();
                var value = entry.getValue();
                if (hasFieldsToSort.needSort(fieldName) && value.isJsonObject()) {
                    var sortedValue = new JsonObject();

                    value.getAsJsonObject()
                            .entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .forEach(e -> sortedValue.add(e.getKey(), e.getValue()));

                    sortedCopy.add(fieldName, sortedValue);
                } else {
                    sortedCopy.add(fieldName, value);
                }
            }

            return sortedCopy;
        } else {
            return jsonElement;
        }
    }
}
