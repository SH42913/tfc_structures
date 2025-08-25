package com.farco.tfc_structures.config;

import com.farco.tfc_structures.TFCStructuresMod;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ReplacementPreset {
    public static final Codec<ReplacementPreset> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Direct.CODEC.listOf().fieldOf("directReplacements").forGetter(cfg -> cfg.directReplacements),
            Random.CODEC.listOf().fieldOf("randomReplacements").forGetter(cfg -> cfg.randomReplacements),
            TFCWorld.CODEC.listOf().fieldOf("tfcWorldReplacements").forGetter(cfg -> cfg.tfcWorldReplacements)
    ).apply(instance, ReplacementPreset::new));

    public static final String TFC_STONE_TYPE = "STONE";
    public static final String TFC_BRICK_TYPE = "BRICK";
    public static final String TFC_WOOD_TYPE = "WOOD";
    public static final String TFC_SOIL_TYPE = "SOIL";
    public static final String TFC_SAND_TYPE = "SAND";
    public static final String TFC_ORE_TYPE = "ORE";
    public static final String TFC_SKIP_TYPE = "SKIP";

    public record Direct(ResourceLocation original, ResourceLocation replacement) {
        public static final Codec<Direct> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("original").forGetter(Direct::original),
                ResourceLocation.CODEC.fieldOf("replacement").forGetter(Direct::replacement)
        ).apply(instance, Direct::new));

        public Direct(String originalString, String replacementString) {
            this(ResourceLocation.parse(originalString), ResourceLocation.parse(replacementString));
        }
    }

    public record Random(ResourceLocation original, boolean perBlock, List<ResourceLocation> replacements) {
        public static final Codec<Random> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("original").forGetter(Random::original),
                Codec.BOOL.optionalFieldOf("perBlock", false).forGetter(Random::perBlock),
                ResourceLocation.CODEC.listOf().fieldOf("replacements").forGetter(Random::replacements)
        ).apply(instance, Random::new));

        public record Replacement(boolean perBlock, List<ResourceLocation> variants) {
        }

        public Random(String originalString, boolean perBlock, List<String> replacementStrings) {
            this(ResourceLocation.parse(originalString), perBlock, replacementStrings.stream().map(ResourceLocation::parse).toList());
        }
    }

    public record TFCWorld(ResourceLocation original, String type) {
        public static final Codec<TFCWorld> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("original").forGetter(TFCWorld::original),
                Codec.STRING.fieldOf("type").forGetter(TFCWorld::type)
        ).apply(instance, TFCWorld::new));
    }

    private final List<Direct> directReplacements;
    private final List<Random> randomReplacements;
    private final List<TFCWorld> tfcWorldReplacements;

    private boolean mapIsInit;
    private Map<ResourceLocation, ResourceLocation> directMap;
    private HashMap<ResourceLocation, Random.Replacement> randomMap;
    private Map<ResourceLocation, String> tfcMap;

    public ReplacementPreset(List<Direct> directReplacements,
                             List<Random> randomReplacements,
                             List<TFCWorld> tfcWorldReplacements) {
        this.directReplacements = directReplacements;
        this.randomReplacements = randomReplacements;
        this.tfcWorldReplacements = tfcWorldReplacements;
    }

    public void createMapsIfNeed(Registry<Block> blockRegistry) {
        if (mapIsInit) {
            return;
        }

        createDirect(blockRegistry);
        createRandom(blockRegistry);
        createTfcMap(blockRegistry);
        mapIsInit = true;
    }

    private void createDirect(Registry<Block> blockRegistry) {
        directMap = new HashMap<>(directReplacements.size());
        for (Direct entry : directReplacements) {
            if (!blockRegistry.containsKey(entry.original)) {
                TFCStructuresMod.LOGGER.error("Original with ID {} not found", entry.original);
                continue;
            }

            if (!blockRegistry.containsKey(entry.replacement)) {
                TFCStructuresMod.LOGGER.error("Replacement with ID {} not found", entry.replacement);
                continue;
            }

            directMap.put(entry.original, entry.replacement);
        }
    }

    public Map<ResourceLocation, ResourceLocation> getDirectReplacementMap() {
        return directMap;
    }

    private void createRandom(Registry<Block> blockRegistry) {
        randomMap = new HashMap<>(randomReplacements.size());
        for (Random entry : randomReplacements) {
            if (!blockRegistry.containsKey(entry.original)) {
                TFCStructuresMod.LOGGER.error("Original for Random with ID {} not found", entry.original);
                continue;
            }

            List<ResourceLocation> replacements = new ArrayList<>(entry.replacements.size());
            for (var replacement : entry.replacements) {
                if (!blockRegistry.containsKey(replacement)) {
                    TFCStructuresMod.LOGGER.error("Random replacement with ID {} not found", replacement);
                    continue;
                }

                replacements.add(replacement);
            }

            if (replacements.isEmpty()) {
                TFCStructuresMod.LOGGER.error("There's no random replacements for {}", entry.original);
                continue;
            }

            randomMap.put(entry.original, new Random.Replacement(entry.perBlock, replacements));
        }
    }

    public Map<ResourceLocation, Random.Replacement> getRandomReplacementMap() {
        return randomMap;
    }

    private void createTfcMap(Registry<Block> blockRegistry) {
        tfcMap = new HashMap<>(tfcWorldReplacements.size());
        for (TFCWorld entry : tfcWorldReplacements) {
            if (!blockRegistry.containsKey(entry.original)) {
                TFCStructuresMod.LOGGER.error("Original for TFC replacement with ID {} not found", entry.original);
                continue;
            }

            tfcMap.put(entry.original, entry.type);
        }
    }

    public Map<ResourceLocation, String> getTfcWorldReplacementMap() {
        return tfcMap;
    }
}
