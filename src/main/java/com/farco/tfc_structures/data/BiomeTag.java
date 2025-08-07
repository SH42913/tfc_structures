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

    public static final BiomeTag BEACH = new BiomeTag(TFCStructuresMod.MODID + ":is_beach", List.of(
            "tfc:tidal_flats",
            "tfc:shore"
    ));

    public static final BiomeTag OCEANIC_MOUNTAIN_LAKE = new BiomeTag(TFCStructuresMod.MODID + ":is_oceanic_mountain_lake", List.of(
            "tfc:oceanic_mountain_lake",
            "tfc:volcanic_oceanic_mountain_lake"
    ));

    public static final BiomeTag MOUNTAIN_LAKE = new BiomeTag(TFCStructuresMod.MODID + ":is_mountain_lake", List.of(
            "tfc:mountain_lake",
            "tfc:old_mountain_lake",
            "tfc:volcanic_mountain_lake"
    ));

    public static final BiomeTag COMMON_LAKE = new BiomeTag(TFCStructuresMod.MODID + ":is_common_lake", List.of(
            "tfc:lake",
            "tfc:plateau_lake"
    ));

    public static final BiomeTag ANY_LAKE = new BiomeTag(TFCStructuresMod.MODID + ":is_any_lake", List.of(
            COMMON_LAKE.getTagId(),
            MOUNTAIN_LAKE.getTagId(),
            OCEANIC_MOUNTAIN_LAKE.getTagId()
    ));

    public static final BiomeTag RIVER = new BiomeTag(TFCStructuresMod.MODID + ":is_river", List.of(
            "tfc:river"
    ));

    public static final BiomeTag HIGHLANDS = new BiomeTag(TFCStructuresMod.MODID + ":is_highlands", List.of(
            "tfc:highlands"
    ));

    public static final BiomeTag BADLANDS = new BiomeTag(TFCStructuresMod.MODID + ":is_badlands", List.of(
            "tfc:badlands",
            "tfc:inverted_badlands"
    ));

    public static final BiomeTag HILL = new BiomeTag(TFCStructuresMod.MODID + ":is_hill", List.of(
            "tfc:rolling_hills",
            "tfc:hills"
    ));

    public static final BiomeTag CANYONS = new BiomeTag(TFCStructuresMod.MODID + ":is_canyons", List.of(
            "tfc:low_canyons",
            "tfc:canyons"
    ));

    public static final BiomeTag MOUNTAINS = new BiomeTag(TFCStructuresMod.MODID + ":is_mountains", List.of(
            "tfc:mountains",
            "tfc:old_mountains"
    ));

    public static final BiomeTag VOLCANIC_MOUNTAINS = new BiomeTag(TFCStructuresMod.MODID + ":is_volcanic_mountains", List.of(
            "tfc:volcanic_mountains",
            "tfc:volcanic_oceanic_mountains"
    ));

    public static final BiomeTag ANY_MOUNTAINS = new BiomeTag(TFCStructuresMod.MODID + ":is_any_mountains", List.of(
            MOUNTAINS.getTagId(),
            VOLCANIC_MOUNTAINS.getTagId()
    ));

    public static final BiomeTag DEEP_OCEAN = new BiomeTag(TFCStructuresMod.MODID + ":is_deep_ocean", List.of(
            "tfc:deep_ocean",
            "tfc:deep_ocean_trench"
    ));

    public static final BiomeTag COMMON_OCEAN = new BiomeTag(TFCStructuresMod.MODID + ":is_common_ocean", List.of(
            "tfc:ocean",
            "tfc:ocean_reef"
    ));

    public static final BiomeTag ANY_OCEAN = new BiomeTag(TFCStructuresMod.MODID + ":is_any_ocean", List.of(
            DEEP_OCEAN.getTagId(),
            COMMON_OCEAN.getTagId()
    ));

    public static final BiomeTag SWAMP = new BiomeTag(TFCStructuresMod.MODID + ":is_swamp", List.of(
            "tfc:lowlands",
            "tfc:salt_marsh"
    ));

    public static final BiomeTag PLAINS = new BiomeTag(TFCStructuresMod.MODID + ":is_plains", List.of(
            "tfc:plains",
            "tfc:plateau"
    ));

    public static List<BiomeTag> getDefaultBiomeTags() {
        var list = new ArrayList<BiomeTag>();
        list.add(BEACH);
        list.add(OCEANIC_MOUNTAIN_LAKE);
        list.add(MOUNTAIN_LAKE);
        list.add(COMMON_LAKE);
        list.add(ANY_LAKE);
        list.add(RIVER);
        list.add(HIGHLANDS);
        list.add(BADLANDS);
        list.add(HILL);
        list.add(CANYONS);
        list.add(MOUNTAINS);
        list.add(VOLCANIC_MOUNTAINS);
        list.add(ANY_MOUNTAINS);
        list.add(DEEP_OCEAN);
        list.add(COMMON_OCEAN);
        list.add(ANY_OCEAN);
        list.add(SWAMP);
        list.add(PLAINS);
        return list;
    }
}
