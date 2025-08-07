package com.farco.tfc_structures.data;

import com.farco.tfc_structures.TFCStructuresMod;
import com.farco.tfc_structures.config.CommonConfig;
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
import java.util.*;

public final class DatapackGenerator {
    public static final String DATA_PACK_FOLDER_NAME = TFCStructuresMod.MODID + "_main";
    public static final String PACK_MCMETA_NAME = "pack.mcmeta";

    public record TagValues(Collection<String> values) {
    }

    private record Pack(int pack_format, String description) {
    }

    private record PackMeta(Pack pack) {
    }

    private final Path datapacksFolderPath;
    private final Path datapackFolderPath;
    private final Gson GSON;

    public DatapackGenerator(Path datapacksFolderPath) {
        this.datapacksFolderPath = datapacksFolderPath;
        this.datapackFolderPath = this.datapacksFolderPath.resolve(DATA_PACK_FOLDER_NAME);

        GSON = new GsonBuilder().setPrettyPrinting().create();
    }

    public void refreshDatapack(StructureConfig structureConfig) {
        try {
            initDatapackIfNeeded();
            generateBlocksTag();
            generateBiomeTags(structureConfig);
            generateActiveStructures(structureConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public RepositorySource getDatapackSource() {
        PackSource packSource = PackSource.SERVER;
        return new FolderRepositorySource(datapacksFolderPath, PackType.SERVER_DATA, packSource);
    }

    private void initDatapackIfNeeded() throws IOException {
        Path packMetaPath = datapackFolderPath.resolve(PACK_MCMETA_NAME);
        if (Files.exists(packMetaPath)) {
            return;
        }

        Files.createDirectories(datapackFolderPath);

        PackMeta packMeta = new PackMeta(new Pack(15, TFCStructuresMod.MODID + " generated data-pack"));
        SaveJson(packMetaPath, packMeta);
    }

    private void generateBiomeTags(StructureConfig structureConfig) throws IOException {
        for (BiomeTag tag : structureConfig.biomeTags) {
            var location = ResourceLocation.parse(tag.id());

            Path biomeTagsFolder = buildBiomeTagsFolderPath(datapackFolderPath, location.getNamespace());
            Files.createDirectories(biomeTagsFolder);

            generateTag(biomeTagsFolder, location.getPath(), tag.tagValues());
        }
    }

    private void generateBlocksTag() throws IOException {
        Path blockTagsFolder = datapackFolderPath
                .resolve("data")
                .resolve(TFCStructuresMod.MODID)
                .resolve("tags")
                .resolve("blocks");

        Files.createDirectories(blockTagsFolder);

        TagValues mossyBlocks = new TagValues(new ArrayList<>(CommonConfig.MOSSY_BLOCKS.get()));
        generateTag(blockTagsFolder, TFCStructuresMod.MOSSY_TAG_NAME, mossyBlocks);

        TagValues strippedLogs = new TagValues(new ArrayList<>(CommonConfig.STRIPPED_LOGS.get()));
        generateTag(blockTagsFolder, TFCStructuresMod.STRIPPED_LOG_TAG_NAME, strippedLogs);

        TagValues strippedWoods = new TagValues(new ArrayList<>(CommonConfig.STRIPPED_WOOD.get()));
        generateTag(blockTagsFolder, TFCStructuresMod.STRIPPED_WOOD_TAG_NAME, strippedWoods);
    }

    private void generateActiveStructures(StructureConfig structureConfig) throws IOException {
        var modIdToStructuresMap = new HashMap<String, List<StructureData>>();
        for (StructureData structure : structureConfig.activeStructures) {
            addStructureToMap(structure, modIdToStructuresMap);
        }

        for (String structureId : structureConfig.disabledStructures) {
            addStructureToMap(new StructureData(structureId, Collections.emptyList()), modIdToStructuresMap);
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

    private static void addStructureToMap(StructureData structure, HashMap<String, List<StructureData>> map) {
        var modId = structure.getResourceLocation().getNamespace();
        var locations = map.getOrDefault(modId, null);
        if (locations == null) {
            locations = new ArrayList<>();
        }

        locations.add(structure);
        map.put(modId, locations);
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
