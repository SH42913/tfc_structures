package com.farco.tfc_structures.processors;

import com.farco.tfc_structures.TFCStructuresMod;
import com.farco.tfc_structures.config.ReplacementConfig;
import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.SandstoneBlockType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.OverworldClimateModel;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.feature.tree.ForestConfig;
import net.dries007.tfc.world.feature.tree.RandomTreeConfig;
import net.dries007.tfc.world.settings.RockSettings;
import net.dries007.tfc.world.surface.SoilSurfaceState;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
import java.util.List;
import java.util.Map;

public class TFCStructureProcessor extends StructureProcessor {
    public static final Codec<TFCStructureProcessor> CODEC = Codec.unit(new TFCStructureProcessor(null));

    private record Pair<T1, T2>(T1 first, T2 second) {
    }

    private final Map<ResourceLocation, ResourceLocation> directReplacements;
    private final Map<ResourceLocation, String> tfcReplacements;

    private final Map<Block, Wood.BlockType> blockToWoodBlockTypeMap;
    private final List<Pair<TagKey<Block>, Wood.BlockType>> tagToWoodBlockTypeMappings;
    private final List<Pair<Block, SoilBlockType>> blockToSoilBlockTypeMappings;

    private DummySurfaceBuilderContext surfaceBuilderContext;

