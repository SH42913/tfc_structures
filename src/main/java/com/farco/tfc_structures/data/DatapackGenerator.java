package com.farco.tfc_structures.data;

import com.farco.tfc_structures.TFCStructuresMod;
import com.farco.tfc_structures.config.CommonConfig;
import com.farco.tfc_structures.config.WorldgenConfig;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.metadata.pack.PackMetadataSectionSerializer;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagFile;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DatapackGenerator {
    private static final PackMetadataSection VERSION_METADATA_SECTION = new PackMetadataSection(Component.literal(TFCStructuresMod.MODID + " generated data-pack"), SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA));
    private static final String DATA_PACK_FOLDER_NAME = TFCStructuresMod.MODID + "_main";
    private static final String PACK_MCMETA_NAME = "pack.mcmeta";
    private static final String DATA_FOLDER_NAME = "data";

    private final Path datapacksFolderPath;
    private final Path datapackFolderPath;

    public DatapackGenerator(Path datapacksFolderPath) {
        this.datapacksFolderPath = datapacksFolderPath;
        this.datapackFolderPath = this.datapacksFolderPath.resolve(DATA_PACK_FOLDER_NAME);
    }

    public void refreshDatapack(WorldgenConfig worldgenConfig) {
        try {
            initDatapackIfNeeded();
            recreateDataFolder();
            generateBlocksTag();
            generateBiomeTags(worldgenConfig);
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

        JsonObject pack = new JsonObject();
        JsonObject metadata = new PackMetadataSectionSerializer().toJson(VERSION_METADATA_SECTION);
        pack.add("pack", metadata);
        SaveJson(packMetaPath, pack);
    }

    private void recreateDataFolder() throws IOException {
        Path dataFolder = datapackFolderPath.resolve(DATA_FOLDER_NAME);
        FileUtils.deleteQuietly(new File(dataFolder.toUri()));
        Files.createDirectories(dataFolder);
    }

    private void generateBiomeTags(WorldgenConfig worldgenConfig) throws IOException {
        var withDisabled = new ArrayList<>(worldgenConfig.biomeTags);
        var disabledLocation = ResourceLocation.fromNamespaceAndPath(TFCStructuresMod.MODID, "disabled");
        withDisabled.add(new BiomeTag(disabledLocation, Collections.emptyList(), worldgenConfig.disabledStructures));

        for (BiomeTag tag : withDisabled) {
            Path biomeTagsFolder = buildBiomeTagsFolderPath(datapackFolderPath, tag.id().getNamespace());
            Files.createDirectories(biomeTagsFolder);

            TagFile tagFile = buildTagFile(tag.biomes());
            generateTag(biomeTagsFolder, tag.id().getPath(), tagFile);
        }
    }

    private void generateBlocksTag() throws IOException {
        Path blockTagsFolder = datapackFolderPath
                .resolve(DATA_FOLDER_NAME)
                .resolve(TFCStructuresMod.MODID)
                .resolve("tags")
                .resolve("blocks");

        Files.createDirectories(blockTagsFolder);

        generateTag(blockTagsFolder, TFCStructuresMod.MOSSY_TAG_NAME, buildTagFileFromIds(CommonConfig.MOSSY_BLOCKS.get()));
        generateTag(blockTagsFolder, TFCStructuresMod.STRIPPED_LOG_TAG_NAME, buildTagFileFromIds(CommonConfig.STRIPPED_LOGS.get()));
        generateTag(blockTagsFolder, TFCStructuresMod.STRIPPED_WOOD_TAG_NAME, buildTagFileFromIds(CommonConfig.STRIPPED_WOOD.get()));
        generateTag(blockTagsFolder, TFCStructuresMod.CRACKED_BRICKS_TAG_NAME, buildTagFileFromIds(CommonConfig.CRACKED_BRICKS.get()));
    }

    private TagFile buildTagFileFromIds(List<? extends String> elementIds) {
        var elements = elementIds.stream()
                .map(ResourceLocation::parse)
                .map(location -> new ExtraCodecs.TagOrElementLocation(location, false))
                .toList();
        return buildTagFile(elements);
    }

    private TagFile buildTagFile(List<ExtraCodecs.TagOrElementLocation> elements) {
        var entries = new ArrayList<TagEntry>(elements.size());
        for (var element : elements) {
            entries.add(element.tag()
                    ? TagEntry.tag(element.id())
                    : TagEntry.element(element.id()));
        }
        return new TagFile(entries, true);
    }

    private static @NotNull Path buildBiomeTagsFolderPath(Path datapackFolder, String modId) {
        return datapackFolder
                .resolve("data")
                .resolve(modId)
                .resolve("tags")
                .resolve("worldgen")
                .resolve("biome");
    }

    private void generateTag(Path folder, String name, TagFile tagFile) throws IOException {
        Path filePath = folder.resolve(name + ".json");
        JsonElement jsonElement = TagFile.CODEC.encodeStart(JsonOps.INSTANCE, tagFile).result().orElseThrow();
        SaveJson(filePath, jsonElement);
    }

    private void SaveJson(Path filePath, JsonElement jsonElement) throws IOException {
        String jsonString = GsonHelper.toStableString(jsonElement);
        Files.writeString(filePath, jsonString);
    }
}
