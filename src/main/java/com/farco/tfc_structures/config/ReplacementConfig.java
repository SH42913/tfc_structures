package com.farco.tfc_structures.config;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ReplacementConfig(List<Direct> directReplacements, List<TFCWorld> tfcWorldReplacement) {
    public static final String CONFIG_NAME = "replacement_config.json";
    public static final String TFC_STONE_TYPE = "STONE";
    public static final String TFC_BRICK_TYPE = "BRICK";
    public static final String TFC_WOOD_TYPE = "WOOD";

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
        return List.of();
    }

    private static @NotNull List<TFCWorld> getDefaultTFCWorld() {
        return List.of(
                new TFCWorld("minecraft:cobblestone", TFC_BRICK_TYPE),
                new TFCWorld("minecraft:cobblestone_stairs", TFC_BRICK_TYPE),
                new TFCWorld("minecraft:cobblestone_wall", TFC_BRICK_TYPE),
                new TFCWorld("minecraft:cobblestone_slab", TFC_BRICK_TYPE),
                new TFCWorld("minecraft:mossy_cobblestone", TFC_BRICK_TYPE),
                new TFCWorld("minecraft:mossy_cobblestone_slab", TFC_BRICK_TYPE),
                new TFCWorld("minecraft:mossy_cobblestone_stairs", TFC_BRICK_TYPE),
                new TFCWorld("minecraft:mossy_cobblestone_wall", TFC_BRICK_TYPE),
                new TFCWorld("minecraft:mossy_stone_brick_slab", TFC_BRICK_TYPE),
                new TFCWorld("minecraft:mossy_stone_brick_stairs", TFC_BRICK_TYPE),
                new TFCWorld("minecraft:mossy_stone_brick_wall", TFC_BRICK_TYPE),
                new TFCWorld("minecraft:mossy_stone_bricks", TFC_BRICK_TYPE),
                new TFCWorld("minecraft:chest", TFC_WOOD_TYPE),
                new TFCWorld("minecraft:oak_log", TFC_WOOD_TYPE),
                new TFCWorld("minecraft:oak_planks", TFC_WOOD_TYPE),
                new TFCWorld("minecraft:oak_door", TFC_WOOD_TYPE),
                new TFCWorld("minecraft:oak_fence", TFC_WOOD_TYPE),
                new TFCWorld("minecraft:oak_fence_gate", TFC_WOOD_TYPE),
                new TFCWorld("minecraft:oak_slab", TFC_WOOD_TYPE),
                new TFCWorld("minecraft:oak_stairs", TFC_WOOD_TYPE),
                new TFCWorld("minecraft:oak_wood", TFC_WOOD_TYPE),
                new TFCWorld("minecraft:dark_oak_log", TFC_WOOD_TYPE),
                new TFCWorld("minecraft:dark_oak_planks", TFC_WOOD_TYPE),
                new TFCWorld("minecraft:dark_oak_door", TFC_WOOD_TYPE),
                new TFCWorld("minecraft:dark_oak_fence", TFC_WOOD_TYPE),
                new TFCWorld("minecraft:dark_oak_fence_gate", TFC_WOOD_TYPE),
                new TFCWorld("minecraft:dark_oak_slab", TFC_WOOD_TYPE),
                new TFCWorld("minecraft:dark_oak_stairs", TFC_WOOD_TYPE),
                new TFCWorld("minecraft:dark_oak_wood", TFC_WOOD_TYPE));
    }
}
