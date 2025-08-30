package com.farco.tfc_structures.config;

import com.farco.tfc_structures.TFCStructuresMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

public final class PresetContainer {
    public static final String NO_REPLACE_PRESET_NAME = "no-replace";
    public static final String DEFAULT_OVERWORLD_PRESET_NAME = "overworld-common";
    private static final String FOLDER_NAME = "presets";

    private final Path presetsFolderPath;
    private final JsonConfigProvider configProvider;
    private final HashMap<String, ReplacementPreset> presets;

    public PresetContainer(Path configFolderPath) {
        this.presets = new HashMap<>();

        presetsFolderPath = configFolderPath.resolve(FOLDER_NAME);
        configProvider = new JsonConfigProvider(presetsFolderPath);
    }

    public void loadPresets() {
        if (!Files.exists(presetsFolderPath)) {
            createDefaultPresets();
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(presetsFolderPath)) {
            for (Path path : stream) {
                if (!Files.isRegularFile(path)) {
                    continue;
                }

                var fileNameWithExt = path.getFileName().toString();
                ReplacementPreset preset = configProvider.load(fileNameWithExt, ReplacementPreset.CODEC, () -> null);
                if (preset == null) {
                    TFCStructuresMod.LOGGER.error("Can't load preset {}", fileNameWithExt);
                    continue;
                }

                var lastDotIndex = fileNameWithExt.lastIndexOf('.');
                var fileName = fileNameWithExt.substring(0, lastDotIndex);
                presets.put(fileName, preset);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ReplacementPreset getPresetByName(String name) {
        return presets.get(name);
    }

    private void createDefaultPresets() {
        try {
            Files.createDirectories(presetsFolderPath);

            var emptyPreset = new ReplacementPreset(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
            configProvider.save(NO_REPLACE_PRESET_NAME + ".json", emptyPreset, ReplacementPreset.CODEC);

            var defaultOverworldPreset = getDefaultOverworldPreset();
            configProvider.save(DEFAULT_OVERWORLD_PRESET_NAME + ".json", defaultOverworldPreset, ReplacementPreset.CODEC);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ReplacementPreset getDefaultOverworldPreset() {
        return new ReplacementPreset(getDefaultDirect(), getDefaultRandom(), getDefaultTFCWorld());
    }

    private static @NotNull List<ReplacementPreset.Direct> getDefaultDirect() {
        List<ReplacementPreset.Direct> list = new ArrayList<>();
        list.add(new ReplacementPreset.Direct("minecraft:iron_block", "tfc:metal/block/wrought_iron"));
        list.add(new ReplacementPreset.Direct("minecraft:gold_block", "tfc:metal/block/gold"));
        list.add(new ReplacementPreset.Direct("minecraft:cake", "tfc:cake"));
        list.add(new ReplacementPreset.Direct("minecraft:pumpkin", "tfc:pumpkin"));
        list.add(new ReplacementPreset.Direct("minecraft:pumpkin_stem", "tfc:crop/pumpkin"));
        list.add(new ReplacementPreset.Direct("minecraft:melon", "tfc:melon"));
        list.add(new ReplacementPreset.Direct("minecraft:melon_stem", "tfc:crop/melon"));
        list.add(new ReplacementPreset.Direct("minecraft:hay_block", "tfc:thatch"));
        list.add(new ReplacementPreset.Direct("minecraft:dandelion", "tfc:plant/dandelion"));
        list.add(new ReplacementPreset.Direct("minecraft:poppy", "tfc:plant/poppy"));
        list.add(new ReplacementPreset.Direct("minecraft:blue_orchid", "tfc:plant/blue_orchid"));
        list.add(new ReplacementPreset.Direct("minecraft:allium", "tfc:plant/allium"));
        list.add(new ReplacementPreset.Direct("minecraft:azure_bluet", "tfc:plant/houstonia"));
        list.add(new ReplacementPreset.Direct("minecraft:red_tulip", "tfc:plant/tulip_red"));
        list.add(new ReplacementPreset.Direct("minecraft:orange_tulip", "tfc:plant/tulip_orange"));
        list.add(new ReplacementPreset.Direct("minecraft:white_tulip", "tfc:plant/tulip_white"));
        list.add(new ReplacementPreset.Direct("minecraft:pink_tulip", "tfc:plant/tulip_pink"));
        list.add(new ReplacementPreset.Direct("minecraft:oxeye_daisy", "tfc:plant/oxeye_daisy"));
        list.add(new ReplacementPreset.Direct("minecraft:cornflower", "tfc:plant/pickerelweed"));
        list.add(new ReplacementPreset.Direct("minecraft:lily_of_the_valley", "tfc:plant/lily_of_the_valley"));
        list.add(new ReplacementPreset.Direct("minecraft:pink_petals", "tfc:plant/maiden_pink"));
        list.add(new ReplacementPreset.Direct("minecraft:sunflower", "tfc:plant/calendula"));
        list.add(new ReplacementPreset.Direct("minecraft:lilac", "tfc:plant/lilac"));
        list.add(new ReplacementPreset.Direct("minecraft:rose_bush", "tfc:plant/rose"));
        list.add(new ReplacementPreset.Direct("minecraft:peony", "tfc:plant/pulsatilla"));
        list.add(new ReplacementPreset.Direct("minecraft:grass", "tfc:plant/bromegrass"));
        list.add(new ReplacementPreset.Direct("minecraft:fern", "tfc:plant/lady_fern"));
        list.add(new ReplacementPreset.Direct("minecraft:dead_bush", "tfc:plant/dead_bush"));
        list.add(new ReplacementPreset.Direct("minecraft:vine", "tfc:plant/jungle_vines"));
        list.add(new ReplacementPreset.Direct("minecraft:tall_grass", "tfc:plant/tall_fescue_grass"));
        list.add(new ReplacementPreset.Direct("minecraft:large_fern", "tfc:plant/ostrich_fern"));
        list.add(new ReplacementPreset.Direct("minecraft:seagrass", "tfc:plant/turtle_grass"));
        list.add(new ReplacementPreset.Direct("minecraft:sea_pickle", "tfc:sea_pickle"));
        list.add(new ReplacementPreset.Direct("minecraft:kelp", "tfc:plant/leafy_kelp"));
        list.add(new ReplacementPreset.Direct("minecraft:granite", "tfc:rock/hardened/granite"));
        list.add(new ReplacementPreset.Direct("minecraft:granite_stairs", "tfc:rock/raw/granite_stairs"));
        list.add(new ReplacementPreset.Direct("minecraft:granite_slab", "tfc:rock/raw/granite_slab"));
        list.add(new ReplacementPreset.Direct("minecraft:granite_wall", "tfc:rock/raw/granite_wall"));
        list.add(new ReplacementPreset.Direct("minecraft:polished_granite", "tfc:rock/smooth/granite"));
        list.add(new ReplacementPreset.Direct("minecraft:polished_granite_stairs", "tfc:rock/smooth/granite_stairs"));
        list.add(new ReplacementPreset.Direct("minecraft:polished_granite_slab", "tfc:rock/smooth/granite_slab"));
        list.add(new ReplacementPreset.Direct("minecraft:diorite", "tfc:rock/hardened/diorite"));
        list.add(new ReplacementPreset.Direct("minecraft:diorite_stairs", "tfc:rock/raw/diorite_stairs"));
        list.add(new ReplacementPreset.Direct("minecraft:diorite_slab", "tfc:rock/raw/diorite_slab"));
        list.add(new ReplacementPreset.Direct("minecraft:diorite_wall", "tfc:rock/raw/diorite_wall"));
        list.add(new ReplacementPreset.Direct("minecraft:polished_diorite", "tfc:rock/smooth/diorite"));
        list.add(new ReplacementPreset.Direct("minecraft:polished_diorite_stairs", "tfc:rock/smooth/diorite_stairs"));
        list.add(new ReplacementPreset.Direct("minecraft:polished_diorite_slab", "tfc:rock/smooth/diorite_slab"));
        list.add(new ReplacementPreset.Direct("minecraft:andesite", "tfc:rock/hardened/andesite"));
        list.add(new ReplacementPreset.Direct("minecraft:andesite_stairs", "tfc:rock/raw/andesite_stairs"));
        list.add(new ReplacementPreset.Direct("minecraft:andesite_slab", "tfc:rock/raw/andesite_slab"));
        list.add(new ReplacementPreset.Direct("minecraft:andesite_wall", "tfc:rock/raw/andesite_wall"));
        list.add(new ReplacementPreset.Direct("minecraft:polished_andesite", "tfc:rock/smooth/andesite"));
        list.add(new ReplacementPreset.Direct("minecraft:polished_andesite_stairs", "tfc:rock/smooth/andesite_stairs"));
        list.add(new ReplacementPreset.Direct("minecraft:polished_andesite_slab", "tfc:rock/smooth/andesite_slab"));
        list.add(new ReplacementPreset.Direct("minecraft:blackstone", "tfc:rock/hardened/basalt"));
        list.add(new ReplacementPreset.Direct("minecraft:blackstone_stairs", "tfc:rock/raw/basalt_stairs"));
        list.add(new ReplacementPreset.Direct("minecraft:blackstone_slab", "tfc:rock/raw/basalt_slab"));
        list.add(new ReplacementPreset.Direct("minecraft:blackstone_wall", "tfc:rock/raw/basalt_wall"));
        list.add(new ReplacementPreset.Direct("minecraft:polished_blackstone", "tfc:rock/smooth/basalt"));
        list.add(new ReplacementPreset.Direct("minecraft:polished_blackstone_stairs", "tfc:rock/smooth/basalt_stairs"));
        list.add(new ReplacementPreset.Direct("minecraft:polished_blackstone_slab", "tfc:rock/smooth/basalt_slab"));
        list.add(new ReplacementPreset.Direct("minecraft:polished_blackstone_wall", "tfc:rock/smooth/basalt_wall"));
        list.add(new ReplacementPreset.Direct("minecraft:chiseled_polished_blackstone", "tfc:rock/chiseled/basalt"));
        list.add(new ReplacementPreset.Direct("minecraft:polished_blackstone_bricks", "tfc:rock/smooth/basalt"));
        list.add(new ReplacementPreset.Direct("minecraft:polished_blackstone_brick_stairs", "tfc:rock/smooth/basalt_stairs"));
        list.add(new ReplacementPreset.Direct("minecraft:polished_blackstone_brick_slab", "tfc:rock/smooth/basalt_slab"));
        list.add(new ReplacementPreset.Direct("minecraft:polished_blackstone_brick_wall", "tfc:rock/smooth/basalt_wall"));
        list.add(new ReplacementPreset.Direct("minecraft:raw_iron_block", "tfc:metal/block/wrought_iron"));
        list.add(new ReplacementPreset.Direct("minecraft:raw_copper_block", "minecraft:copper_block"));
        list.add(new ReplacementPreset.Direct("minecraft:raw_gold_block", "tfc:metal/block/gold"));
        list.add(new ReplacementPreset.Direct("minecraft:packed_mud", "tfc:smooth_mud_bricks"));
        list.add(new ReplacementPreset.Direct("minecraft:mud_bricks", "tfc:mud_bricks/sandy_loam"));
        list.add(new ReplacementPreset.Direct("minecraft:mud_brick_stairs", "tfc:mud_bricks/sandy_loam_stairs"));
        list.add(new ReplacementPreset.Direct("minecraft:mud_brick_slab", "tfc:mud_bricks/sandy_loam_slab"));
        list.add(new ReplacementPreset.Direct("minecraft:mud_brick_wall", "tfc:mud_bricks/sandy_loam_wall"));
        list.add(new ReplacementPreset.Direct("minecraft:sugar_cane", "tfc:crop/sugarcane"));
        list.add(new ReplacementPreset.Direct("minecraft:copper_block", "tfc:metal/block/copper"));
        return list;
    }

    private static @NotNull List<ReplacementPreset.Random> getDefaultRandom() {
        List<String> anvils = List.of("tfc:metal/anvil/copper", "tfc:metal/anvil/bismuth_bronze", "tfc:metal/anvil/black_bronze", "tfc:metal/anvil/bronze");
        return List.of(
                new ReplacementPreset.Random("minecraft:chain", false, List.of(
                        "tfc:metal/chain/copper",
                        "tfc:metal/chain/bismuth_bronze",
                        "tfc:metal/chain/black_bronze",
                        "tfc:metal/chain/bronze",
                        "tfc:metal/chain/wrought_iron"
                )),
                new ReplacementPreset.Random("minecraft:iron_bars", false, List.of(
                        "tfc:metal/bars/copper",
                        "tfc:metal/bars/bismuth_bronze",
                        "tfc:metal/bars/black_bronze",
                        "tfc:metal/bars/bronze",
                        "tfc:metal/bars/wrought_iron"
                )),
                new ReplacementPreset.Random("minecraft:iron_trapdoor", false, List.of(
                        "tfc:metal/trapdoor/copper",
                        "tfc:metal/trapdoor/bismuth_bronze",
                        "tfc:metal/trapdoor/black_bronze",
                        "tfc:metal/trapdoor/bronze",
                        "tfc:metal/trapdoor/wrought_iron"
                )),
                new ReplacementPreset.Random("minecraft:anvil", true, anvils),
                new ReplacementPreset.Random("minecraft:chipped_anvil", true, anvils),
                new ReplacementPreset.Random("minecraft:damaged_anvil", true, anvils),
                new ReplacementPreset.Random("minecraft:wheat", true, List.of(
                        "tfc:crop/barley",
                        "tfc:crop/oat",
                        "tfc:crop/rye",
                        "tfc:crop/maize",
                        "tfc:crop/wheat",
                        "tfc:crop/rice"
                )),
                new ReplacementPreset.Random("minecraft:beetroots", true, List.of(
                        "tfc:crop/beet",
                        "tfc:crop/cabbage",
                        "tfc:crop/green_bean",
                        "tfc:crop/soybean",
                        "tfc:crop/jute",
                        "tfc:crop/papyrus"
                )),
                new ReplacementPreset.Random("minecraft:potatoes", true, List.of(
                        "tfc:crop/potato",
                        "tfc:crop/onion",
                        "tfc:crop/garlic",
                        "tfc:crop/pumpkin",
                        "tfc:crop/melon",
                        "tfc:crop/squash",
                        "tfc:crop/sugarcane"
                )),
                new ReplacementPreset.Random("minecraft:carrots", true, List.of(
                        "tfc:crop/carrot",
                        "tfc:crop/tomato",
                        "tfc:crop/yellow_bell_pepper",
                        "tfc:crop/red_bell_pepper"
                )),
                new ReplacementPreset.Random("minecraft:magma_block", true, List.of(
                        "tfc:rock/magma/granite",
                        "tfc:rock/magma/diorite",
                        "tfc:rock/magma/gabbro",
                        "tfc:rock/magma/rhyolite",
                        "tfc:rock/magma/basalt",
                        "tfc:rock/magma/andesite",
                        "tfc:rock/magma/dacite"
                ))
        );
    }

    private static @NotNull List<ReplacementPreset.TFCWorld> getDefaultTFCWorld() {
        var ignoreNames = List.of("nether", "prismarine", "end", "infested", "redstone", "blackstone", "dripstone", "soul", "suspicious");
        var stoneNames = List.of("stone");
        var stoneFunctional = Set.of(Blocks.STONECUTTER, Blocks.GRINDSTONE, Blocks.LODESTONE, Blocks.GLOWSTONE);
        var brickNames = List.of("brick");
        var sandNames = List.of("sand", "gravel");
        var oreNames = List.of("ore");
        var woodNames = WoodType.values().map(WoodType::name).toList();
        var woodBlocksSet = Set.of(Blocks.CRAFTING_TABLE, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.BOOKSHELF);
        var soilBlockSet = Set.of(Blocks.GRASS_BLOCK, Blocks.DIRT_PATH, Blocks.DIRT, Blocks.FARMLAND, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.ROOTED_DIRT, Blocks.MUD, Blocks.MUDDY_MANGROVE_ROOTS);

        var list = new ArrayList<ReplacementPreset.TFCWorld>();
        for (var entry : ForgeRegistries.BLOCKS.getEntries()) {
            ResourceLocation location = entry.getKey().location();
            if (!location.getNamespace().equals("minecraft")) {
                continue;
            }

            String name = location.getPath();
            Predicate<String> predicate = name::contains;
            if (ignoreNames.stream().anyMatch(predicate)) {
                continue;
            }

            String conversionType;
            Block block = entry.getValue();
            if (woodBlocksSet.contains(block) || woodNames.stream().anyMatch(predicate)) {
                conversionType = ReplacementPreset.TFC_WOOD_TYPE;
            } else if (soilBlockSet.contains(block)) {
                conversionType = ReplacementPreset.TFC_SOIL_TYPE;
            } else if (brickNames.stream().anyMatch(predicate)) {
                conversionType = ReplacementPreset.TFC_BRICK_TYPE;
            } else if (sandNames.stream().anyMatch(predicate)) {
                conversionType = ReplacementPreset.TFC_SAND_TYPE;
            } else if (oreNames.stream().anyMatch(predicate)) {
                conversionType = ReplacementPreset.TFC_ORE_TYPE;
            } else if (!stoneFunctional.contains(block) && stoneNames.stream().anyMatch(predicate)) {
                conversionType = ReplacementPreset.TFC_STONE_TYPE;
            } else {
                continue;
            }

            list.add(new ReplacementPreset.TFCWorld(location, conversionType));
        }

        list.sort(Comparator.comparing(ReplacementPreset.TFCWorld::original));
        return list;
    }
}
