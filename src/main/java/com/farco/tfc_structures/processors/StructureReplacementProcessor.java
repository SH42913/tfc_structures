package com.farco.tfc_structures.processors;

import com.farco.tfc_structures.TFCStructuresMod;
import com.farco.tfc_structures.config.CommonConfig;
import com.farco.tfc_structures.config.ReplacementPreset;
import com.farco.tfc_structures.config.StructureConfig;
import com.farco.tfc_structures.processors.features.DirectReplaceFeature;
import com.farco.tfc_structures.processors.features.RandomReplaceFeature;
import com.farco.tfc_structures.processors.features.ReplaceFeature;
import com.farco.tfc_structures.processors.features.TFCReplaceFeature;
import com.farco.tfc_structures.utils.BlockStateHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class StructureReplacementProcessor {
    public static final ThreadLocal<StructureReplacementProcessor> THREAD_LOCAL = new ThreadLocal<>();
    private static final String LOOT_TABLE_NAME = RandomizableContainerBlockEntity.LOOT_TABLE_TAG;
    private static final String LOOT_TABLE_SEED_NAME = RandomizableContainerBlockEntity.LOOT_TABLE_SEED_TAG;
    private static final String ITEMS_NAME = "Items";

    private final @Nullable StructureConfig.Data structureData;
    private final List<ReplaceFeature> replaceFeatures;
    private final HashSet<BlockPos> registeredBlocks;
    private final HashSet<BlockPos> blocksToSkip;

    public StructureReplacementProcessor(@Nullable StructureConfig.Data structureData, ReplacementPreset replacementPreset) {
        this.structureData = structureData;

        replaceFeatures = new ArrayList<>();
        replaceFeatures.add(new DirectReplaceFeature(replacementPreset.getDirectReplacementMap()));
        replaceFeatures.add(new RandomReplaceFeature(replacementPreset.getRandomReplacementMap()));

        if (TFCStructuresMod.TFC_IS_LOADED) {
            replaceFeatures.add(new TFCReplaceFeature(replacementPreset.getTfcWorldReplacementMap()));
        }

        registeredBlocks = new HashSet<>();
        blocksToSkip = new HashSet<>();
    }

    public void registerBlock(BlockState state, BlockPos blockPos) {
        if (state.isAir()) {
            registeredBlocks.remove(blockPos);
        } else {
            registeredBlocks.add(blockPos);
        }
    }

    public void applyReplacements(WorldGenLevel level, RandomSource random, ChunkPos rootChunkPos, BoundingBox box, ChunkPos chunkPos) {
        for (ReplaceFeature feature : replaceFeatures) {
            feature.prepareData(level, random.fork(), rootChunkPos, box, chunkPos);
        }

        var chunkAccess = level.getChunk(chunkPos.x, chunkPos.z);
        var postProcessHelper = new PostProcessHelper(level, blocksToSkip);
        for (BlockPos pos : registeredBlocks) {
            if (blocksToSkip.contains(pos)) {
                continue;
            }

            BlockState originalState = level.getBlockState(pos);
            if (originalState.isAir()) {
                TFCStructuresMod.LOGGER.warn("Registered block at {} was moved or removed", pos);
                continue;
            }

            BlockEntity originalEntity = level.getBlockEntity(pos);
            Block newBlock = getReplacementBlock(level, pos, originalState);
            if (newBlock == null) {
                continue;
            }

            Block originalBlock = originalState.getBlock();
            TFCStructuresMod.LOGGER.debug("{} at {} replaced with {}", originalBlock, pos, newBlock);
            BlockState newState = BlockStateHelper.replaceBlock(newBlock, originalState);
            level.setBlock(pos, newState, Block.UPDATE_NONE);

            if (originalEntity != null) {
                replaceBlockEntity(pos, originalState, originalEntity, newState, chunkAccess, level, random);
            } else {
                createBlockEntity(pos, newState, chunkAccess);
            }

            for (ReplaceFeature feature : replaceFeatures) {
                feature.postProcessBlock(pos, originalBlock, newBlock, newState, postProcessHelper);
            }
        }

        registeredBlocks.clear();
        blocksToSkip.clear();
    }

    private Block getReplacementBlock(WorldGenLevel level, BlockPos pos, BlockState original) {
        ResourceLocation originalLocation = level.registryAccess().registryOrThrow(Registries.BLOCK).getKey(original.getBlock());
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

    private void replaceBlockEntity(BlockPos pos, BlockState originalState, BlockEntity originalEntity, BlockState newState, ChunkAccess chunkAccess, WorldGenLevel level, RandomSource random) {
        BlockEntity newEntity = null;
        if (newState.getBlock() instanceof EntityBlock entityBlock) {
            newEntity = entityBlock.newBlockEntity(pos, newState);
        }

        if (newEntity == null) {
            TFCStructuresMod.LOGGER.error("Replacement block {} can't fully replace {} due first one is not EntityBlock", newState.getBlock(), originalState.getBlock());
            return;
        }

        //noinspection DataFlowIssue due WorldGenLevel always has Server
        LootDataManager lootDataManager = level.getServer().getLootData();

        CompoundTag originalTag = originalEntity.saveWithFullMetadata();
        overrideLootTableInTag(originalTag, lootDataManager);
        initNonLootTableChest(originalTag, lootDataManager, random);
        newEntity.load(originalTag);
        newEntity.setChanged();

        chunkAccess.setBlockEntity(newEntity);
    }

    private void overrideLootTableInTag(CompoundTag originalTag, LootDataManager lootDataManager) {
        if (!originalTag.contains(LOOT_TABLE_NAME)) {
            return;
        }

        String originalLootTable = originalTag.getString(LOOT_TABLE_NAME);
        TFCStructuresMod.LOGGER.debug("Detected LootTable = {}", originalLootTable);

        String newLootTable = structureData != null && structureData.lootTablesOverrideMap() != null
                ? structureData.lootTablesOverrideMap().get(originalLootTable)
                : null;

        if (newLootTable == null && CommonConfig.FALLBACK_TO_TFC_STRUCTURES_LOOT.get()) {
            newLootTable = originalLootTable.replaceFirst("minecraft", TFCStructuresMod.MODID);
        }

        if (newLootTable != null) {
            TFCStructuresMod.LOGGER.debug("LootTable {} will be replaced with {}", originalLootTable, newLootTable);
            setLootTableToTag(originalTag, lootDataManager, newLootTable);
        }
    }

    private void initNonLootTableChest(CompoundTag originalTag, LootDataManager lootDataManager, RandomSource random) {
        if (originalTag.contains(LOOT_TABLE_NAME)) {
            return;
        }

        if (structureData == null || structureData.emptyChestLootTable().isEmpty()) {
            return;
        }

        boolean isEmptyChest = true;
        if (originalTag.contains(ITEMS_NAME)) {
            isEmptyChest = originalTag.getList(ITEMS_NAME, Tag.TAG_COMPOUND).isEmpty();
        }

        if (!isEmptyChest) {
            originalTag.remove(ITEMS_NAME);
            setLootTableToTag(originalTag, lootDataManager, structureData.emptyChestLootTable());
            originalTag.putLong(LOOT_TABLE_SEED_NAME, random.nextLong());
        }
    }

    private static void setLootTableToTag(CompoundTag originalTag, LootDataManager lootDataManager, String newLootTable) {
        if (newLootTable == null || newLootTable.isEmpty()) {
            return;
        }

        var lootTableLocation = ResourceLocation.parse(newLootTable);
        if (lootDataManager.getLootTable(lootTableLocation) != LootTable.EMPTY) {
            originalTag.putString(LOOT_TABLE_NAME, newLootTable);
        } else {
            TFCStructuresMod.LOGGER.warn("Can't use lootTable {} due it's not valid", newLootTable);
        }
    }

    private void createBlockEntity(BlockPos pos, BlockState newState, ChunkAccess chunkAccess) {
        BlockEntity newEntity = null;
        if (newState.getBlock() instanceof EntityBlock entityBlock) {
            newEntity = entityBlock.newBlockEntity(pos, newState);
        }

        if (newEntity != null) {
            chunkAccess.setBlockEntity(newEntity);
        }
    }
}