    public TFCStructureProcessor(ReplacementConfig replacementConfig) {
        if (replacementConfig == null) {
            directReplacements = new HashMap<>();
            tfcReplacements = new HashMap<>();
        } else {
            directReplacements = replacementConfig.getDirectReplacementMap();
            tfcReplacements = replacementConfig.getTfcWorldReplacementMap();
        }

        blockToWoodBlockTypeMap = Map.of(
                Blocks.CRAFTING_TABLE, Wood.BlockType.WORKBENCH,
                Blocks.LECTERN, Wood.BlockType.LECTERN
        );

        tagToWoodBlockTypeMappings = List.of(
                new Pair<>(BlockTags.LEAVES, Wood.BlockType.LEAVES),
                new Pair<>(BlockTags.PLANKS, Wood.BlockType.PLANKS),
                new Pair<>(BlockTags.WOODEN_DOORS, Wood.BlockType.DOOR),
                new Pair<>(BlockTags.WOODEN_TRAPDOORS, Wood.BlockType.TRAPDOOR),
                new Pair<>(BlockTags.WOODEN_FENCES, Wood.BlockType.FENCE),
                new Pair<>(BlockTags.FENCE_GATES, Wood.BlockType.FENCE_GATE),
                new Pair<>(BlockTags.WOODEN_BUTTONS, Wood.BlockType.BUTTON),
                new Pair<>(BlockTags.WOODEN_PRESSURE_PLATES, Wood.BlockType.PRESSURE_PLATE),
                new Pair<>(BlockTags.WOODEN_SLABS, Wood.BlockType.SLAB),
                new Pair<>(BlockTags.WOODEN_STAIRS, Wood.BlockType.STAIRS),
                new Pair<>(BlockTags.STANDING_SIGNS, Wood.BlockType.SIGN),
                new Pair<>(BlockTags.WALL_SIGNS, Wood.BlockType.WALL_SIGN),
                new Pair<>(Tags.Blocks.CHESTS_TRAPPED, Wood.BlockType.TRAPPED_CHEST),
                new Pair<>(Tags.Blocks.CHESTS_WOODEN, Wood.BlockType.CHEST),
                new Pair<>(Tags.Blocks.BOOKSHELVES, Wood.BlockType.BOOKSHELF),
                new Pair<>(TFCStructuresMod.STRIPPED_LOG_TAG, Wood.BlockType.STRIPPED_LOG),
                new Pair<>(TFCStructuresMod.STRIPPED_WOOD_TAG, Wood.BlockType.STRIPPED_WOOD),
                new Pair<>(BlockTags.LOGS, Wood.BlockType.LOG)
        );

        blockToSoilBlockTypeMappings = List.of(
                new Pair<>(Blocks.DIRT, SoilBlockType.DIRT),
                new Pair<>(Blocks.GRASS_BLOCK, SoilBlockType.GRASS),
                new Pair<>(Blocks.DIRT_PATH, SoilBlockType.GRASS_PATH),
                new Pair<>(Blocks.FARMLAND, SoilBlockType.FARMLAND),
                new Pair<>(Blocks.ROOTED_DIRT, SoilBlockType.ROOTED_DIRT),
                new Pair<>(Blocks.MUD, SoilBlockType.MUD),
                new Pair<>(Blocks.MUD_BRICKS, SoilBlockType.MUD_BRICKS),
                new Pair<>(Blocks.MUDDY_MANGROVE_ROOTS, SoilBlockType.MUDDY_ROOTS)
        );
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

    public BlockState replaceBlock(@NotNull LevelReader levelReader, BlockPos pos, BlockState original) {
        ResourceLocation originalLocation = ForgeRegistries.BLOCKS.getKey(original.getBlock());
        if (originalLocation == null) {
            return original;
        }

        ResourceLocation replacementLocation = directReplacements.get(originalLocation);
        if (replacementLocation != null) {
            return replaceDirectly(original, replacementLocation);
        }

        BlockState replacedBlockState = null;
        String tfcWorldType = tfcReplacements.get(originalLocation);
        if (tfcWorldType == null || tfcWorldType.equals(ReplacementConfig.TFC_SKIP_TYPE)) {
            replacedBlockState = original;
        } else if (tfcWorldType.equals(ReplacementConfig.TFC_STONE_TYPE)) {
            replacedBlockState = replaceTFCStone(levelReader, pos, original, original.is(TFCStructuresMod.MOSSY_TAG)
                    ? Rock.BlockType.MOSSY_COBBLE
                    : Rock.BlockType.SMOOTH);
        } else if (tfcWorldType.equals(ReplacementConfig.TFC_BRICK_TYPE)) {
            replacedBlockState = replaceTFCStone(levelReader, pos, original, original.is(TFCStructuresMod.MOSSY_TAG)
                    ? Rock.BlockType.MOSSY_BRICKS
                    : Rock.BlockType.BRICKS);
        } else if (tfcWorldType.equals(ReplacementConfig.TFC_WOOD_TYPE)) {
            replacedBlockState = replaceTFCWood(levelReader, pos, original);
        } else if (tfcWorldType.equals(ReplacementConfig.TFC_SOIL_TYPE)) {
            replacedBlockState = replaceTFCSoil(levelReader, pos, original);
        } else if (tfcWorldType.equals(ReplacementConfig.TFC_SAND_TYPE)) {
            replacedBlockState = replaceTFCSand(levelReader, pos, original);
        }

        if (replacedBlockState == null) {
            throw new RuntimeException("Type " + tfcWorldType + " is not supported");
        }

//        postProcessNewBlock(levelReader, pos); must be called after SetBlock, not now
        return replacedBlockState;
    }

    private BlockState replaceDirectly(BlockState original, ResourceLocation replacementLocation) {
        Block replacement = ForgeRegistries.BLOCKS.getValue(replacementLocation);
        return replaceBlock(original, replacement);
    }

    private BlockState replaceTFCStone(@NotNull LevelReader level, BlockPos pos, BlockState original, Rock.BlockType blockType) {
        Block hardenedStone = getRockSettings(level, pos).hardened();
        var rock = getRockByHardenedBlock(hardenedStone);
        if (rock == null) {
            TFCStructuresMod.LOGGER.warn("Rock was not detected, so it will be hardened one");
            return replaceBlock(original, hardenedStone);
        }

        Block replacement;
        if (original.is(BlockTags.STONE_BUTTONS)) {
            replacement = rock.getBlock(Rock.BlockType.BUTTON).get();
        } else if (original.is(BlockTags.STONE_PRESSURE_PLATES)) {
            replacement = rock.getBlock(Rock.BlockType.PRESSURE_PLATE).get();
        } else if (original.is(BlockTags.STAIRS)) {
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

    private static RockSettings getRockSettings(LevelReader level, BlockPos pos) {
        WorldGenLevel worldGenLevel = (WorldGenLevel) level;
        ChunkDataProvider provider = ChunkDataProvider.get(worldGenLevel);
        var chunkData = provider.get(worldGenLevel, pos);
        return chunkData.getRockData().getSurfaceRock(pos.getX(), pos.getZ());
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

        Wood.BlockType blockType = null;
        Block originalBlock = original.getBlock();
        var possibleReplacement = blockToWoodBlockTypeMap.get(originalBlock);
        if (possibleReplacement != null) {
            blockType = possibleReplacement;
        } else {
            for (var pair : tagToWoodBlockTypeMappings) {
                if (original.is(pair.first)) {
                    blockType = pair.second;
                    break;
                }
            }
        }

        if (blockType == null) {
            TFCStructuresMod.LOGGER.warn("Wood block type was not detected, will be used common WOOD");
            blockType = Wood.BlockType.WOOD;
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

    private BlockState replaceTFCSoil(@NotNull LevelReader levelReader, BlockPos pos, BlockState original) {
        SoilBlockType blockType = null;
        for (var mapping : blockToSoilBlockTypeMappings) {
            if (original.is(mapping.first)) {
                blockType = mapping.second;
                break;
            }
        }

        if (blockType == null) {
            TFCStructuresMod.LOGGER.warn("Soil block type was not detected, will be used common DIRT");
            blockType = SoilBlockType.DIRT;
        }

        SurfaceState surfaceState = SoilSurfaceState.buildType(blockType);
        SurfaceBuilderContext context = buildTempContext(levelReader, pos);
        return surfaceState.getState(context);
    }

    private SurfaceBuilderContext buildTempContext(LevelReader levelReader, BlockPos pos) {
        WorldGenLevel worldGenLevel = (WorldGenLevel) levelReader;
        ChunkData chunkData = ChunkDataProvider.get(worldGenLevel).get(worldGenLevel, pos);
        if (surfaceBuilderContext == null) {
            var chunkGenerator = (TFCChunkGenerator) worldGenLevel.getLevel().getChunkSource().getGenerator();
            var rockLayerSettings = chunkGenerator.rockLayerSettings();
            surfaceBuilderContext = new DummySurfaceBuilderContext(chunkData, rockLayerSettings);
        }

        var accessor = surfaceBuilderContext.getAccessor();
        accessor.setRockData(chunkData.getRockData());
        accessor.setRainfall(chunkData.getRainfall(pos));
        accessor.setCursor(pos.mutable());

        return surfaceBuilderContext;
    }

    private BlockState replaceTFCSand(@NotNull LevelReader levelReader, BlockPos pos, BlockState original) {
        boolean isSandstoneBlock = original.is(Tags.Blocks.SANDSTONE);
        boolean isStair = original.is(BlockTags.STAIRS);
        boolean isSlab = original.is(BlockTags.SLABS);
        boolean isWall = original.is(BlockTags.WALLS);
        boolean isCommonSandstone = isSandstoneBlock || isStair || isSlab || isWall;

        RockSettings rockSettings = getRockSettings(levelReader, pos);
        if (!isCommonSandstone) {
            if (original.is(Tags.Blocks.GRAVEL)) {
                return replaceBlock(original, rockSettings.gravel());
            } else {
                return replaceBlock(original, rockSettings.sand());
            }
        }

        var sandBlockType = genSandBlockType(rockSettings.sand());
        if (sandBlockType == null) {
            TFCStructuresMod.LOGGER.warn("SandBlockType was not detected, can't replace block");
            return original;
        }

        Block originalBlock = original.getBlock();
        var sandstoneBlockType = getSandstoneBlockType(originalBlock);
        if (sandstoneBlockType == null) {
            TFCStructuresMod.LOGGER.warn("SandStoneBlockType was not detected, can't replace block");
            return original;
        }

        Block replacement;
        var decorations = TFCBlocks.SANDSTONE_DECORATIONS;
        if (isStair) {
            replacement = decorations.get(sandBlockType).get(sandstoneBlockType).stair().get();
        } else if (isSlab) {
            replacement = decorations.get(sandBlockType).get(sandstoneBlockType).slab().get();
        } else if (isWall) {
            replacement = decorations.get(sandBlockType).get(sandstoneBlockType).wall().get();
        } else {
            replacement = TFCBlocks.SANDSTONE.get(sandBlockType).get(sandstoneBlockType).get();
        }

        return replaceBlock(original, replacement);
    }

    private static SandBlockType genSandBlockType(Block originalBlock) {
        for (var entry : TFCBlocks.SAND.entrySet()) {
            if (originalBlock == entry.getValue().get()) {
                return entry.getKey();
            }
        }

        return null;
    }

    private static SandstoneBlockType getSandstoneBlockType(Block originalBlock) {
        var resourceLocation = ForgeRegistries.BLOCKS.getKey(originalBlock);
        if (resourceLocation == null) {
            return null;
        }

        String path = resourceLocation.getPath();
        if (path.startsWith(SandstoneBlockType.CUT.name().toLowerCase()) || path.startsWith("chiseled")) {
            return SandstoneBlockType.CUT;
        } else if (path.startsWith(SandstoneBlockType.SMOOTH.name().toLowerCase())) {
            return SandstoneBlockType.SMOOTH;
        } else {
            return SandstoneBlockType.RAW;
        }
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

    private static Rock getRockByHardenedBlock(Block block) {
        for (Rock rock : Rock.VALUES) {
            Block rockBlock = rock.getBlock(Rock.BlockType.HARDENED).get();
            if (block.equals(rockBlock)) {
                return rock;
            }
        }

        return null;
    }
}
