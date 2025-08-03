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

    //  Add desired structures from unregistered_structures here
    //  You can find available biome tags(or add new one) in tfc_structures_datapack\data\minecraft\tags\worldgen\biome
    public List<StructureData> activeStructures;

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
    }

    public static StructureConfig getDefaultConfig() {
        var config = new StructureConfig();
        config.activeStructures = getVanillaStructures();
        config.unregisteredStructures = new HashSet<>();
        return config;
    }

    private static List<StructureData> getVanillaStructures() {
        List<StructureData> list = new ArrayList<>();
        list.add(new StructureData("minecraft:buried_treasure", List.of(BiomeTag.BEACH.getTagId())));
        list.add(new StructureData("minecraft:desert_pyramid", List.of("tfc:badlands")));
        list.add(new StructureData("minecraft:jungle_temple", List.of("tfc:low_canyons")));
//        list.add(new StructureData("minecraft:mineshaft", List.of(BiomeTag.MOUNTAIN.getTagId())));
//        list.add(new StructureData("minecraft:mineshaft_mesa", List.of(BiomeTag.BADLANDS.getTagId())));
        list.add(new StructureData("minecraft:ocean_monument", List.of(BiomeTag.DEEP_OCEAN.getTagId())));
        list.add(new StructureData("minecraft:pillager_outpost", List.of(BiomeTag.PLAINS.getTagId())));
        list.add(new StructureData("minecraft:shipwreck", List.of(BiomeTag.OCEAN.getTagId())));
        list.add(new StructureData("minecraft:shipwreck_beached", List.of(BiomeTag.BEACH.getTagId())));
        list.add(new StructureData("minecraft:stronghold", List.of("tfc:volcanic_mountains", "tfc:old_mountains")));
        list.add(new StructureData("minecraft:swamp_hut", List.of(BiomeTag.SWAMP.getTagId())));
        list.add(new StructureData("minecraft:trail_ruins", List.of(BiomeTag.TAIGA.getTagId())));
        list.add(new StructureData("minecraft:village_desert", List.of(BiomeTag.BEACH.getTagId())));
        list.add(new StructureData("minecraft:village_plains", List.of(BiomeTag.PLAINS.getTagId())));
        list.add(new StructureData("minecraft:village_savanna", List.of(BiomeTag.SAVANNA.getTagId())));
        list.add(new StructureData("minecraft:village_snowy", List.of("minecraft:snowy_plains")));
        list.add(new StructureData("minecraft:mansion", List.of("minecraft:dark_forest")));
        list.add(new StructureData("minecraft:jungle_pyramid", List.of(BiomeTag.JUNGLE.getTagId())));
        list.add(new StructureData("minecraft:ocean_ruin_cold", List.of(BiomeTag.COLD_OCEAN.getTagId())));
        list.add(new StructureData("minecraft:ocean_ruin_warm", List.of(BiomeTag.WARM_OCEAN.getTagId())));
        list.add(new StructureData("minecraft:monument", List.of(BiomeTag.DEEP_OCEAN.getTagId())));
        list.add(new StructureData("minecraft:igloo", List.of(BiomeTag.SNOWY.getTagId())));
        list.add(new StructureData("minecraft:ruined_portal_standard", List.of("tfc:highlands")));
        list.add(new StructureData("minecraft:ruined_portal_swamp", List.of(BiomeTag.SWAMP.getTagId())));
        list.add(new StructureData("minecraft:ruined_portal_jungle", List.of(BiomeTag.JUNGLE.getTagId())));
        list.add(new StructureData("minecraft:ruined_portal_mountain", List.of(BiomeTag.MOUNTAIN.getTagId())));
        list.add(new StructureData("minecraft:ruined_portal_ocean", List.of(BiomeTag.OCEAN.getTagId())));
        return list;
    }
}
