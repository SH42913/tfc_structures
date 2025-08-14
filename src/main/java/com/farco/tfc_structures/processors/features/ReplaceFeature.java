package com.farco.tfc_structures.processors.features;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.Nullable;

public interface ReplaceFeature {
    void prepareData(WorldGenLevel level, RandomSource random, ChunkPos boundingBox, BoundingBox box, ChunkPos chunkPos);
    @Nullable Block replaceBlock(WorldGenLevel level, BlockPos pos, BlockState originalState, ResourceLocation originalLocation);
}
