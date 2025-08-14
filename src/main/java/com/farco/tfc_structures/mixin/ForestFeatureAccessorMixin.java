package com.farco.tfc_structures.mixin;

import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.feature.tree.ForestConfig;
import net.dries007.tfc.world.feature.tree.ForestFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ForestFeature.class)
public interface ForestFeatureAccessorMixin {
    @Invoker(value = "getTree", remap = false)
    ForestConfig.@Nullable Entry invokeGetTree(ChunkData chunkData, RandomSource random, ForestConfig config, BlockPos pos);
}
