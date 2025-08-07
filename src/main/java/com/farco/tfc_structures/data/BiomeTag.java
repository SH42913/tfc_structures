package com.farco.tfc_structures.data;

import com.farco.tfc_structures.TFCStructuresMod;

import java.util.ArrayList;
import java.util.List;

public record BiomeTag(String id, DatapackGenerator.TagValues tagValues) {
    public BiomeTag(String id, List<String> links) {
        this(id, new DatapackGenerator.TagValues(links));
    }

    public String getTagId() {
        return '#' + id;
    }

    public static final BiomeTag PLAINS = new BiomeTag(TFCStructuresMod.MODID + ":is_plains", List.of(
            "minecraft:plains",
            "tfc:plains"
    ));

    public static final BiomeTag TAIGA = new BiomeTag(TFCStructuresMod.MODID + ":is_taiga", List.of(
            "minecraft:taiga",
            "minecraft:snowy_taiga",
            "minecraft:old_growth_pine_taiga",
            "minecraft:old_growth_spruce_taiga",
            "tfc:hills"
    ));

    public static final BiomeTag FOREST = new BiomeTag(TFCStructuresMod.MODID + ":is_forest", List.of(
            "minecraft:forest",
            "minecraft:flower_forest",
            "minecraft:birch_forest",
            "minecraft:old_growth_birch_forest",
            "minecraft:dark_forest",
            "minecraft:grove",
            "tfc:canyons",
            "tfc:low_canyons"
    ));

    public static final BiomeTag DEEP_OCEAN = new BiomeTag(TFCStructuresMod.MODID + ":is_deep_ocean", List.of(
            "minecraft:deep_frozen_ocean",
            "minecraft:deep_cold_ocean",
            "minecraft:deep_ocean",
            "minecraft:deep_lukewarm_ocean",
            "tfc:deep_ocean",
            "tfc:deep_ocean_trench"
    ));

    public static final BiomeTag COLD_OCEAN = new BiomeTag(TFCStructuresMod.MODID + ":is_cold_ocean", List.of(
            "minecraft:frozen_ocean",
            "minecraft:cold_ocean"
    ));

    public static final BiomeTag WARM_OCEAN = new BiomeTag(TFCStructuresMod.MODID + ":is_warm_ocean", List.of(
            "minecraft:lukewarm_ocean",
            "minecraft:warm_ocean"
    ));

    public static final BiomeTag OCEAN = new BiomeTag(TFCStructuresMod.MODID + ":is_ocean", List.of(
            "minecraft:ocean",
            COLD_OCEAN.getTagId(),
            WARM_OCEAN.getTagId(),
            "tfc:ocean",
            "tfc:ocean_reef"
    ));

    public static final BiomeTag ALL_OCEAN = new BiomeTag(TFCStructuresMod.MODID + ":is_all_ocean", List.of(
            DEEP_OCEAN.getTagId(),
            OCEAN.getTagId()
    ));

    public static final BiomeTag BADLANDS = new BiomeTag(TFCStructuresMod.MODID + ":is_badlands", List.of(
            "minecraft:badlands",
            "minecraft:eroded_badlands",
            "minecraft:wooded_badlands",
            "tfc:badlands",
            "tfc:inverted_badlands"
    ));

    public static final BiomeTag BEACH = new BiomeTag(TFCStructuresMod.MODID + ":is_beach", List.of(
            "minecraft:beach",
            "minecraft:snowy_beach",
            "tfc:shore"
    ));

    public static final BiomeTag LAKE = new BiomeTag(TFCStructuresMod.MODID + ":is_lake", List.of(
            "tfc:lake",
            "tfc:plateau_lake",
            "tfc:mountain_lake",
            "tfc:old_mountain_lake",
            "tfc:oceanic_mountain_lake",
            "tfc:volcanic_mountain_lake",
            "tfc:volcanic_oceanic_mountain_lake"
    ));

    public static final BiomeTag RIVER = new BiomeTag(TFCStructuresMod.MODID + ":is_river", List.of(
            "minecraft:river",
            "minecraft:frozen_river",
            "tfc:river"
    ));

    public static final BiomeTag REDUCE_WATER_AMBIENT_SPAWN = new BiomeTag(TFCStructuresMod.MODID + ":reduce_water_ambient_spawns", List.of(
            LAKE.getTagId(),
            RIVER.getTagId()
    ));

    public static final BiomeTag OLD_MOUNTAINS = new BiomeTag(TFCStructuresMod.MODID + ":old_mountains", List.of(
            "tfc:old_mountains",
            "tfc:volcanic_mountains"
    ));

    public static final BiomeTag MOUNTAIN = new BiomeTag(TFCStructuresMod.MODID + ":is_mountain", List.of(
            "minecraft:meadow",
            "minecraft:frozen_peaks",
            "minecraft:jagged_peaks",
            "minecraft:stony_peaks",
            "minecraft:snowy_slopes",
            "minecraft:cherry_grove",
            "tfc:highlands",
            "tfc:mountains",
            OLD_MOUNTAINS.getTagId()
    ));

    public static final BiomeTag SAVANNA = new BiomeTag(TFCStructuresMod.MODID + ":is_savanna", List.of(
            "minecraft:savanna",
            "minecraft:savanna_plateau",
            "minecraft:windswept_savanna",
            "tfc:plateau"
    ));

    public static final BiomeTag SWAMP = new BiomeTag(TFCStructuresMod.MODID + ":is_swamp", List.of(
            "minecraft:swamp",
            "tfc:lowlands"
    ));

    public static final BiomeTag SNOWY = new BiomeTag(TFCStructuresMod.MODID + ":is_snowy", List.of(
            "minecraft:snowy_taiga",
            "minecraft:snowy_plains",
            "minecraft:snowy_beach",
            "minecraft:snowy_slopes"
    ));

    public static final BiomeTag HILL = new BiomeTag(TFCStructuresMod.MODID + ":is_hill", List.of(
            "minecraft:windswept_hills",
            "minecraft:windswept_forest",
            "minecraft:windswept_gravelly_hills",
            "tfc:hills",
            "tfc:rolling_hills"
    ));

    public static final BiomeTag JUNGLE = new BiomeTag(TFCStructuresMod.MODID + ":is_jungle", List.of(
            "minecraft:bamboo_jungle",
            "minecraft:jungle",
            "minecraft:sparse_jungle",
            "tfc:low_canyons"
    ));

    public static List<BiomeTag> getAllVanillaBiomeTags() {
        var list = new ArrayList<BiomeTag>();
        list.add(PLAINS);
        list.add(TAIGA);
        list.add(FOREST);
        list.add(COLD_OCEAN);
        list.add(WARM_OCEAN);
        list.add(DEEP_OCEAN);
        list.add(OCEAN);
        list.add(ALL_OCEAN);
        list.add(BADLANDS);
        list.add(BEACH);
        list.add(LAKE);
        list.add(RIVER);
        list.add(REDUCE_WATER_AMBIENT_SPAWN);
        list.add(MOUNTAIN);
        list.add(SAVANNA);
        list.add(SWAMP);
        list.add(SNOWY);
        list.add(HILL);
        list.add(JUNGLE);
        return list;
    }
}
