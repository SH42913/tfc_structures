package com.farco.tfc_structures.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

public record ReplacementConfig(List<Direct> directReplacements, List<TFCWorld> tfcWorldReplacement) {
    public static final String CONFIG_NAME = "replacement_config.json";
    public static final String TFC_STONE_TYPE = "STONE";
    public static final String TFC_BRICK_TYPE = "BRICK";
    public static final String TFC_WOOD_TYPE = "WOOD";
    public static final String TFC_SKIP_TYPE = "SKIP";

    private record Direct(String original, String replacement) {
    }

    private record TFCWorld(String original, String type) {
    }

    public Map<ResourceLocation, ResourceLocation> getDirectReplacementMap() {
        var map = new HashMap<ResourceLocation, ResourceLocation>(directReplacements.size());
        for (Direct entry : directReplacements) {
            var originalLocation = ResourceLocation.parse(entry.original);
            var replacementLocation = ResourceLocation.parse(entry.replacement);
            map.put(originalLocation, replacementLocation);
        }

        return map;
    }

    public Map<ResourceLocation, String> getTfcWorldReplacementMap() {
        var map = new HashMap<ResourceLocation, String>(tfcWorldReplacement.size());
        for (TFCWorld entry : tfcWorldReplacement) {
            var originalLocation = ResourceLocation.parse(entry.original);
            map.put(originalLocation, entry.type);
        }

        return map;
    }

    public static ReplacementConfig getDefaultConfig() {
        return new ReplacementConfig(getDefaultDirect(), getDefaultTFCWorld());
    }

    private static @NotNull List<Direct> getDefaultDirect() {
        return List.of(
                new Direct("minecraft:campfire", "tfc:firepit"),
                new Direct("minecraft:anvil", "tfc:metal/anvil/bismuth_bronze"),
                new Direct("minecraft:chipped_anvil", "tfc:metal/anvil/bronze"),
                new Direct("minecraft:damaged_anvil", "tfc:metal/anvil/copper")
        );
    }

    private static @NotNull List<TFCWorld> getDefaultTFCWorld() {
        var ignoreNames = List.of("nether", "prismarine", "end");
        var stoneNames = List.of("stone");
        var brickNames = List.of("brick");
        var woodNames = WoodType.values().map(WoodType::name).toList();
        var woodBlocksSet = Set.of(Blocks.CRAFTING_TABLE, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.LECTERN, Blocks.BOOKSHELF);

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
            } else if (brickNames.stream().anyMatch(predicate)) {
                conversionType = TFC_BRICK_TYPE;
            } else if (stoneNames.stream().anyMatch(predicate)) {
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
