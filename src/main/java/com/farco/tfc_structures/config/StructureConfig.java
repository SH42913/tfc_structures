package com.farco.tfc_structures.config;

import com.farco.tfc_structures.TFCStructuresMod;
import com.farco.tfc_structures.data.BiomeTag;
import com.farco.tfc_structures.data.StructureData;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class StructureConfig {
    public static final String CONFIG_NAME = "structure_config.json";

    public List<BiomeTag> biomeTags;

    //  Add desired structures from unregistered_structures here
    //  You can find available biome tags(or add new one) in tfc_structures_datapack\data\minecraft\tags\worldgen\biome
    public List<StructureData> activeStructures;

    public Set<String> disabledStructures;

    //  A list of structures that exists in Minecraft, but is not registered in that config
    //  This list will be updated when server is started
    public List<String> unregisteredStructures;

    public void refreshUnused(Registry<Structure> structureRegistry) {
        var allStructures = structureRegistry.keySet().stream().map(ResourceLocation::toString).toList();

        var structures = new HashSet<>(allStructures);
        for (StructureData structure : activeStructures) {
            if (!structureRegistry.containsKey(structure.getResourceLocation())) {
                TFCStructuresMod.LOGGER.warn("Structure {} is not valid", structure.id());
            } else {
                structures.remove(structure.id());
            }
        }

        for (String structure : disabledStructures) {
            if (structureRegistry.containsKey(ResourceLocation.parse(structure))) {
                structures.remove(structure);
            }
        }

        unregisteredStructures = structures.stream().sorted().toList();
    }

    public @Nullable StructureData getDataByLocation(ResourceLocation location) {
        for (StructureData structureData : activeStructures) {
            if (structureData.getResourceLocation().equals(location)) {
                return structureData;
            }
        }

        return null;
    }

    public static StructureConfig getDefaultConfig() {
        var config = new StructureConfig();
        config.biomeTags = BiomeTag.getDefaultBiomeTags();
        config.activeStructures = getVanillaStructures();
        config.disabledStructures = getDisabledVanillaStructures();
        config.unregisteredStructures = Collections.emptyList();
        return config;
    }

    private static List<StructureData> getVanillaStructures() {
        List<StructureData> list = new ArrayList<>();
        list.add(new StructureData("minecraft:buried_treasure", List.of(BiomeTag.BEACH.getTagId(), BiomeTag.OCEANIC_MOUNTAIN_LAKE.getTagId()), Collections.emptyMap()));
        list.add(new StructureData("minecraft:desert_pyramid", List.of(BiomeTag.ANY_BADLANDS.getTagId(), BiomeTag.HILL.getTagId(), "tfc:shore", "tfc:plateau"), Collections.emptyMap()));
        list.add(new StructureData("minecraft:pillager_outpost", List.of(BiomeTag.CANYONS.getTagId(), BiomeTag.ANY_BADLANDS.getTagId(), BiomeTag.ANY_MOUNTAINS.getTagId(), BiomeTag.HIGHLANDS.getTagId()), Collections.emptyMap()));
        list.add(new StructureData("minecraft:shipwreck", List.of(BiomeTag.ANY_OCEAN.getTagId()), Collections.emptyMap()));
        list.add(new StructureData("minecraft:shipwreck_beached", List.of(BiomeTag.BEACH.getTagId(), BiomeTag.SWAMP.getTagId()), Collections.emptyMap()));
        list.add(new StructureData("minecraft:stronghold", List.of(BiomeTag.ANY_MOUNTAINS.getTagId()), Collections.emptyMap()));
        list.add(new StructureData("minecraft:swamp_hut", List.of(BiomeTag.SWAMP.getTagId(), BiomeTag.ANY_LAKE.getTagId(), BiomeTag.BEACH.getTagId()), Collections.emptyMap()));
        list.add(new StructureData("minecraft:trail_ruins", List.of(BiomeTag.HIGHLANDS.getTagId(), "tfc:old_mountains", "tfc:low_canyons", "tfc:hills", "tfc:plateau"), Collections.emptyMap()));
        list.add(new StructureData("minecraft:village_desert", List.of(BiomeTag.VILLAGE_BIOMES.getTagId()), Collections.emptyMap()));
        list.add(new StructureData("minecraft:village_plains", List.of(BiomeTag.VILLAGE_BIOMES.getTagId()), Collections.emptyMap()));
        list.add(new StructureData("minecraft:village_savanna", List.of(BiomeTag.VILLAGE_BIOMES.getTagId()), Collections.emptyMap()));
        list.add(new StructureData("minecraft:village_taiga", List.of(BiomeTag.VILLAGE_BIOMES.getTagId()), Collections.emptyMap()));
        list.add(new StructureData("minecraft:mansion", List.of(BiomeTag.ANY_BADLANDS.getTagId(), BiomeTag.PLAINS.getTagId()), Collections.emptyMap()));
        list.add(new StructureData("minecraft:jungle_pyramid", List.of(BiomeTag.ANY_BADLANDS.getTagId(), BiomeTag.CANYONS.getTagId(), BiomeTag.HIGHLANDS.getTagId()), Collections.emptyMap()));
        list.add(new StructureData("minecraft:ocean_ruin_cold", List.of(BiomeTag.DEEP_OCEAN.getTagId(), "tfc:ocean"), Collections.emptyMap()));
        list.add(new StructureData("minecraft:ocean_ruin_warm", List.of(BiomeTag.COMMON_OCEAN.getTagId(), "tfc:deep_ocean"), Collections.emptyMap()));
        list.add(new StructureData("minecraft:monument", List.of(BiomeTag.DEEP_OCEAN.getTagId()), Collections.emptyMap()));
        list.add(new StructureData("minecraft:ruined_portal", List.of(BiomeTag.VILLAGE_BIOMES.getTagId()), Collections.emptyMap()));
        list.add(new StructureData("minecraft:ruined_portal_swamp", List.of(BiomeTag.SWAMP.getTagId()), Collections.emptyMap()));
        list.add(new StructureData("minecraft:ruined_portal_mountain", List.of(BiomeTag.ANY_MOUNTAINS.getTagId()), Collections.emptyMap()));
        list.add(new StructureData("minecraft:ruined_portal_ocean", List.of(BiomeTag.ANY_OCEAN.getTagId(), BiomeTag.ANY_LAKE.getTagId(), BiomeTag.RIVER.getTagId()), Collections.emptyMap()));
        list.add(new StructureData("minecraft:ruined_portal_desert", List.of(BiomeTag.ANY_BADLANDS.getTagId()), Collections.emptyMap()));
        list.add(new StructureData("minecraft:ruined_portal_jungle", List.of(BiomeTag.HIGHLANDS.getTagId(), BiomeTag.HILL.getTagId(), BiomeTag.CANYONS.getTagId()), Collections.emptyMap()));
        list.add(new StructureData("minecraft:mineshaft", List.of(BiomeTag.ANY_MOUNTAINS.getTagId()), Collections.emptyMap()));
        return list;
    }

    private static Set<String> getDisabledVanillaStructures() {
        return Set.of(
                "minecraft:mineshaft_mesa",
                "minecraft:igloo",
                "minecraft:village_snowy"
        );
    }
}
