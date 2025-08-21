package com.farco.tfc_structures.config;

import com.farco.tfc_structures.TFCStructuresMod;
import com.farco.tfc_structures.utils.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

public record ReplacementConfig(List<Direct> directReplacements,
                                List<Random> randomReplacements,
                                List<TFCWorld> tfcWorldReplacements) {
    public static final String CONFIG_NAME = "replacement_config.json";
    public static final String TFC_STONE_TYPE = "STONE";
    public static final String TFC_BRICK_TYPE = "BRICK";
    public static final String TFC_WOOD_TYPE = "WOOD";
    public static final String TFC_SOIL_TYPE = "SOIL";
    public static final String TFC_SAND_TYPE = "SAND";
    public static final String TFC_ORE_TYPE = "ORE";
    public static final String TFC_SKIP_TYPE = "SKIP";

    private record Direct(String original, String replacement) {
    }

    private record Random(String original, boolean perBlock, List<String> replacements) {
    }

    private record TFCWorld(String original, String type) {
    }

    public Map<ResourceLocation, ResourceLocation> getDirectReplacementMap() {
        IForgeRegistry<Block> blocks = ForgeRegistries.BLOCKS;
        var map = new HashMap<ResourceLocation, ResourceLocation>(directReplacements.size());
        for (Direct entry : directReplacements) {
            var originalLocation = ResourceLocation.parse(entry.original);
            if (!blocks.containsKey(originalLocation)) {
                TFCStructuresMod.LOGGER.error("Original with ID {} not found", entry.original);
                continue;
            }

            var replacementLocation = ResourceLocation.parse(entry.replacement);
            if (!blocks.containsKey(replacementLocation)) {
                TFCStructuresMod.LOGGER.error("Replacement with ID {} not found", entry.replacement);
                continue;
            }

            map.put(originalLocation, replacementLocation);
        }

        return map;
    }

    public Map<ResourceLocation, Pair<Boolean, List<ResourceLocation>>> getRandomReplacementMap() {
        IForgeRegistry<Block> blocks = ForgeRegistries.BLOCKS;
        var map = new HashMap<ResourceLocation, Pair<Boolean, List<ResourceLocation>>>(randomReplacements.size());
        for (Random entry : randomReplacements) {
            var originalLocation = ResourceLocation.parse(entry.original);
            if (!blocks.containsKey(originalLocation)) {
                TFCStructuresMod.LOGGER.error("Original for Random with ID {} not found", entry.original);
                continue;
            }

            List<ResourceLocation> replacements = new ArrayList<>(entry.replacements.size());
            for (String replacement : entry.replacements) {
                var replacementLocation = ResourceLocation.parse(replacement);
                if (!blocks.containsKey(replacementLocation)) {
                    TFCStructuresMod.LOGGER.error("Random replacement with ID {} not found", replacement);
                    continue;
                }

                replacements.add(replacementLocation);
            }

            if (replacements.isEmpty()) {
                TFCStructuresMod.LOGGER.error("There's no random replacements for {}", originalLocation);
                continue;
            }

            map.put(originalLocation, new Pair<>(entry.perBlock, replacements));
        }

        return map;
    }

    public Map<ResourceLocation, String> getTfcWorldReplacementMap() {
        IForgeRegistry<Block> blocks = ForgeRegistries.BLOCKS;
        var map = new HashMap<ResourceLocation, String>(tfcWorldReplacements.size());
        for (TFCWorld entry : tfcWorldReplacements) {
            var originalLocation = ResourceLocation.parse(entry.original);
            if (blocks.containsKey(originalLocation)) {
                map.put(originalLocation, entry.type);
            } else {
                TFCStructuresMod.LOGGER.error("Original for TFC replacement with ID {} not found", entry.original);
            }
        }

        return map;
    }

    public static ReplacementConfig getDefaultConfig() {
        return new ReplacementConfig(getDefaultDirect(), getDefaultRandom(), getDefaultTFCWorld());
    }

    private static @NotNull List<Direct> getDefaultDirect() {
        List<Direct> list = new ArrayList<>();
        list.add(new Direct("minecraft:iron_block", "tfc:metal/block/wrought_iron"));
        list.add(new Direct("minecraft:gold_block", "tfc:metal/block/gold"));
        list.add(new Direct("minecraft:cake", "tfc:cake"));
        list.add(new Direct("minecraft:pumpkin", "tfc:pumpkin"));
        list.add(new Direct("minecraft:pumpkin_stem", "tfc:crop/pumpkin"));
        list.add(new Direct("minecraft:melon", "tfc:melon"));
        list.add(new Direct("minecraft:melon_stem", "tfc:crop/melon"));
        list.add(new Direct("minecraft:hay_block", "tfc:thatch"));
        list.add(new Direct("minecraft:dandelion", "tfc:plant/dandelion"));
        list.add(new Direct("minecraft:poppy", "tfc:plant/poppy"));
        list.add(new Direct("minecraft:blue_orchid", "tfc:plant/blue_orchid"));
        list.add(new Direct("minecraft:allium", "tfc:plant/allium"));
        list.add(new Direct("minecraft:azure_bluet", "tfc:plant/houstonia"));
        list.add(new Direct("minecraft:red_tulip", "tfc:plant/tulip_red"));
        list.add(new Direct("minecraft:orange_tulip", "tfc:plant/tulip_orange"));
        list.add(new Direct("minecraft:white_tulip", "tfc:plant/tulip_white"));
        list.add(new Direct("minecraft:pink_tulip", "tfc:plant/tulip_pink"));
        list.add(new Direct("minecraft:oxeye_daisy", "tfc:plant/oxeye_daisy"));
        list.add(new Direct("minecraft:cornflower", "tfc:plant/pickerelweed"));
        list.add(new Direct("minecraft:lily_of_the_valley", "tfc:plant/lily_of_the_valley"));
        list.add(new Direct("minecraft:pink_petals", "tfc:plant/maiden_pink"));
        list.add(new Direct("minecraft:sunflower", "tfc:plant/calendula"));
        list.add(new Direct("minecraft:lilac", "tfc:plant/lilac"));
        list.add(new Direct("minecraft:rose_bush", "tfc:plant/rose"));
        list.add(new Direct("minecraft:peony", "tfc:plant/pulsatilla"));
        list.add(new Direct("minecraft:grass", "tfc:plant/bromegrass"));
        list.add(new Direct("minecraft:fern", "tfc:plant/lady_fern"));
        list.add(new Direct("minecraft:dead_bush", "tfc:plant/dead_bush"));
        list.add(new Direct("minecraft:vine", "tfc:plant/jungle_vines"));
        list.add(new Direct("minecraft:tall_grass", "tfc:plant/tall_fescue_grass"));
        list.add(new Direct("minecraft:large_fern", "tfc:plant/ostrich_fern"));
        list.add(new Direct("minecraft:seagrass", "tfc:plant/turtle_grass"));
        list.add(new Direct("minecraft:sea_pickle", "tfc:sea_pickle"));
        list.add(new Direct("minecraft:kelp", "tfc:plant/leafy_kelp"));
        list.add(new Direct("minecraft:granite", "tfc:rock/hardened/granite"));
        list.add(new Direct("minecraft:granite_stairs", "tfc:rock/raw/granite_stairs"));
        list.add(new Direct("minecraft:granite_slab", "tfc:rock/raw/granite_slab"));
        list.add(new Direct("minecraft:granite_wall", "tfc:rock/raw/granite_wall"));
        list.add(new Direct("minecraft:polished_granite", "tfc:rock/smooth/granite"));
        list.add(new Direct("minecraft:polished_granite_stairs", "tfc:rock/smooth/granite_stairs"));
        list.add(new Direct("minecraft:polished_granite_slab", "tfc:rock/smooth/granite_slab"));
        list.add(new Direct("minecraft:diorite", "tfc:rock/hardened/diorite"));
        list.add(new Direct("minecraft:diorite_stairs", "tfc:rock/raw/diorite_stairs"));
        list.add(new Direct("minecraft:diorite_slab", "tfc:rock/raw/diorite_slab"));
        list.add(new Direct("minecraft:diorite_wall", "tfc:rock/raw/diorite_wall"));
        list.add(new Direct("minecraft:polished_diorite", "tfc:rock/smooth/diorite"));
        list.add(new Direct("minecraft:polished_diorite_stairs", "tfc:rock/smooth/diorite_stairs"));
        list.add(new Direct("minecraft:polished_diorite_slab", "tfc:rock/smooth/diorite_slab"));
        list.add(new Direct("minecraft:andesite", "tfc:rock/hardened/andesite"));
        list.add(new Direct("minecraft:andesite_stairs", "tfc:rock/raw/andesite_stairs"));
        list.add(new Direct("minecraft:andesite_slab", "tfc:rock/raw/andesite_slab"));
        list.add(new Direct("minecraft:andesite_wall", "tfc:rock/raw/andesite_wall"));
        list.add(new Direct("minecraft:polished_andesite", "tfc:rock/smooth/andesite"));
        list.add(new Direct("minecraft:polished_andesite_stairs", "tfc:rock/smooth/andesite_stairs"));
        list.add(new Direct("minecraft:polished_andesite_slab", "tfc:rock/smooth/andesite_slab"));
        list.add(new Direct("minecraft:blackstone", "tfc:rock/hardened/basalt"));
        list.add(new Direct("minecraft:blackstone_stairs", "tfc:rock/raw/basalt_stairs"));
        list.add(new Direct("minecraft:blackstone_slab", "tfc:rock/raw/basalt_slab"));
        list.add(new Direct("minecraft:blackstone_wall", "tfc:rock/raw/basalt_wall"));
        list.add(new Direct("minecraft:polished_blackstone", "tfc:rock/smooth/basalt"));
        list.add(new Direct("minecraft:polished_blackstone_stairs", "tfc:rock/smooth/basalt_stairs"));
        list.add(new Direct("minecraft:polished_blackstone_slab", "tfc:rock/smooth/basalt_slab"));
        list.add(new Direct("minecraft:polished_blackstone_wall", "tfc:rock/smooth/basalt_wall"));
        list.add(new Direct("minecraft:chiseled_polished_blackstone", "tfc:rock/chiseled/basalt"));
        list.add(new Direct("minecraft:polished_blackstone_bricks", "tfc:rock/smooth/basalt"));
        list.add(new Direct("minecraft:polished_blackstone_brick_stairs", "tfc:rock/smooth/basalt_stairs"));
        list.add(new Direct("minecraft:polished_blackstone_brick_slab", "tfc:rock/smooth/basalt_slab"));
        list.add(new Direct("minecraft:polished_blackstone_brick_wall", "tfc:rock/smooth/basalt_wall"));
        list.add(new Direct("minecraft:raw_iron_block", "tfc:metal/block/wrought_iron"));
        list.add(new Direct("minecraft:raw_copper_block", "minecraft:copper_block"));
        list.add(new Direct("minecraft:raw_gold_block", "tfc:metal/block/gold"));
        list.add(new Direct("minecraft:packed_mud", "tfc:smooth_mud_bricks"));
        list.add(new Direct("minecraft:mud_bricks", "tfc:mud_bricks/sandy_loam"));
        list.add(new Direct("minecraft:mud_brick_stairs", "tfc:mud_bricks/sandy_loam_stairs"));
        list.add(new Direct("minecraft:mud_brick_slab", "tfc:mud_bricks/sandy_loam_slab"));
        list.add(new Direct("minecraft:mud_brick_wall", "tfc:mud_bricks/sandy_loam_wall"));
        return list;
    }

    private static @NotNull List<Random> getDefaultRandom() {
        List<String> anvils = List.of("tfc:metal/anvil/copper", "tfc:metal/anvil/bismuth_bronze", "tfc:metal/anvil/black_bronze", "tfc:metal/anvil/bronze");
        return List.of(
                new Random("minecraft:chain", false, List.of(
                        "tfc:metal/chain/copper",
                        "tfc:metal/chain/bismuth_bronze",
                        "tfc:metal/chain/black_bronze",
                        "tfc:metal/chain/bronze",
                        "tfc:metal/chain/wrought_iron"
                )),
                new Random("minecraft:iron_bars", false, List.of(
                        "tfc:metal/bars/copper",
                        "tfc:metal/bars/bismuth_bronze",
                        "tfc:metal/bars/black_bronze",
                        "tfc:metal/bars/bronze",
                        "tfc:metal/bars/wrought_iron"
                )),
                new Random("minecraft:iron_trapdoor", false, List.of(
                        "tfc:metal/trapdoor/copper",
                        "tfc:metal/trapdoor/bismuth_bronze",
                        "tfc:metal/trapdoor/black_bronze",
                        "tfc:metal/trapdoor/bronze",
                        "tfc:metal/trapdoor/wrought_iron"
                )),
                new Random("minecraft:anvil", true, anvils),
                new Random("minecraft:chipped_anvil", true, anvils),
                new Random("minecraft:damaged_anvil", true, anvils),
                new Random("minecraft:wheat", true, List.of(
                        "tfc:crop/barley",
                        "tfc:crop/oat",
                        "tfc:crop/rye",
                        "tfc:crop/maize",
                        "tfc:crop/wheat",
                        "tfc:crop/rice"
                )),
                new Random("minecraft:beetroots", true, List.of(
                        "tfc:crop/beet",
                        "tfc:crop/cabbage",
                        "tfc:crop/green_bean",
                        "tfc:crop/soybean",
                        "tfc:crop/jute",
                        "tfc:crop/papyrus"
                )),
                new Random("minecraft:potatoes", true, List.of(
                        "tfc:crop/potato",
                        "tfc:crop/onion",
                        "tfc:crop/garlic",
                        "tfc:crop/pumpkin",
                        "tfc:crop/melon",
                        "tfc:crop/squash",
                        "tfc:crop/sugarcane"
                )),
                new Random("minecraft:carrots", true, List.of(
                        "tfc:crop/carrot",
                        "tfc:crop/tomato",
                        "tfc:crop/yellow_bell_pepper",
                        "tfc:crop/red_bell_pepper"
                ))
        );
    }

    private static @NotNull List<TFCWorld> getDefaultTFCWorld() {
        var ignoreNames = List.of("nether", "prismarine", "end", "infested", "redstone", "blackstone", "dripstone", "soul", "suspicious");
        var stoneNames = List.of("stone");
        var stoneFunctional = Set.of(Blocks.STONECUTTER, Blocks.GRINDSTONE, Blocks.LODESTONE, Blocks.GLOWSTONE);
        var brickNames = List.of("brick");
        var sandNames = List.of("sand", "gravel");
        var oreNames = List.of("ore");
        var woodNames = WoodType.values().map(WoodType::name).toList();
        var woodBlocksSet = Set.of(Blocks.CRAFTING_TABLE, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.LECTERN, Blocks.BOOKSHELF);
        var soilBlockSet = Set.of(Blocks.GRASS_BLOCK, Blocks.DIRT_PATH, Blocks.DIRT, Blocks.FARMLAND, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.ROOTED_DIRT, Blocks.MUD, Blocks.MUDDY_MANGROVE_ROOTS);

        var list = new ArrayList<TFCWorld>();
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
                conversionType = TFC_WOOD_TYPE;
            } else if (soilBlockSet.contains(block)) {
                conversionType = TFC_SOIL_TYPE;
            } else if (brickNames.stream().anyMatch(predicate)) {
                conversionType = TFC_BRICK_TYPE;
            } else if (sandNames.stream().anyMatch(predicate)) {
                conversionType = TFC_SAND_TYPE;
            } else if (oreNames.stream().anyMatch(predicate)) {
                conversionType = TFC_ORE_TYPE;
            } else if (!stoneFunctional.contains(block) && stoneNames.stream().anyMatch(predicate)) {
                conversionType = TFC_STONE_TYPE;
            } else {
                continue;
            }

            list.add(new TFCWorld(location.toString(), conversionType));
        }

        list.sort(Comparator.comparing(o -> o.original));
        return list;
    }
}
