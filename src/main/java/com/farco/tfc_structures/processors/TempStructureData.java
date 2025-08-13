package com.farco.tfc_structures.processors;

import net.minecraft.core.BlockPos;

import java.util.HashSet;

public class TempStructureData {
    public static final ThreadLocal<TempStructureData> CURRENT = new ThreadLocal<>();

    public final HashSet<BlockPos> blockPosSet = new HashSet<>();
}
