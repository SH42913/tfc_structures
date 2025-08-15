package com.farco.tfc_structures.processors;

import com.farco.tfc_structures.TFCStructuresMod;
import com.farco.tfc_structures.config.ReplacementConfig;
import com.farco.tfc_structures.processors.features.DirectReplaceFeature;
import com.farco.tfc_structures.processors.features.ReplaceFeature;
import com.farco.tfc_structures.processors.features.TFCReplaceFeature;
import net.dries007.tfc.common.blockentities.DecayingBlockEntity;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;

public class StructureReplacementProcessor {
    public static final ThreadLocal<StructureReplacementProcessor> THREAD_LOCAL = new ThreadLocal<>();

    private final List<ReplaceFeature> replaceFeatures;
    private final HashSet<BlockPos> registeredBlocks;

    public StructureReplacementProcessor(ReplacementConfig replacementConfig) {
        replaceFeatures = List.of(
                new DirectReplaceFeature(replacementConfig.getDirectReplacementMap()),
                new TFCReplaceFeature(replacementConfig.getTfcWorldReplacementMap())
        );

        registeredBlocks = new HashSet<>();
    }

    public void registerBlock(BlockPos blockPos) {
        registeredBlocks.add(blockPos);
    }

    public void applyReplacements(WorldGenLevel level, RandomSource random, ChunkPos rootChunkPos, BoundingBox box, ChunkPos chunkPos) {
        for (ReplaceFeature feature : replaceFeatures) {
            feature.prepareData(level, random, rootChunkPos, box, chunkPos);
        }

        var chunkAccess = level.getChunk(chunkPos.x, chunkPos.z);
        for (BlockPos pos : registeredBlocks) {
            BlockState originalState = chunkAccess.getBlockState(pos);
            if (originalState.isAir()) {
                TFCStructuresMod.LOGGER.warn("Registered block at {} was moved or removed", pos);
                continue;
            }

            BlockEntity originalEntity = chunkAccess.getBlockEntity(pos);

            Block newBlock = getReplacementBlock(level, pos, originalState);
            if (newBlock == null) {
                continue;
            }

            BlockState newState = newBlock.defaultBlockState();
            newState = Helpers.copyProperties(newState, originalState);
            chunkAccess.setBlockState(pos, newState, false);

            ReplaceBlockEntity(level, pos, originalState, originalEntity, newState, chunkAccess);
            postProcessNewBlock(level, pos);
        }
    }

    private Block getReplacementBlock(WorldGenLevel level, BlockPos pos, BlockState original) {
        ResourceLocation originalLocation = ForgeRegistries.BLOCKS.getKey(original.getBlock());
        if (originalLocation == null) {
            return null;
        }

        for (ReplaceFeature feature : replaceFeatures) {
            Block replacement = feature.replaceBlock(level, pos, original, originalLocation);
            if (replacement != null) {
                return replacement;
            }
        }

        return null;
    }

    private static void ReplaceBlockEntity(WorldGenLevel level, BlockPos pos, BlockState originalState, BlockEntity originalEntity, BlockState newState, ChunkAccess chunkAccess) {
        if (originalEntity == null) {
            return;
        }

        BlockEntity newEntity = null;
        if (newState.getBlock() instanceof EntityBlock entityBlock) {
            newEntity = entityBlock.newBlockEntity(pos, newState);
        }

        if (newEntity == null) {
            var blockRegistry = level.registryAccess().registryOrThrow(Registries.BLOCK);
            var originalLocation = blockRegistry.getKey(originalState.getBlock());
            var newLocation = blockRegistry.getKey(newState.getBlock());
            TFCStructuresMod.LOGGER.error("Replacement block {} can't fully replace {} due first one is not EntityBlock", newLocation, originalLocation);
            return;
        }

        var originalTag = originalEntity.saveWithFullMetadata();
        newEntity.load(originalTag);
        chunkAccess.setBlockEntity(newEntity);
    }

    private void postProcessNewBlock(WorldGenLevel worldGenLevel, BlockPos pos) {
        var level = worldGenLevel.getLevel();
        var blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof DecayingBlockEntity decaying) {
            ItemStack stack = decaying.getStack();
            if (stack.getCount() < 1) {
                stack.setCount(1);
            }

            IFood food = FoodCapability.get(stack);
            if (food != null) {
                food.setCreationDate(FoodCapability.getRoundedCreationDate());
                decaying.setStack(stack);
                blockEntity.setChanged();
            }
        }
    }
}
