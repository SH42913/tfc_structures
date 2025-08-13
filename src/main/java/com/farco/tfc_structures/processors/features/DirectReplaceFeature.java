package com.farco.tfc_structures.processors.features;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DirectReplaceFeature implements ReplaceFeature {
    private final Map<ResourceLocation, ResourceLocation> replacementMap;

    public DirectReplaceFeature(Map<ResourceLocation, ResourceLocation> replacementMap) {
        this.replacementMap = replacementMap;
    }

    @Override
    public @Nullable Block replaceBlock(WorldGenLevel level, BlockPos pos, BlockState originalState, ResourceLocation originalLocation) {
        ResourceLocation replacementLocation = replacementMap.get(originalLocation);
        return replacementLocation != null ? ForgeRegistries.BLOCKS.getValue(replacementLocation) : null;
    }

}
