package com.farco.tfc_structures.processors;

import com.farco.tfc_structures.TFCStructuresMod;
import com.farco.tfc_structures.config.ReplacementConfig;
import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class TFCStructureProcessor extends StructureProcessor {
    public static final Codec<TFCStructureProcessor> CODEC = Codec.unit(new TFCStructureProcessor(null));

    private final Map<ResourceLocation, ResourceLocation> directReplacements;
    private final Map<ResourceLocation, String> tfcReplacements;

    public TFCStructureProcessor(ReplacementConfig replacementConfig) {
        if (replacementConfig == null) {
            directReplacements = new HashMap<>();
            tfcReplacements = new HashMap<>();
        } else {
            directReplacements = replacementConfig.getDirectReplacementMap();
            tfcReplacements = replacementConfig.getTfcWorldReplacementMap();
        }
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return ModStructureProcessors.TFC_PROCESSOR.get();
    }

    @Override
    public @Nullable StructureTemplate.StructureBlockInfo process(@NotNull LevelReader level,
                                                                  @NotNull BlockPos pos,
                                                                  @NotNull BlockPos pivot,
                                                                  @NotNull StructureTemplate.StructureBlockInfo original,
                                                                  @NotNull StructureTemplate.StructureBlockInfo current,
                                                                  @NotNull StructurePlaceSettings settings,
                                                                  @Nullable StructureTemplate template) {
        var newBlock = replaceBlock(level, current.pos(), current.state());
        return new StructureTemplate.StructureBlockInfo(
                current.pos(),
                newBlock,
                current.nbt()
        );
    }

    public BlockState replaceBlock(@NotNull LevelReader level, BlockPos pos, BlockState original) {
        ResourceLocation originalLocation = ForgeRegistries.BLOCKS.getKey(original.getBlock());
        if (originalLocation == null) {
            return original;
        }

        ResourceLocation replacementLocation = directReplacements.get(originalLocation);
        if (replacementLocation != null) {
            return replaceDirectly(original, replacementLocation);
        }

        String tfcWorldType = tfcReplacements.get(originalLocation);
        if (tfcWorldType == null) {
            return original;
        } else if (tfcWorldType.equals(ReplacementConfig.TFC_STONE_TYPE)) {
            return replaceTFCStone(level, pos, original, Rock.BlockType.HARDENED);
        } else if (tfcWorldType.equals(ReplacementConfig.TFC_BRICK_TYPE)) {
            return replaceTFCStone(level, pos, original, Rock.BlockType.BRICKS);
        }

        throw new RuntimeException("Type " + tfcWorldType + " is not supported");
    }

    private BlockState replaceDirectly(BlockState original, ResourceLocation replacementLocation) {
        Block replacement = ForgeRegistries.BLOCKS.getValue(replacementLocation);
        return replaceBlock(original, replacement);
    }

    private BlockState replaceTFCStone(@NotNull LevelReader level, BlockPos pos, BlockState original, Rock.BlockType blockType) {
        Block hardenedStone = getHardenedStone(level, pos);
        var rock = getRockFromHardened(hardenedStone);
        if (rock == null) {
            TFCStructuresMod.LOGGER.warn("Rock was not detected, so it will be hardened one");
            return replaceBlock(original, hardenedStone);
        }

        Block replacement;
        if (original.is(BlockTags.STAIRS)) {
            replacement = rock.getStair(blockType).get();
        } else if (original.is(BlockTags.SLABS)) {
            replacement = rock.getSlab(blockType).get();
        } else if (original.is(BlockTags.WALLS)) {
            replacement = rock.getWall(blockType).get();
        } else {
            replacement = rock.getBlock(blockType).get();
        }

        return replaceBlock(original, replacement);
    }

    private static Block getHardenedStone(LevelReader level, BlockPos pos) {
        WorldGenLevel worldGenLevel = (WorldGenLevel) level;
        ChunkDataProvider provider = ChunkDataProvider.get(worldGenLevel);
        var chunkData = provider.get(worldGenLevel, pos);
        return chunkData.getRockData().getSurfaceRock(pos.getX(), pos.getZ()).hardened();
    }

    private static BlockState replaceBlock(BlockState originalBlockState, Block replacement) {
        if (replacement == null) {
            return originalBlockState;
        }

        BlockState newBlockState = replacement.defaultBlockState();
        if (newBlockState.isAir()) {
            return originalBlockState;
        }

        return Helpers.copyProperties(newBlockState, originalBlockState);
    }

    private static Rock getRockFromHardened(Block block) {
        for (Rock rock : Rock.VALUES) {
            Block rockBlock = rock.getBlock(Rock.BlockType.HARDENED).get();
            if (block.equals(rockBlock)) {
                return rock;
            }
        }

        return null;
    }
}
