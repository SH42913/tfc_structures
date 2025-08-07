package com.farco.tfc_structures.data;

import com.farco.tfc_structures.TFCStructuresMod;

import java.util.ArrayList;
import java.util.List;

public record BiomeTag(String id, DatapackGenerator.TagValues tagValues) {
    public BiomeTag(String id, List<DatapackGenerator.TagLink> links) {
        this(id, new DatapackGenerator.TagValues(links));
    }

    public String getTagId() {
        return '#' + id;
    }

    public DatapackGenerator.TagLink getTagLink(boolean required) {
        return new DatapackGenerator.TagLink(getTagId(), required);
    }

    public static final BiomeTag PLAINS = new BiomeTag(TFCStructuresMod.MODID + ":is_plains", List.of(
            new DatapackGenerator.TagLink("minecraft:plains", true),
            new DatapackGenerator.TagLink("tfc:plains", false)
    ));

    public static final BiomeTag TAIGA = new BiomeTag(TFCStructuresMod.MODID + ":is_taiga", List.of(
            new DatapackGenerator.TagLink("minecraft:taiga", true),
            new DatapackGenerator.TagLink("minecraft:snowy_taiga", true),
            new DatapackGenerator.TagLink("minecraft:old_growth_pine_taiga", true),
            new DatapackGenerator.TagLink("minecraft:old_growth_spruce_taiga", true),
            new DatapackGenerator.TagLink("tfc:hills", false)
    ));

    public static final BiomeTag FOREST = new BiomeTag(TFCStructuresMod.MODID + ":is_forest", List.of(
            new DatapackGenerator.TagLink("minecraft:forest", true),
            new DatapackGenerator.TagLink("minecraft:flower_forest", true),
            new DatapackGenerator.TagLink("minecraft:birch_forest", true),
            new DatapackGenerator.TagLink("minecraft:old_growth_birch_forest", true),
            new DatapackGenerator.TagLink("minecraft:dark_forest", true),
            new DatapackGenerator.TagLink("minecraft:grove", true),
            new DatapackGenerator.TagLink("tfc:canyons", false),
            new DatapackGenerator.TagLink("tfc:low_canyons", false)
    ));

    public static final BiomeTag DEEP_OCEAN = new BiomeTag(TFCStructuresMod.MODID + ":is_deep_ocean", List.of(
            new DatapackGenerator.TagLink("minecraft:deep_frozen_ocean", true),
            new DatapackGenerator.TagLink("minecraft:deep_cold_ocean", true),
            new DatapackGenerator.TagLink("minecraft:deep_ocean", true),
            new DatapackGenerator.TagLink("minecraft:deep_lukewarm_ocean", true),
            new DatapackGenerator.TagLink("tfc:deep_ocean", false),
            new DatapackGenerator.TagLink("tfc:deep_ocean_trench", false)
    ));

    public static final BiomeTag COLD_OCEAN = new BiomeTag(TFCStructuresMod.MODID + ":is_cold_ocean", List.of(
            new DatapackGenerator.TagLink("minecraft:frozen_ocean", true),
            new DatapackGenerator.TagLink("minecraft:cold_ocean", true)
    ));

    public static final BiomeTag WARM_OCEAN = new BiomeTag(TFCStructuresMod.MODID + ":is_warm_ocean", List.of(
            new DatapackGenerator.TagLink("minecraft:lukewarm_ocean", true),
            new DatapackGenerator.TagLink("minecraft:warm_ocean", true)
    ));

    public static final BiomeTag OCEAN = new BiomeTag(TFCStructuresMod.MODID + ":is_ocean", List.of(
            new DatapackGenerator.TagLink("minecraft:ocean", true),
            COLD_OCEAN.getTagLink(true),
            WARM_OCEAN.getTagLink(true),
            new DatapackGenerator.TagLink("tfc:ocean", false),
            new DatapackGenerator.TagLink("tfc:ocean_reef", false)
    ));

    public static final BiomeTag ALL_OCEAN = new BiomeTag(TFCStructuresMod.MODID + ":is_all_ocean", List.of(
            DEEP_OCEAN.getTagLink(true),
            OCEAN.getTagLink(true)
    ));

    public static final BiomeTag BADLANDS = new BiomeTag(TFCStructuresMod.MODID + ":is_badlands", List.of(
            new DatapackGenerator.TagLink("minecraft:badlands", true),
            new DatapackGenerator.TagLink("minecraft:eroded_badlands", true),
            new DatapackGenerator.TagLink("minecraft:wooded_badlands", true),
            new DatapackGenerator.TagLink("tfc:badlands", false),
            new DatapackGenerator.TagLink("tfc:inverted_badlands", false)
    ));

