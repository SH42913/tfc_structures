package com.farco.tfc_structures.data;

import com.farco.tfc_structures.TFCStructuresMod;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record BiomeTag(String id, DatapackGenerator.TagValues tagValues) {
    public BiomeTag(String id, List<String> links) {
        this(id, new DatapackGenerator.TagValues(links));
    }

    public BiomeTag(String id, Collection<ResourceKey<Biome>> biomes) {
        this(id, new DatapackGenerator.TagValues(biomes.stream().map(key -> key.location().toString()).toList()));
    }

    public String getTagId() {
        return '#' + id;
    }

    public static final BiomeTag BEACH = new BiomeTag(TFCStructuresMod.MODID + ":is_beach", List.of(
            TFCBiomes.TIDAL_FLATS.key(),
            TFCBiomes.SHORE.key()
    ));

    public static final BiomeTag OCEANIC_MOUNTAIN_LAKE = new BiomeTag(TFCStructuresMod.MODID + ":is_oceanic_mountain_lake", List.of(
            TFCBiomes.OCEANIC_MOUNTAIN_LAKE.key(),
            TFCBiomes.VOLCANIC_OCEANIC_MOUNTAIN_LAKE.key()
    ));

    public static final BiomeTag MOUNTAIN_LAKE = new BiomeTag(TFCStructuresMod.MODID + ":is_mountain_lake", List.of(
            TFCBiomes.MOUNTAIN_LAKE.key(),
            TFCBiomes.OLD_MOUNTAIN_LAKE.key(),
            TFCBiomes.VOLCANIC_MOUNTAIN_LAKE.key()
    ));

    public static final BiomeTag COMMON_LAKE = new BiomeTag(TFCStructuresMod.MODID + ":is_common_lake", List.of(
            TFCBiomes.LAKE.key(),
            TFCBiomes.PLATEAU_LAKE.key()
    ));

    public static final BiomeTag ANY_LAKE = new BiomeTag(TFCStructuresMod.MODID + ":is_any_lake", List.of(
            COMMON_LAKE.getTagId(),
            MOUNTAIN_LAKE.getTagId(),
            OCEANIC_MOUNTAIN_LAKE.getTagId()
    ));

    public static final BiomeTag RIVER = new BiomeTag(TFCStructuresMod.MODID + ":is_river", List.of(
            TFCBiomes.RIVER.key()
    ));

    public static final BiomeTag HIGHLANDS = new BiomeTag(TFCStructuresMod.MODID + ":is_highlands", List.of(
            TFCBiomes.HIGHLANDS.key()
    ));

    public static final BiomeTag ANY_BADLANDS = new BiomeTag(TFCStructuresMod.MODID + ":is_any_badlands", List.of(
            TFCBiomes.BADLANDS.key(),
            TFCBiomes.INVERTED_BADLANDS.key()
    ));

    public static final BiomeTag HILL = new BiomeTag(TFCStructuresMod.MODID + ":is_hill", List.of(
            TFCBiomes.ROLLING_HILLS.key(),
            TFCBiomes.HILLS.key()
    ));

    public static final BiomeTag CANYONS = new BiomeTag(TFCStructuresMod.MODID + ":is_canyons", List.of(
            TFCBiomes.LOW_CANYONS.key(),
            TFCBiomes.CANYONS.key()
    ));

    public static final BiomeTag COMMON_MOUNTAINS = new BiomeTag(TFCStructuresMod.MODID + ":is_common_mountains", List.of(
            TFCBiomes.MOUNTAINS.key(),
            TFCBiomes.OLD_MOUNTAINS.key()
    ));

    public static final BiomeTag VOLCANIC_MOUNTAINS = new BiomeTag(TFCStructuresMod.MODID + ":is_volcanic_mountains", List.of(
            TFCBiomes.VOLCANIC_MOUNTAINS.key(),
            TFCBiomes.VOLCANIC_OCEANIC_MOUNTAINS.key()
    ));

    public static final BiomeTag ANY_MOUNTAINS = new BiomeTag(TFCStructuresMod.MODID + ":is_any_mountains", List.of(
            COMMON_MOUNTAINS.getTagId(),
            VOLCANIC_MOUNTAINS.getTagId()
    ));

    public static final BiomeTag DEEP_OCEAN = new BiomeTag(TFCStructuresMod.MODID + ":is_deep_ocean", List.of(
            TFCBiomes.DEEP_OCEAN.key(),
            TFCBiomes.DEEP_OCEAN_TRENCH.key()
    ));

    public static final BiomeTag COMMON_OCEAN = new BiomeTag(TFCStructuresMod.MODID + ":is_common_ocean", List.of(
            TFCBiomes.OCEAN.key(),
            TFCBiomes.OCEAN_REEF.key()
    ));

    public static final BiomeTag ANY_OCEAN = new BiomeTag(TFCStructuresMod.MODID + ":is_any_ocean", List.of(
            DEEP_OCEAN.getTagId(),
            COMMON_OCEAN.getTagId()
    ));

    public static final BiomeTag SWAMP = new BiomeTag(TFCStructuresMod.MODID + ":is_swamp", List.of(
            TFCBiomes.LOWLANDS.key(),
            TFCBiomes.SALT_MARSH.key()
    ));

    public static final BiomeTag PLAINS = new BiomeTag(TFCStructuresMod.MODID + ":is_plains", List.of(
            TFCBiomes.PLAINS.key(),
            TFCBiomes.PLATEAU.key()
    ));

    public static final BiomeTag VILLAGE_BIOMES = new BiomeTag(TFCStructuresMod.MODID + ":village_biomes", List.of(
            PLAINS.getTagId(),
            HILL.getTagId()
    ));

    public static final BiomeTag ALL_TFC_BIOMES = new BiomeTag(TFCStructuresMod.MODID + ":all_tfc_biomes", TFCBiomes.getAllKeys());

    public static List<BiomeTag> getDefaultBiomeTags() {
        var list = new ArrayList<BiomeTag>();
        list.add(BEACH);
        list.add(OCEANIC_MOUNTAIN_LAKE);
        list.add(MOUNTAIN_LAKE);
        list.add(COMMON_LAKE);
        list.add(ANY_LAKE);
        list.add(RIVER);
        list.add(HIGHLANDS);
        list.add(ANY_BADLANDS);
        list.add(HILL);
        list.add(CANYONS);
        list.add(COMMON_MOUNTAINS);
        list.add(VOLCANIC_MOUNTAINS);
        list.add(ANY_MOUNTAINS);
        list.add(DEEP_OCEAN);
        list.add(COMMON_OCEAN);
        list.add(ANY_OCEAN);
        list.add(SWAMP);
        list.add(PLAINS);
        list.add(VILLAGE_BIOMES);
        list.add(ALL_TFC_BIOMES);
        return list;
    }
}
