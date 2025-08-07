package com.farco.tfc_structures.config;

import com.farco.tfc_structures.TFCStructuresMod;
import com.farco.tfc_structures.data.BiomeTag;
import com.farco.tfc_structures.data.StructureData;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class StructureConfig {
    public static final String CONFIG_NAME = "structure_config.json";

    public List<BiomeTag> biomeTags;

    //  Add desired structures from unregistered_structures here
    //  You can find available biome tags(or add new one) in tfc_structures_datapack\data\minecraft\tags\worldgen\biome
    public List<StructureData> activeStructures;

    public Set<String> disabledStructures;

    //  A list of structures that exists in Minecraft, but is not registered in that config
    //  This list will be updated when server is started
    public Set<String> unregisteredStructures;

    public void refreshUnused(Registry<Structure> structureRegistry) {
        var allStructures = structureRegistry.keySet().stream().map(ResourceLocation::toString).toList();
        unregisteredStructures.clear();
        unregisteredStructures.addAll(allStructures);

        for (StructureData structure : activeStructures) {
            if (!structureRegistry.containsKey(structure.getResourceLocation())) {
                TFCStructuresMod.LOGGER.warn("Structure {} is not valid", structure.id());
            } else {
                unregisteredStructures.remove(structure.id());
            }
        }

        for (String structure : disabledStructures) {
            if (structureRegistry.containsKey(ResourceLocation.parse(structure))) {
                unregisteredStructures.remove(structure);
            }
        }
    }

    public static StructureConfig getDefaultConfig() {
        var config = new StructureConfig();
        config.biomeTags = BiomeTag.getDefaultBiomeTags();
        config.activeStructures = getVanillaStructures();
        config.disabledStructures = getDisabledVanillaStructures();
        config.unregisteredStructures = new HashSet<>();
        return config;
    }

    private static List<StructureData> getVanillaStructures() {
        List<StructureData> list = new ArrayList<>();
        list.add(new StructureData("minecraft:buried_treasure", List.of(BiomeTag.BEACH.getTagId(), BiomeTag.OCEANIC_MOUNTAIN_LAKE.getTagId())));
        list.add(new StructureData("minecraft:desert_pyramid", List.of(BiomeTag.BADLANDS.getTagId(), BiomeTag.HILL.getTagId(), "tfc:shore", "tfc:plateau")));
        list.add(new StructureData("minecraft:pillager_outpost", List.of(BiomeTag.CANYONS.getTagId(), BiomeTag.BADLANDS.getTagId(), BiomeTag.ANY_MOUNTAINS.getTagId(), "tfc:rolling_hills", "tfc:highlands")));
        list.add(new StructureData("minecraft:shipwreck", List.of(BiomeTag.ANY_OCEAN.getTagId())));
        list.add(new StructureData("minecraft:shipwreck_beached", List.of(BiomeTag.BEACH.getTagId(), BiomeTag.SWAMP.getTagId())));
        list.add(new StructureData("minecraft:stronghold", List.of(BiomeTag.ANY_MOUNTAINS.getTagId())));
        list.add(new StructureData("minecraft:swamp_hut", List.of(BiomeTag.SWAMP.getTagId(), BiomeTag.COMMON_LAKE.getTagId(), "tfc:tidal_flats")));
        list.add(new StructureData("minecraft:trail_ruins", List.of(BiomeTag.HIGHLANDS.getTagId(), "tfc:old_mountains", "tfc:low_canyons", "tfc:hills", "tfc:plateau")));
        list.add(new StructureData("minecraft:village_desert", List.of(BiomeTag.BEACH.getTagId(), BiomeTag.BADLANDS.getTagId())));
        list.add(new StructureData("minecraft:village_plains", List.of(BiomeTag.PLAINS.getTagId(), BiomeTag.HILL.getTagId())));
        list.add(new StructureData("minecraft:village_savanna", List.of("tfc:badlands", "tfc:shore", "tfc:plateau")));
        list.add(new StructureData("minecraft:village_taiga", List.of(BiomeTag.PLAINS.getTagId(), BiomeTag.HILL.getTagId())));
        list.add(new StructureData("minecraft:mansion", List.of(BiomeTag.BADLANDS.getTagId(), BiomeTag.PLAINS.getTagId())));
        list.add(new StructureData("minecraft:jungle_pyramid", List.of(BiomeTag.BADLANDS.getTagId(), BiomeTag.CANYONS.getTagId(), BiomeTag.HIGHLANDS.getTagId(), "tfc:rolling_hills", "tfc:plateau")));
        list.add(new StructureData("minecraft:ocean_ruin_cold", List.of(BiomeTag.DEEP_OCEAN.getTagId(), "tfc:ocean")));
        list.add(new StructureData("minecraft:ocean_ruin_warm", List.of(BiomeTag.COMMON_OCEAN.getTagId(), "tfc:deep_ocean")));
        list.add(new StructureData("minecraft:monument", List.of(BiomeTag.DEEP_OCEAN.getTagId())));
        list.add(new StructureData("minecraft:ruined_portal", List.of(BiomeTag.PLAINS.getTagId(), BiomeTag.HILL.getTagId())));
        list.add(new StructureData("minecraft:ruined_portal_swamp", List.of(BiomeTag.SWAMP.getTagId())));
        list.add(new StructureData("minecraft:ruined_portal_mountain", List.of(BiomeTag.ANY_MOUNTAINS.getTagId())));
        list.add(new StructureData("minecraft:ruined_portal_ocean", List.of(BiomeTag.ANY_OCEAN.getTagId(), BiomeTag.ANY_LAKE.getTagId(), BiomeTag.RIVER.getTagId())));
        list.add(new StructureData("minecraft:ruined_portal_desert", List.of(BiomeTag.BADLANDS.getTagId(), BiomeTag.BEACH.getTagId())));
        return list;
    }

    private static Set<String> getDisabledVanillaStructures() {
        return Set.of(
                "minecraft:mineshaft",
                "minecraft:mineshaft_mesa",
                "minecraft:igloo",
                "minecraft:village_snowy",
                "minecraft:ruined_portal_jungle"
        );
    }
}
