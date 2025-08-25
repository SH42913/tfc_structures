package com.farco.tfc_structures.processors.features;

import com.farco.tfc_structures.TFCStructuresMod;
import com.farco.tfc_structures.config.ReplacementPreset;
import com.farco.tfc_structures.mixin.ForestFeatureAccessorMixin;
import com.farco.tfc_structures.processors.DummySurfaceBuilderContext;
import com.farco.tfc_structures.utils.Pair;
import net.dries007.tfc.common.blocks.SandstoneBlockType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.util.climate.OverworldClimateModel;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.chunkdata.RockData;
import net.dries007.tfc.world.feature.tree.*;
import net.dries007.tfc.world.settings.RockSettings;
import net.dries007.tfc.world.surface.SoilSurfaceState;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceState;
import net.dries007.tfc.world.surface.SurfaceStates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TFCReplaceFeature implements ReplaceFeature {
    private static final TagKey<ConfiguredFeature<?, ?>> FOREST_TREES_TAG = TagKey.create(
            Registries.CONFIGURED_FEATURE,
            ResourceLocation.fromNamespaceAndPath("tfc", "forest_trees")
    );

    private final Map<ResourceLocation, String> replacementMap;

    private final Map<Block, Wood.BlockType> blockToWoodBlockTypeMap;
    private final List<Pair<TagKey<Block>, Wood.BlockType>> tagToWoodBlockTypeMappings;
    private final List<Pair<Block, SoilBlockType>> blockToSoilBlockTypeMappings;
    private final List<Pair<TagKey<Block>, Ore>> tagToOreMappings;

    private boolean tfcGeneratorAvailable;
    private Registry<Block> blockRegistry;
    private DummySurfaceBuilderContext surfaceBuilderContext;
    private RockSettings cachedSurfaceRock;
    private Wood cachedWood;

    public TFCReplaceFeature(Map<ResourceLocation, String> replacementMap) {
        this.replacementMap = replacementMap;

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
                new Pair<>(Blocks.COARSE_DIRT, SoilBlockType.MUD),
                new Pair<>(Blocks.GRASS_BLOCK, SoilBlockType.GRASS),
                new Pair<>(Blocks.DIRT_PATH, SoilBlockType.GRASS_PATH),
                new Pair<>(Blocks.FARMLAND, SoilBlockType.FARMLAND),
                new Pair<>(Blocks.ROOTED_DIRT, SoilBlockType.ROOTED_DIRT),
                new Pair<>(Blocks.MUD, SoilBlockType.MUD),
                new Pair<>(Blocks.MUD_BRICKS, SoilBlockType.MUD_BRICKS),
                new Pair<>(Blocks.MUDDY_MANGROVE_ROOTS, SoilBlockType.MUDDY_ROOTS)
        );

        tagToOreMappings = List.of(
                new Pair<>(BlockTags.GOLD_ORES, Ore.NATIVE_GOLD),
                new Pair<>(BlockTags.IRON_ORES, Ore.MAGNETITE),
                new Pair<>(BlockTags.DIAMOND_ORES, Ore.GRAPHITE),
                new Pair<>(BlockTags.REDSTONE_ORES, Ore.CINNABAR),
                new Pair<>(BlockTags.LAPIS_ORES, Ore.LAPIS_LAZULI),
                new Pair<>(BlockTags.COAL_ORES, Ore.BITUMINOUS_COAL),
                new Pair<>(BlockTags.EMERALD_ORES, Ore.EMERALD),
                new Pair<>(BlockTags.COPPER_ORES, Ore.NATIVE_COPPER)
        );
    }

    @Override
    public void prepareData(WorldGenLevel level, RandomSource random, ChunkPos rootChunkPos, BoundingBox box, ChunkPos chunkPos) {
        var chunkGenerator = level.getLevel().getChunkSource().getGenerator();
        tfcGeneratorAvailable = chunkGenerator instanceof TFCChunkGenerator;
        if (!tfcGeneratorAvailable) {
            TFCStructuresMod.LOGGER.warn("Cannot use TFCReplaceFeature due TFCChunkGenerator is not in use");
            return;
        }

        blockRegistry = level.registryAccess().registryOrThrow(Registries.BLOCK);

        int x = rootChunkPos.getMiddleBlockX();
        int z = rootChunkPos.getMiddleBlockZ();
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
        BlockPos rootChunkSurface = new BlockPos(x, y, z);

        ChunkDataProvider provider = ChunkDataProvider.get(level);
        ChunkData chunkData = provider.get(level, rootChunkPos);
        RockData cachedRockData = chunkData.getRockData();
        cachedSurfaceRock = cachedRockData.getSurfaceRock(x, z);

        Wood wood = null;
        int woodIndex = random.nextInt(Integer.MAX_VALUE);
        var forestEntries = getForestEntry(level, chunkData, rootChunkSurface, random);
        if (forestEntries == null || forestEntries.isEmpty()) {
            TFCStructuresMod.LOGGER.warn("Can't detect ForestEntry");
        } else {
            var forestEntry = forestEntries.get(woodIndex % forestEntries.size());
            wood = getWoodFromConfigEntry(forestEntry);
        }

        if (wood == null) {
            TFCStructuresMod.LOGGER.warn("Wood was not detected, will be used random wood");
            wood = Wood.VALUES[woodIndex % Wood.VALUES.length];
        }

        cachedWood = wood;
    }

    @Override
    public @Nullable Block replaceBlock(WorldGenLevel level, BlockPos pos, BlockState originalState, ResourceLocation originalLocation) {
        if (!tfcGeneratorAvailable) {
            return null;
        }

        String replacementType = replacementMap.get(originalLocation);
        if (replacementType == null) {
            return null;
        }

        return switch (replacementType) {
            case ReplacementPreset.TFC_SKIP_TYPE -> null;
            case ReplacementPreset.TFC_STONE_TYPE ->
                    replaceTFCStone(originalState, originalState.is(TFCStructuresMod.MOSSY_TAG)
                            ? Rock.BlockType.MOSSY_COBBLE
                            : Rock.BlockType.SMOOTH);
            case ReplacementPreset.TFC_BRICK_TYPE ->
                    replaceTFCStone(originalState, originalState.is(TFCStructuresMod.MOSSY_TAG)
                            ? Rock.BlockType.MOSSY_BRICKS
                            : Rock.BlockType.BRICKS);
            case ReplacementPreset.TFC_WOOD_TYPE -> replaceTFCWood(originalState);
            case ReplacementPreset.TFC_SOIL_TYPE -> replaceTFCSoil(level, pos, originalState);
            case ReplacementPreset.TFC_SAND_TYPE -> replaceTFCSand(level, pos, originalState);
            case ReplacementPreset.TFC_ORE_TYPE -> replaceTFCOre(originalState);
            default -> throw new RuntimeException("Type " + replacementType + " is not supported");
        };
    }

    private Block replaceTFCStone(BlockState original, Rock.BlockType blockType) {
        var rock = getRockFromRockSettings(cachedSurfaceRock);
        if (rock == null) {
            TFCStructuresMod.LOGGER.warn("Rock was not detected, so it will be hardened one");
            return cachedSurfaceRock.hardened();
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
        } else if (original.is(TFCStructuresMod.CRACKED_BRICKS_TAG)) {
            replacement = rock.getBlock(Rock.BlockType.CRACKED_BRICKS).get();
        } else {
            replacement = rock.getBlock(blockType).get();
        }

        return replacement;
    }

    private Block replaceTFCWood(BlockState original) {
        Wood.BlockType blockType = null;
        Block originalBlock = original.getBlock();
        var possibleReplacement = blockToWoodBlockTypeMap.get(originalBlock);
        if (possibleReplacement != null) {
            blockType = possibleReplacement;
        } else {
            for (var mapping : tagToWoodBlockTypeMappings) {
                if (original.is(mapping.first())) {
                    blockType = mapping.second();
                    break;
                }
            }
        }

        if (blockType == null) {
            var location = blockRegistry.getKey(original.getBlock());
            TFCStructuresMod.LOGGER.warn("Wood block type of {} was not detected, will be used common WOOD", location);
            blockType = Wood.BlockType.WOOD;
        }

        return cachedWood.getBlock(blockType).get();
    }

    private Block replaceTFCSoil(WorldGenLevel level, BlockPos pos, BlockState original) {
        SoilBlockType blockType = null;
        for (var mapping : blockToSoilBlockTypeMappings) {
            if (original.is(mapping.first())) {
                blockType = mapping.second();
                break;
            }
        }

        if (blockType == null) {
            var location = blockRegistry.getKey(original.getBlock());
            TFCStructuresMod.LOGGER.warn("Soil block type of {} was not detected, will be used common DIRT", location);
            blockType = SoilBlockType.DIRT;
        }

        SurfaceState surfaceState = SoilSurfaceState.buildType(blockType);
        SurfaceBuilderContext context = buildSoilContext(level, pos);
        return surfaceState.getState(context).getBlock();
    }

    private Block replaceTFCSand(WorldGenLevel level, BlockPos pos, BlockState original) {
        boolean isSandstoneBlock = original.is(Tags.Blocks.SANDSTONE);
        boolean isStair = original.is(BlockTags.STAIRS);
        boolean isSlab = original.is(BlockTags.SLABS);
        boolean isWall = original.is(BlockTags.WALLS);
        boolean isCommonSandstone = isSandstoneBlock || isStair || isSlab || isWall;

        var context = buildSoilContext(level, pos);
        Block sandBlock = SurfaceStates.SHORE_SAND.getState(context).getBlock();
        if (!isCommonSandstone) {
            if (original.is(Tags.Blocks.GRAVEL)) {
                return SurfaceStates.GRAVEL.getState(context).getBlock();
            } else {
                return sandBlock;
            }
        }

        var sandBlockType = genSandBlockType(sandBlock);
        if (sandBlockType == null) {
            var location = blockRegistry.getKey(original.getBlock());
            TFCStructuresMod.LOGGER.warn("SandBlockType of {} was not detected, can't replace block", location);
            return null;
        }

        Block originalBlock = original.getBlock();
        var sandstoneBlockType = getSandstoneBlockType(originalBlock);
        if (sandstoneBlockType == null) {
            var location = blockRegistry.getKey(original.getBlock());
            TFCStructuresMod.LOGGER.warn("SandStoneBlockType of {} was not detected, can't replace block", location);
            return null;
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

        return replacement;
    }

    private @Nullable Block replaceTFCOre(BlockState originalState) {
        var rock = getRockFromRockSettings(cachedSurfaceRock);
        if (rock == null) {
            var location = blockRegistry.getKey(originalState.getBlock());
            TFCStructuresMod.LOGGER.warn("Rock was not detected, can't replace block {}", location);
            return null;
        }

        Ore ore = null;
        for (Pair<TagKey<Block>, Ore> mapping : tagToOreMappings) {
            if (originalState.is(mapping.first())) {
                ore = mapping.second();
            }
        }

        if (ore == null) {
            var location = blockRegistry.getKey(originalState.getBlock());
            TFCStructuresMod.LOGGER.warn("Ore type was not detected, can't replace block {}", location);
            return null;
        }

        if (ore.isGraded()) {
            return TFCBlocks.GRADED_ORES.get(rock).get(ore).get(Ore.Grade.NORMAL).get();
        } else {
            return TFCBlocks.ORES.get(rock).get(ore).get();
        }
    }

    private static Rock getRockFromRockSettings(RockSettings rockSettings) {
        var block = rockSettings.raw();
        for (Rock rock : Rock.VALUES) {
            Block rockBlock = rock.getBlock(Rock.BlockType.RAW).get();
            if (block.equals(rockBlock)) {
                return rock;
            }
        }

        return null;
    }

    private static List<ForestConfig.Entry> getForestEntry(WorldGenLevel level, ChunkData chunkData, BlockPos pos, RandomSource random) {
        Biome biome = level.getBiome(pos).get();
        BiomeExtension biomeExtension = TFCBiomes.getExtensionOrThrow(level, biome);
        Set<PlacedFeature> featureSet = biomeExtension.getFlattenedFeatureSet(biome);

        for (PlacedFeature placedFeature : featureSet) {
            var configuredFeature = placedFeature.feature().value();
            if (configuredFeature.feature() instanceof ForestFeature forestFeature) {
                var accessor = (ForestFeatureAccessorMixin) forestFeature;
                var config = (ForestConfig) configuredFeature.config();
                var tree = accessor.invokeGetTree(chunkData, random.fork(), config, pos);
                if (tree != null) {
                    return List.of(tree);
                }
            }
        }

        List<Holder<ConfiguredFeature<?, ?>>> features = new ArrayList<>();
        var featureRegistry = level.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
        for (Holder<ConfiguredFeature<?, ?>> holder : featureRegistry.getTagOrEmpty(FOREST_TREES_TAG)) {
            features.add(holder);
        }

        if (features.isEmpty()) {
            return null;
        }

        float rainfall = chunkData.getRainfall(pos);
        float averageTemperature = OverworldClimateModel.getAdjustedAverageTempByElevation(pos, chunkData);

        var entries = new ArrayList<ForestConfig.Entry>();
        for (var entryHolder : features) {
            var entry = (ForestConfig.Entry) entryHolder.value().config();
            if (entry.isValid(averageTemperature, rainfall)) {
                entries.add(entry);
            }
        }

        return entries;
    }

    private static Wood getWoodFromConfigEntry(ForestConfig.Entry forestEntry) {
        var configuredFeature = forestEntry.treeFeature().get();
        FeatureConfiguration config = configuredFeature.config();

        String path;
        if (config instanceof RandomTreeConfig treeConfig) {
            path = treeConfig.structureNames().get(0).getPath();
        } else if (config instanceof OverlayTreeConfig treeConfig) {
            path = treeConfig.base().getPath();
        } else if (config instanceof StackedTreeConfig treeConfig) {
            path = treeConfig.layers().get(0).templates().get(0).getPath();
        } else {
            return null;
        }

        for (Wood wood : Wood.VALUES) {
            if (path.startsWith(wood.getSerializedName())) {
                return wood;
            }
        }

        return null;
    }

    private SurfaceBuilderContext buildSoilContext(WorldGenLevel level, BlockPos pos) {
        ChunkData chunkData = ChunkDataProvider.get(level).get(level, pos);
        if (surfaceBuilderContext == null) {
            var chunkGenerator = (TFCChunkGenerator) level.getLevel().getChunkSource().getGenerator();
            var rockLayerSettings = chunkGenerator.rockLayerSettings();
            surfaceBuilderContext = new DummySurfaceBuilderContext(chunkData, rockLayerSettings);
        }

        var accessor = surfaceBuilderContext.getAccessor();
        accessor.setRockData(chunkData.getRockData());
        accessor.setRainfall(chunkData.getRainfall(pos));
        accessor.setCursor(pos.mutable());

        return surfaceBuilderContext;
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
}
