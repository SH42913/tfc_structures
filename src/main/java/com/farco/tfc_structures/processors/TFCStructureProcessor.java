package com.farco.tfc_structures.processors;

import com.farco.tfc_structures.config.ReplacementConfig;
import com.farco.tfc_structures.processors.features.DirectReplaceFeature;
import com.farco.tfc_structures.processors.features.ReplaceFeature;
import com.farco.tfc_structures.processors.features.TFCReplaceFeature;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class TFCStructureProcessor {
    private final List<ReplaceFeature> replaceFeatures;

    public TFCStructureProcessor(ReplacementConfig replacementConfig) {
        replaceFeatures = List.of(
                new DirectReplaceFeature(replacementConfig.getDirectReplacementMap()),
                new TFCReplaceFeature(replacementConfig.getTfcWorldReplacementMap())
        );
    }

    public BlockState replaceBlock(LevelReader levelReader, BlockPos pos, BlockState original) {
        ResourceLocation originalLocation = ForgeRegistries.BLOCKS.getKey(original.getBlock());
        if (originalLocation == null) {
            return original;
        }

        for (ReplaceFeature feature : replaceFeatures) {
            Block replacement = feature.replaceBlock(levelReader, pos, original, originalLocation);
            if (replacement != null) {
//        postProcessNewBlock(levelReader, pos); must be called after SetBlock, not now
                return replaceBlock(original, replacement);
            }
        }

        return original;
    }

    private static BlockState replaceBlock(BlockState originalBlockState, Block replacement) {
        if (replacement == null) {
            return originalBlockState;
        }

        BlockState newBlockState = replacement.defaultBlockState();
        if (newBlockState.isAir()) {
            return originalBlockState;
        }

        newBlockState = Helpers.copyProperties(newBlockState, originalBlockState);
        return newBlockState;
    }

//    private void postProcessNewBlock(@NotNull LevelReader levelReader, BlockPos pos) {
//        var worldGenLevel = (WorldGenLevel) levelReader;
//        var level = worldGenLevel.getLevel();
//        var blockEntity = level.getBlockEntity(pos);
//        if (blockEntity instanceof DecayingBlockEntity decaying) {
//            ItemStack stack = decaying.getStack();
//            IFood food = FoodCapability.get(stack);
//            if (food != null) {
//                food.setCreationDate(FoodCapability.getRoundedCreationDate());
//                decaying.setStack(stack);
//                blockEntity.setChanged();
//            }
//        }
//    }
}
