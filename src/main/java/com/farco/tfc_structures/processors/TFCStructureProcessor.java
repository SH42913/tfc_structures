package com.farco.tfc_structures.processors;

import com.farco.tfc_structures.TFCStructuresMod;
import com.farco.tfc_structures.config.ReplacementConfig;
import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.OverworldClimateModel;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.feature.tree.ForestConfig;
import net.dries007.tfc.world.feature.tree.RandomTreeConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.common.Tags;
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
            return replaceTFCStone(level, pos, original, original.is(TFCStructuresMod.MOSSY_TAG)
                    ? Rock.BlockType.MOSSY_COBBLE
                    : Rock.BlockType.HARDENED);
        } else if (tfcWorldType.equals(ReplacementConfig.TFC_BRICK_TYPE)) {
            return replaceTFCStone(level, pos, original, original.is(TFCStructuresMod.MOSSY_TAG)
                    ? Rock.BlockType.MOSSY_BRICKS
                    : Rock.BlockType.BRICKS);
        } else if (tfcWorldType.equals(ReplacementConfig.TFC_WOOD_TYPE)) {
            return replaceTFCWood(level, pos, original);
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

    private BlockState replaceTFCWood(@NotNull LevelReader level, BlockPos pos, BlockState original) {
        BiomeGenerationSettings generationSettings = level.getBiome(pos).get().getGenerationSettings();
        ForestConfig forestConfig = getForestConfig(generationSettings);
        if (forestConfig == null) {
            TFCStructuresMod.LOGGER.warn("Forest config was not detected, can't replace block");
            return original;
        }

        ForestConfig.Entry forestEntry = getForestEntry((WorldGenLevel) level, pos, forestConfig);
        if (forestEntry == null) {
            TFCStructuresMod.LOGGER.warn("Forest entry was not detected, can't replace block");
            return original;
        }

        Wood wood = getWood(forestEntry);
        if (wood == null) {
            TFCStructuresMod.LOGGER.warn("Wood was not detected, can't replace block");
            return original;
        }

        Wood.BlockType blockType = Wood.BlockType.WOOD;
        if (original.is(BlockTags.LOGS)) {
            blockType = Wood.BlockType.LOG;
        } else if (original.is(BlockTags.PLANKS)) {
            blockType = Wood.BlockType.PLANKS;
        } else if (original.is(BlockTags.SLABS)) {
            blockType = Wood.BlockType.SLAB;
        } else if (original.is(BlockTags.STAIRS)) {
            blockType = Wood.BlockType.STAIRS;
        } else if (original.is(BlockTags.FENCES)) {
            blockType = Wood.BlockType.FENCE;
        } else if (original.is(BlockTags.FENCE_GATES)) {
            blockType = Wood.BlockType.FENCE_GATE;
        } else if (original.is(BlockTags.DOORS)) {
            blockType = Wood.BlockType.DOOR;
        } else if (original.is(Tags.Blocks.CHESTS)) {
            blockType = Wood.BlockType.CHEST;
        }

        Block replacement = wood.getBlock(blockType).get();
        return replaceBlock(original, replacement);
    }

    private static ForestConfig getForestConfig(BiomeGenerationSettings generationSettings) {
        for (HolderSet<PlacedFeature> placedHolderSet : generationSettings.features()) {
            for (Holder<PlacedFeature> placedHolder : placedHolderSet) {
                PlacedFeature placedFeature = placedHolder.value();
                var feature = placedFeature.feature().value();
                if (feature.config() instanceof ForestConfig forestConfig) {
                    return forestConfig;
                }
            }
        }

        return null;
    }

    private static ForestConfig.Entry getForestEntry(WorldGenLevel level, BlockPos pos, ForestConfig forestConfig) {
        ChunkData chunkData = ChunkDataProvider.get(level).get(level, pos);
        float rainfall = chunkData.getRainfall(pos);
        float averageTemperature = OverworldClimateModel.getAdjustedAverageTempByElevation(pos, chunkData);
        for (var entryHolder : forestConfig.entries()) {
            var forestEntry = (ForestConfig.Entry) entryHolder.value().config();
            if (forestEntry.isValid(averageTemperature, rainfall)) {
                return forestEntry;
            }
        }

        return null;
    }

    private Wood getWood(ForestConfig.Entry forestEntry) {
        var configuredFeature = forestEntry.treeFeature().get();
        if (configuredFeature.config() instanceof RandomTreeConfig treeConfig) {
            ResourceLocation first = treeConfig.structureNames().get(0);
            for (Wood wood : Wood.VALUES) {
                if (first.getPath().startsWith(wood.getSerializedName())) {
                    return wood;
                }
            }
        }

        return null;
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
