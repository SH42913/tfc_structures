package com.farco.tfc_structures.processors;

import com.farco.tfc_structures.TFCStructuresMod;
import com.farco.tfc_structures.config.ReplacementPreset;
import com.farco.tfc_structures.config.StructureConfig;
import com.farco.tfc_structures.processors.features.DirectReplaceFeature;
import com.farco.tfc_structures.processors.features.RandomReplaceFeature;
import com.farco.tfc_structures.processors.features.ReplaceFeature;
import com.farco.tfc_structures.processors.features.TFCReplaceFeature;
import net.dries007.tfc.common.blockentities.DecayingBlockEntity;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.plant.ITallPlant;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;

public class StructureReplacementProcessor {
    public static final ThreadLocal<StructureReplacementProcessor> THREAD_LOCAL = new ThreadLocal<>();
    private static final String LOOT_TABLE_NAME = "LootTable";
    private static final TagKey<Block> TFC_SHELVES = TagKey.create(Registries.BLOCK, ResourceLocation.parse("tfc:bookshelves"));
    private static final List<Direction> HORIZONTAL_DIRECTIONS = List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);

    private final @Nullable StructureConfig.Data structureData;
    private final List<ReplaceFeature> replaceFeatures;
    private final HashSet<BlockPos> registeredBlocks;
    private final HashSet<BlockPos> blocksToSkip;

    public StructureReplacementProcessor(@Nullable StructureConfig.Data structureData, ReplacementPreset replacementPreset) {
        this.structureData = structureData;

        replaceFeatures = List.of(
                new DirectReplaceFeature(replacementPreset.getDirectReplacementMap()),
                new RandomReplaceFeature(replacementPreset.getRandomReplacementMap()),
                new TFCReplaceFeature(replacementPreset.getTfcWorldReplacementMap())
        );

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
        for (BlockPos pos : registeredBlocks) {
            if (blocksToSkip.contains(pos)) {
                continue;
            }

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

            BlockState newState = replaceBlock(newBlock, originalState);
            chunkAccess.setBlockState(pos, newState, false);

            if (originalEntity != null) {
                replaceBlockEntity(pos, originalState, originalEntity, newState, chunkAccess, level);
            } else {
                createBlockEntity(pos, newState, chunkAccess);
            }

            postProcessNewBlock(pos, newBlock, newState, chunkAccess);
        }
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

    private static @NotNull BlockState replaceBlock(Block newBlock, BlockState originalState) {
        BlockState newState = newBlock.defaultBlockState();
        newState = Helpers.copyProperties(newState, originalState);
        return newState;
    }

    private void replaceBlockEntity(BlockPos pos, BlockState originalState, BlockEntity originalEntity, BlockState newState, ChunkAccess chunkAccess, WorldGenLevel level) {
        BlockEntity newEntity = null;
        if (newState.getBlock() instanceof EntityBlock entityBlock) {
            newEntity = entityBlock.newBlockEntity(pos, newState);
        }

        if (newEntity == null) {
            var originalLocation = ForgeRegistries.BLOCKS.getKey(originalState.getBlock());
            var newLocation = ForgeRegistries.BLOCKS.getKey(newState.getBlock());
            TFCStructuresMod.LOGGER.error("Replacement block {} can't fully replace {} due first one is not EntityBlock", newLocation, originalLocation);
            return;
        }

        CompoundTag originalTag = originalEntity.saveWithFullMetadata();
        //noinspection DataFlowIssue due WorldGenLevel always has Server
        replaceLootTable(originalTag, level.getServer().getLootData());
        newEntity.load(originalTag);
        chunkAccess.setBlockEntity(newEntity);
    }

    private void replaceLootTable(CompoundTag originalTag, LootDataManager lootData) {
        if (!originalTag.contains(LOOT_TABLE_NAME)) {
            return;
        }

        String originalLootTable = originalTag.getString(LOOT_TABLE_NAME);
        TFCStructuresMod.LOGGER.debug("Detected LootTable = {}", originalLootTable);

        String newLootTable = structureData != null && structureData.lootTablesMap() != null
                ? structureData.lootTablesMap().get(originalLootTable)
                : null;

        if (newLootTable == null) {
            newLootTable = originalLootTable.replace("minecraft", TFCStructuresMod.MODID);
        }

        var lootTableLocation = ResourceLocation.parse(newLootTable);
        if (lootData.getLootTable(lootTableLocation) == LootTable.EMPTY) {
            TFCStructuresMod.LOGGER.warn("Can't replace {} with {} due it's not valid", originalLootTable, newLootTable);
            return;
        }

        originalTag.putString(LOOT_TABLE_NAME, newLootTable);
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

    private void postProcessNewBlock(BlockPos pos, Block newBlock, BlockState newState, ChunkAccess chunkAccess) {
        postProcessDoubleBlocks(pos, newBlock, newState, chunkAccess);

        var blockEntity = chunkAccess.getBlockEntity(pos);
        if (blockEntity instanceof DecayingBlockEntity decaying) {
            Item item = newBlock.asItem();
            ItemStack itemStack = new ItemStack(item, 1);
            FoodCapability.get(itemStack);

            IFood food = FoodCapability.get(itemStack);
            if (food != null) {
                food.setCreationDate(FoodCapability.getRoundedCreationDate());
                decaying.setStack(itemStack);
                blockEntity.setChanged();
            }
        }

        if (newState.is(TFC_SHELVES)) {
            for (Direction direction : HORIZONTAL_DIRECTIONS) {
                var neighbourPos = pos.relative(direction);
                var neighbourState = chunkAccess.getBlockState(neighbourPos);
                if (neighbourState.getCollisionShape(chunkAccess, neighbourPos).isEmpty()) {
                    newState = newState.setValue(BlockStateProperties.HORIZONTAL_FACING, direction);
                    chunkAccess.setBlockState(pos, newState, false);
                    break;
                }
            }
        }
    }

    private void postProcessDoubleBlocks(BlockPos pos, Block newBlock, BlockState newState, ChunkAccess chunkAccess) {
        var bedPartProperty = BlockStateProperties.BED_PART;
        var doubleBlockHalfProperty = BlockStateProperties.DOUBLE_BLOCK_HALF;
        var tallPlantPartProperty = TFCBlockStateProperties.TALL_PLANT_PART;

        BlockPos secondPartPos = null;
        BlockState secondPartState = Helpers.copyProperties(newBlock.defaultBlockState(), newState);
        if (newState.hasProperty(bedPartProperty)) {
            BedPart value = newState.getValue(bedPartProperty);
            Direction direction = newState.getValue(BlockStateProperties.HORIZONTAL_FACING);
            secondPartPos = pos.relative(direction);
            secondPartState = value == BedPart.FOOT
                    ? secondPartState.setValue(bedPartProperty, BedPart.HEAD)
                    : secondPartState.setValue(bedPartProperty, BedPart.FOOT);
        } else if (newState.hasProperty(doubleBlockHalfProperty)) {
            DoubleBlockHalf value = newState.getValue(doubleBlockHalfProperty);
            if (value == DoubleBlockHalf.LOWER) {
                secondPartPos = pos.above();
                secondPartState = secondPartState.setValue(doubleBlockHalfProperty, DoubleBlockHalf.UPPER);
            } else {
                secondPartPos = pos.below();
                secondPartState = secondPartState.setValue(doubleBlockHalfProperty, DoubleBlockHalf.LOWER);
            }
        } else if (newState.hasProperty(tallPlantPartProperty)) {
            ITallPlant.Part value = newState.getValue(tallPlantPartProperty);
            if (value == ITallPlant.Part.LOWER) {
                secondPartPos = pos.above();
                secondPartState = secondPartState.setValue(tallPlantPartProperty, ITallPlant.Part.UPPER);
            } else {
                secondPartPos = pos.below();
                secondPartState = secondPartState.setValue(tallPlantPartProperty, ITallPlant.Part.LOWER);
            }
        }

        if (secondPartPos != null) {
            chunkAccess.setBlockState(secondPartPos, secondPartState, false);
            blocksToSkip.add(secondPartPos);
        }
    }
}
