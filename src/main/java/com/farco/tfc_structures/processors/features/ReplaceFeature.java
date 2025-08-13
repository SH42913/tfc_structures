package com.farco.tfc_structures.processors.features;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface ReplaceFeature {
    @Nullable Block replaceBlock(LevelReader levelReader, BlockPos pos, BlockState originalState, ResourceLocation originalLocation);
}
