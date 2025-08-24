package com.farco.tfc_structures.processors.features;

import com.farco.tfc_structures.config.ReplacementConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RandomReplaceFeature implements ReplaceFeature {
    private final Map<ResourceLocation, ReplacementConfig.Random.Replacement> replacementMap;
    private Map<ResourceLocation, ResourceLocation> predefinedReplacementMap;
    private RandomSource localRandom;

    public RandomReplaceFeature(Map<ResourceLocation, ReplacementConfig.Random.Replacement> replacementMap) {
        this.replacementMap = replacementMap;
    }

    @Override
    public void prepareData(WorldGenLevel level, RandomSource random, ChunkPos rootChunkPos, BoundingBox box, ChunkPos chunkPos) {
        localRandom = random;

        predefinedReplacementMap = new HashMap<>();
        for (var entry : replacementMap.entrySet()) {
            var value = entry.getValue();
            if (value.perBlock()) {
                continue;
            }

            List<ResourceLocation> variants = value.variants();
            int randomIndex = localRandom.nextInt(variants.size());
            ResourceLocation replacement = value.variants().get(randomIndex);
            predefinedReplacementMap.put(entry.getKey(), replacement);
        }
    }

    @Override
    public @Nullable Block replaceBlock(WorldGenLevel level, BlockPos pos, BlockState originalState, ResourceLocation originalLocation) {
        Registry<Block> blockRegistry = level.registryAccess().registryOrThrow(Registries.BLOCK);
        ResourceLocation predefinedLocation = predefinedReplacementMap.get(originalLocation);
        if (predefinedLocation != null) {
            return blockRegistry.get(predefinedLocation);
        }

        var pair = replacementMap.get(originalLocation);
        if (pair == null) {
            return null;
        }

        List<ResourceLocation> variants = pair.variants();
        int randomIndex = localRandom.nextInt(variants.size());
        var replacementLocation = variants.get(randomIndex);
        return blockRegistry.get(replacementLocation);

    }
}