    public static final BiomeTag BEACH = new BiomeTag(TFCStructuresMod.MODID + ":is_beach", List.of(
            new DatapackGenerator.TagLink("minecraft:beach", true),
            new DatapackGenerator.TagLink("minecraft:snowy_beach", true),
            new DatapackGenerator.TagLink("tfc:shore", false)
    ));

    public static final BiomeTag LAKE = new BiomeTag(TFCStructuresMod.MODID + ":is_lake", List.of(
            new DatapackGenerator.TagLink("tfc:lake", false),
            new DatapackGenerator.TagLink("tfc:plateau_lake", false),
            new DatapackGenerator.TagLink("tfc:mountain_lake", false),
            new DatapackGenerator.TagLink("tfc:old_mountain_lake", false),
            new DatapackGenerator.TagLink("tfc:oceanic_mountain_lake", false),
            new DatapackGenerator.TagLink("tfc:volcanic_mountain_lake", false),
            new DatapackGenerator.TagLink("tfc:volcanic_oceanic_mountain_lake", false)
    ));

    public static final BiomeTag RIVER = new BiomeTag(TFCStructuresMod.MODID + ":is_river", List.of(
            new DatapackGenerator.TagLink("minecraft:river", true),
            new DatapackGenerator.TagLink("minecraft:frozen_river", true),
            new DatapackGenerator.TagLink("tfc:river", false)
    ));

    public static final BiomeTag REDUCE_WATER_AMBIENT_SPAWN = new BiomeTag(TFCStructuresMod.MODID + ":reduce_water_ambient_spawns", List.of(
            LAKE.getTagLink(false),
            RIVER.getTagLink(false)
    ));

    public static final BiomeTag OLD_MOUNTAINS = new BiomeTag(TFCStructuresMod.MODID + ":old_mountains", List.of(
            new DatapackGenerator.TagLink("tfc:old_mountains", false),
            new DatapackGenerator.TagLink("tfc:volcanic_mountains", false)
    ));

    public static final BiomeTag MOUNTAIN = new BiomeTag(TFCStructuresMod.MODID + ":is_mountain", List.of(
            new DatapackGenerator.TagLink("minecraft:meadow", true),
            new DatapackGenerator.TagLink("minecraft:frozen_peaks", true),
            new DatapackGenerator.TagLink("minecraft:jagged_peaks", true),
            new DatapackGenerator.TagLink("minecraft:stony_peaks", true),
            new DatapackGenerator.TagLink("minecraft:snowy_slopes", true),
            new DatapackGenerator.TagLink("minecraft:cherry_grove", true),
            new DatapackGenerator.TagLink("tfc:highlands", false),
            new DatapackGenerator.TagLink("tfc:mountains", false),
            OLD_MOUNTAINS.getTagLink(false)
    ));

    public static final BiomeTag SAVANNA = new BiomeTag(TFCStructuresMod.MODID + ":is_savanna", List.of(
            new DatapackGenerator.TagLink("minecraft:savanna", true),
            new DatapackGenerator.TagLink("minecraft:savanna_plateau", true),
            new DatapackGenerator.TagLink("minecraft:windswept_savanna", true),
            new DatapackGenerator.TagLink("tfc:plateau", false)
    ));

    public static final BiomeTag SWAMP = new BiomeTag(TFCStructuresMod.MODID + ":is_swamp", List.of(
            new DatapackGenerator.TagLink("minecraft:swamp", true),
            new DatapackGenerator.TagLink("tfc:lowlands", false)
    ));

    public static final BiomeTag SNOWY = new BiomeTag(TFCStructuresMod.MODID + ":is_snowy", List.of(
            new DatapackGenerator.TagLink("minecraft:snowy_taiga", true),
            new DatapackGenerator.TagLink("minecraft:snowy_plains", true),
            new DatapackGenerator.TagLink("minecraft:snowy_beach", true),
            new DatapackGenerator.TagLink("minecraft:snowy_slopes", true)
    ));

    public static final BiomeTag HILL = new BiomeTag(TFCStructuresMod.MODID + ":is_hill", List.of(
            new DatapackGenerator.TagLink("minecraft:windswept_hills", true),
            new DatapackGenerator.TagLink("minecraft:windswept_forest", true),
            new DatapackGenerator.TagLink("minecraft:windswept_gravelly_hills", true),
            new DatapackGenerator.TagLink("tfc:hills", false),
            new DatapackGenerator.TagLink("tfc:rolling_hills", false)
    ));

    public static final BiomeTag JUNGLE = new BiomeTag(TFCStructuresMod.MODID + ":is_jungle", List.of(
            new DatapackGenerator.TagLink("minecraft:bamboo_jungle", true),
            new DatapackGenerator.TagLink("minecraft:jungle", true),
            new DatapackGenerator.TagLink("minecraft:sparse_jungle", true),
            new DatapackGenerator.TagLink("tfc:low_canyons", false)
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
