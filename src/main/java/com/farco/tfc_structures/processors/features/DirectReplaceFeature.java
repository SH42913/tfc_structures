package com.farco.tfc_structures.processors.features;

import com.farco.tfc_structures.processors.PostProcessHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DirectReplaceFeature implements ReplaceFeature {
    private final Map<ResourceLocation, ResourceLocation> replacementMap;

    public DirectReplaceFeature(Map<ResourceLocation, ResourceLocation> replacementMap) {
        this.replacementMap = replacementMap;
    }

    @Override
    public void prepareData(WorldGenLevel level, RandomSource random, ChunkPos rootChunkPos, BoundingBox box) {
    }

    @Override
    public @Nullable Block replaceBlock(WorldGenLevel level, BlockPos pos, BlockState originalState, ResourceLocation originalLocation) {
        ResourceLocation replacementLocation = replacementMap.get(originalLocation);
        return replacementLocation != null ? level.registryAccess().registryOrThrow(Registries.BLOCK).get(replacementLocation) : null;
    }

    @Override
    public void postProcessBlock(BlockPos pos, Block originalBlock, Block newBlock, BlockState newState, PostProcessHelper postProcessHelper) {
        BlockState secondPartState = postProcessHelper.copyProperties(newBlock.defaultBlockState(), newState);

        var bedPartProperty = BlockStateProperties.BED_PART;
        if (newState.hasProperty(bedPartProperty)) {
            BedPart value = newState.getValue(bedPartProperty);
            Direction direction = newState.getValue(BlockStateProperties.HORIZONTAL_FACING);
            if (value == BedPart.FOOT) {
                secondPartState = secondPartState.setValue(bedPartProperty, BedPart.HEAD);
            } else {
                secondPartState = secondPartState.setValue(bedPartProperty, BedPart.FOOT);
                direction = direction.getOpposite();
            }

            postProcessHelper.setPostProcessBlock(pos.relative(direction), secondPartState);
        }

        var doubleBlockHalfProperty = BlockStateProperties.DOUBLE_BLOCK_HALF;
        if (newState.hasProperty(doubleBlockHalfProperty)) {
            DoubleBlockHalf value = newState.getValue(doubleBlockHalfProperty);
            if (value == DoubleBlockHalf.LOWER) {
                secondPartState = secondPartState.setValue(doubleBlockHalfProperty, DoubleBlockHalf.UPPER);
                postProcessHelper.setPostProcessBlock(pos.above(), secondPartState);
            } else {
                secondPartState = secondPartState.setValue(doubleBlockHalfProperty, DoubleBlockHalf.LOWER);
                postProcessHelper.setPostProcessBlock(pos.below(), secondPartState);
            }
        }
    }

}
