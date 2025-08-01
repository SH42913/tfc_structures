package com.farco.tfc_structures.data;

import com.farco.tfc_structures.TFCStructuresMod;
import com.farco.tfc_structures.config.StructureConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class DatapackGenerator {
    public static final String DATA_PACK_FOLDER_NAME = TFCStructuresMod.MODID + "_datapack";
    public static final String PACK_MCMETA_NAME = "pack.mcmeta";

    public record TagLink(String id, boolean required) {
    }

    public record TagValues(List<TagLink> values) {
        public TagValues(List<String> values, boolean required) {
            this(values.stream().map(id -> new TagLink(id, required)).toList());
        }
    }

    private record Pack(int pack_format, String description) {
    }

    private record PackMeta(Pack pack) {
    }

    private final Path configFolderPath;
    private final Path datapackFolderPath;
    private final Gson GSON;

    public DatapackGenerator(Path configFolderPath) {
        this.configFolderPath = configFolderPath;
        this.datapackFolderPath = this.configFolderPath.resolve(DATA_PACK_FOLDER_NAME);

        GSON = new GsonBuilder().setPrettyPrinting().create();
    }

    public void refreshDatapack(StructureConfig structureConfig) {
        try {
            initDatapackIfNeeded();
            generateActiveStructures(structureConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public RepositorySource getDatapackSource() {
        PackSource packSource = PackSource.SERVER;
        return new FolderRepositorySource(configFolderPath, PackType.SERVER_DATA, packSource);
    }

    private void initDatapackIfNeeded() throws IOException {
        Path packMetaPath = datapackFolderPath.resolve(PACK_MCMETA_NAME);
        if (Files.exists(packMetaPath)) {
            return;
        }

        Files.createDirectories(datapackFolderPath);

        PackMeta packMeta = new PackMeta(new Pack(15, TFCStructuresMod.MODID + " generated data-pack"));
        SaveJson(packMetaPath, packMeta);

        Path biomeTagsFolder = buildBiomeTagsFolderPath(datapackFolderPath, "minecraft");
        Files.createDirectories(biomeTagsFolder);
        for (BiomeTag tag : BiomeTag.getAllVanillaBiomeTags()) {
            var location = ResourceLocation.parse(tag.id());
            generateTag(biomeTagsFolder, location.getPath(), tag.tagValues());
        }
    }

    private void generateActiveStructures(StructureConfig structureConfig) throws IOException {
        var modIdToStructuresMap = new HashMap<String, List<StructureData>>();
        for (StructureData structure : structureConfig.activeStructures) {
            var modId = structure.getResourceLocation().getNamespace();
            var locations = modIdToStructuresMap.getOrDefault(modId, null);
            if (locations == null) {
                locations = new ArrayList<>();
            }

            locations.add(structure);
            modIdToStructuresMap.put(modId, locations);
        }

        for (var entry : modIdToStructuresMap.entrySet()) {
            var modId = entry.getKey();
            var structures = entry.getValue();

            Path hasStructureFolder = buildBiomeTagsFolderPath(datapackFolderPath, modId).resolve("has_structure");
            FileUtils.deleteQuietly(new File(hasStructureFolder.toUri()));
            Files.createDirectories(hasStructureFolder);

            for (StructureData structure : structures) {
                String shortName = structure.getResourceLocation().getPath();
                generateTag(hasStructureFolder, shortName, structure.getAllowedBiomesAsTagValues());
            }
        }
    }

    private static @NotNull Path buildBiomeTagsFolderPath(Path datapackFolder, String modId) {
        return datapackFolder
                .resolve("data")
                .resolve(modId)
                .resolve("tags")
                .resolve("worldgen")
                .resolve("biome");
    }

    private void generateTag(Path folder, String name, TagValues tagValues) throws IOException {
        Path filePath = folder.resolve(name + ".json");
        SaveJson(filePath, tagValues);
    }

    private void SaveJson(Path filePath, Object target) throws IOException {
        Files.writeString(filePath, GSON.toJson(target));
    }
}
