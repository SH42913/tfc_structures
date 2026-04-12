package com.farco.tfc_structures.processors;

import com.farco.tfc_structures.utils.BlockStateHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;

public final class PostProcessHelper {
    public final WorldGenLevel level;
    private final HashSet<BlockPos> blocksToSkip;

    public PostProcessHelper(WorldGenLevel level, HashSet<BlockPos> blocksToSkip) {
        this.level = level;
        this.blocksToSkip = blocksToSkip;
    }

    public BlockState copyProperties(BlockState copyTo, BlockState copyFrom) {
        return BlockStateHelper.copyProperties(copyTo, copyFrom);
    }

    public void setPostProcessBlock(BlockPos pos, BlockState state) {
        level.setBlock(pos, state, Block.UPDATE_NONE);
        blocksToSkip.add(pos);
    }
}
