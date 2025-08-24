package com.farco.tfc_structures.config;

import com.farco.tfc_structures.TFCStructuresMod;
import com.farco.tfc_structures.data.BiomeTag;
import com.farco.tfc_structures.data.StructureData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public final class WorldgenConfig {
    public static final Codec<WorldgenConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BiomeTag.CODEC.listOf().fieldOf("biomeTags").forGetter(cfg -> cfg.biomeTags),
            StructureData.CODEC.listOf().fieldOf("activeStructures").forGetter(cfg -> cfg.activeStructures),
            Codec.STRING.listOf().fieldOf("disabledStructures").forGetter(cfg -> cfg.disabledStructures),
            Codec.STRING.listOf().fieldOf("unregisteredStructures").forGetter(cfg -> cfg.unregisteredStructures)
    ).apply(instance, WorldgenConfig::new));

    public static final String CONFIG_NAME = "worldgen_config.json";
    private static final String HAS_STRUCTURE = "has_structure/";

    public List<BiomeTag> biomeTags;
    public List<StructureData> activeStructures;
    public List<String> disabledStructures;
    public List<String> unregisteredStructures;

    public WorldgenConfig(List<BiomeTag> biomeTags, List<StructureData> activeStructures, List<String> disabledStructures, List<String> unregisteredStructures) {
        this.biomeTags = biomeTags;
        this.activeStructures = activeStructures;
        this.disabledStructures = disabledStructures;
        this.unregisteredStructures = unregisteredStructures;
    }

    public void refreshUnused(Registry<Biome> biomeRegistry) {
        var allHasStructureTags = biomeRegistry.getTags()
                .map(pair -> pair.getFirst().location())
                .filter(location -> location.getPath().startsWith(HAS_STRUCTURE))
                .map(location -> location.toString().replace(HAS_STRUCTURE, ""))
                .collect(Collectors.toSet());

        var structures = new HashSet<>(allHasStructureTags);
        for (StructureData structure : activeStructures) {
            if (!allHasStructureTags.contains(structure.id())) {
                TFCStructuresMod.LOGGER.warn("Structure {} is not valid", structure.id());
            } else {
                structures.remove(structure.id());
            }
        }

        for (String structure : disabledStructures) {
            if (allHasStructureTags.contains(structure)) {
                structures.remove(structure);
            }
        }

        unregisteredStructures = structures.stream().sorted().toList();
    }

    public static WorldgenConfig getDefaultConfig() {
        return new WorldgenConfig(
                BiomeTag.getDefaultBiomeTags(),
                getVanillaStructures(),
                getDisabledVanillaStructures(),
                Collections.emptyList());
    }

    private static List<StructureData> getVanillaStructures() {
        List<StructureData> list = new ArrayList<>();
        list.add(new StructureData("minecraft:buried_treasure", List.of(BiomeTag.BEACH.getTagId(), BiomeTag.OCEANIC_MOUNTAIN_LAKE.getTagId())));
        list.add(new StructureData("minecraft:desert_pyramid", List.of(BiomeTag.ANY_BADLANDS.getTagId(), BiomeTag.HILL.getTagId(), "tfc:shore", "tfc:plateau")));
        list.add(new StructureData("minecraft:pillager_outpost", List.of(BiomeTag.CANYONS.getTagId(), BiomeTag.ANY_BADLANDS.getTagId(), BiomeTag.ANY_MOUNTAINS.getTagId(), BiomeTag.HIGHLANDS.getTagId())));
        list.add(new StructureData("minecraft:shipwreck", List.of(BiomeTag.ANY_OCEAN.getTagId())));
        list.add(new StructureData("minecraft:shipwreck_beached", List.of(BiomeTag.BEACH.getTagId(), BiomeTag.SWAMP.getTagId())));
        list.add(new StructureData("minecraft:stronghold", List.of(BiomeTag.ANY_MOUNTAINS.getTagId())));
        list.add(new StructureData("minecraft:swamp_hut", List.of(BiomeTag.SWAMP.getTagId(), BiomeTag.ANY_LAKE.getTagId(), BiomeTag.BEACH.getTagId())));
        list.add(new StructureData("minecraft:trail_ruins", List.of(BiomeTag.HIGHLANDS.getTagId(), "tfc:old_mountains", "tfc:low_canyons", "tfc:hills", "tfc:plateau")));
        list.add(new StructureData("minecraft:village_desert", List.of(BiomeTag.VILLAGE_BIOMES.getTagId())));
        list.add(new StructureData("minecraft:village_plains", List.of(BiomeTag.VILLAGE_BIOMES.getTagId())));
        list.add(new StructureData("minecraft:village_savanna", List.of(BiomeTag.VILLAGE_BIOMES.getTagId())));
        list.add(new StructureData("minecraft:village_taiga", List.of(BiomeTag.VILLAGE_BIOMES.getTagId())));
        list.add(new StructureData("minecraft:woodland_mansion", List.of(BiomeTag.ANY_BADLANDS.getTagId(), BiomeTag.PLAINS.getTagId())));
        list.add(new StructureData("minecraft:jungle_temple", List.of(BiomeTag.ANY_BADLANDS.getTagId(), BiomeTag.CANYONS.getTagId(), BiomeTag.HIGHLANDS.getTagId())));
        list.add(new StructureData("minecraft:ocean_ruin_cold", List.of(BiomeTag.DEEP_OCEAN.getTagId(), "tfc:ocean")));
        list.add(new StructureData("minecraft:ocean_ruin_warm", List.of(BiomeTag.COMMON_OCEAN.getTagId(), "tfc:deep_ocean")));
        list.add(new StructureData("minecraft:ocean_monument", List.of(BiomeTag.DEEP_OCEAN.getTagId())));
        list.add(new StructureData("minecraft:ruined_portal_standard", List.of(BiomeTag.VILLAGE_BIOMES.getTagId())));
        list.add(new StructureData("minecraft:ruined_portal_swamp", List.of(BiomeTag.SWAMP.getTagId())));
        list.add(new StructureData("minecraft:ruined_portal_mountain", List.of(BiomeTag.ANY_MOUNTAINS.getTagId())));
        list.add(new StructureData("minecraft:ruined_portal_ocean", List.of(BiomeTag.ANY_OCEAN.getTagId(), BiomeTag.ANY_LAKE.getTagId(), BiomeTag.RIVER.getTagId())));
        list.add(new StructureData("minecraft:ruined_portal_desert", List.of(BiomeTag.ANY_BADLANDS.getTagId())));
        list.add(new StructureData("minecraft:ruined_portal_jungle", List.of(BiomeTag.HIGHLANDS.getTagId(), BiomeTag.HILL.getTagId(), BiomeTag.CANYONS.getTagId())));
        list.add(new StructureData("minecraft:mineshaft", List.of(BiomeTag.ANY_MOUNTAINS.getTagId())));
        return list;
    }

    private static List<String> getDisabledVanillaStructures() {
        return List.of(
                "minecraft:mineshaft_mesa",
                "minecraft:igloo",
                "minecraft:village_snowy"
        );
    }
}
