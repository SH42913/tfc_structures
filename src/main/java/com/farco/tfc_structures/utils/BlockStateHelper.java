package com.farco.tfc_structures.utils;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

public class BlockStateHelper {
    public static @NotNull BlockState replaceBlock(Block newBlock, BlockState originalState) {
        BlockState newState = newBlock.defaultBlockState();
        newState = copyProperties(newState, originalState);
        return newState;
    }

    public static BlockState copyProperties(BlockState copyTo, BlockState copyFrom) {
        for (Property<?> property : copyFrom.getProperties()) {
            copyTo = copyProperty(copyTo, copyFrom, property);
        }

        return copyTo;
    }

    public static <T extends Comparable<T>> BlockState copyProperty(BlockState copyTo, BlockState copyFrom, Property<T> property) {
        return copyTo.hasProperty(property) ? copyTo.setValue(property, copyFrom.getValue(property)) : copyTo;
    }
}
