package com.farco.tfc_structures.config;

import com.farco.tfc_structures.TFCStructuresMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

public record ReplacementConfig(List<Direct> directReplacements, List<TFCWorld> tfcWorldReplacement) {
    public static final String CONFIG_NAME = "replacement_config.json";
    public static final String TFC_STONE_TYPE = "STONE";
    public static final String TFC_BRICK_TYPE = "BRICK";
    public static final String TFC_WOOD_TYPE = "WOOD";
    public static final String TFC_SOIL_TYPE = "SOIL";
    public static final String TFC_SAND_TYPE = "SAND";
    public static final String TFC_SKIP_TYPE = "SKIP";

    private record Direct(String original, String replacement) {
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

    public Map<ResourceLocation, String> getTfcWorldReplacementMap() {
        IForgeRegistry<Block> blocks = ForgeRegistries.BLOCKS;
        var map = new HashMap<ResourceLocation, String>(tfcWorldReplacement.size());
        for (TFCWorld entry : tfcWorldReplacement) {
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
        return new ReplacementConfig(getDefaultDirect(), getDefaultTFCWorld());
    }

    private static @NotNull List<Direct> getDefaultDirect() {
        List<Direct> list = new ArrayList<>();
        list.add(new Direct("minecraft:campfire", "tfc:firepit"));
        list.add(new Direct("minecraft:anvil", "tfc:metal/anvil/bismuth_bronze"));
        list.add(new Direct("minecraft:chipped_anvil", "tfc:metal/anvil/bronze"));
        list.add(new Direct("minecraft:damaged_anvil", "tfc:metal/anvil/copper"));
        list.add(new Direct("minecraft:chain", "tfc:metal/chain/wrought_iron"));
        list.add(new Direct("minecraft:iron_bars", "tfc:metal/bars/wrought_iron"));
        list.add(new Direct("minecraft:iron_block", "tfc:metal/block/wrought_iron"));
        list.add(new Direct("minecraft:iron_trapdoor", "tfc:metal/trapdoor/wrought_iron"));
        list.add(new Direct("minecraft:gold_block", "tfc:metal/block/gold"));
        list.add(new Direct("minecraft:cake", "tfc:cake"));
        list.add(new Direct("minecraft:pumpkin", "tfc:pumpkin"));
        list.add(new Direct("minecraft:pumpkin_stem", "tfc:crop/pumpkin"));
        list.add(new Direct("minecraft:melon", "tfc:melon"));
        list.add(new Direct("minecraft:melon_stem", "tfc:crop/melon"));
        list.add(new Direct("minecraft:wheat", "tfc:crop/wheat"));
        list.add(new Direct("minecraft:hay_block", "tfc:thatch"));
        list.add(new Direct("minecraft:beetroots", "tfc:crop/beet"));
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
        return list;
    }

    private static @NotNull List<TFCWorld> getDefaultTFCWorld() {
        var ignoreNames = List.of("nether", "prismarine", "end", "infested", "redstone", "blackstone", "dripstone", "soul", "suspicious");
        var stoneNames = List.of("stone");
        var stoneFunctional = Set.of(Blocks.STONECUTTER, Blocks.GRINDSTONE, Blocks.LODESTONE, Blocks.GLOWSTONE);
        var brickNames = List.of("brick");
        var sandNames = List.of("sand", "gravel");
        var woodNames = WoodType.values().map(WoodType::name).toList();
        var woodBlocksSet = Set.of(Blocks.CRAFTING_TABLE, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.LECTERN, Blocks.BOOKSHELF);
        var soilBlockSet = Set.of(Blocks.GRASS_BLOCK, Blocks.DIRT_PATH, Blocks.DIRT, Blocks.FARMLAND, Blocks.COARSE_DIRT);

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
