package com.farco.tfc_structures.config;

import com.farco.tfc_structures.TFCStructuresMod;
import com.farco.tfc_structures.data.BiomeTag;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.*;
import java.util.stream.Collectors;

public final class WorldgenConfig {
    public static final Codec<WorldgenConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BiomeTag.CODEC.listOf().fieldOf("biomeTags").forGetter(cfg -> cfg.biomeTags),
            ResourceLocation.CODEC.listOf().fieldOf("disabledStructures").forGetter(cfg -> cfg.disabledStructures),
            ResourceLocation.CODEC.listOf().fieldOf("defaultWorldgenStructures").forGetter(cfg -> cfg.defaultWorldgenStructures)
    ).apply(instance, WorldgenConfig::new));

    public static final String CONFIG_NAME = "worldgen_config.json";

    public List<BiomeTag> biomeTags;
    public List<ResourceLocation> disabledStructures;
    public List<ResourceLocation> defaultWorldgenStructures;

    private Map<String, TagKey<Biome>> structureToTagMap;

    public WorldgenConfig(List<BiomeTag> biomeTags, List<ResourceLocation> disabledStructures, List<ResourceLocation> defaultWorldgenStructures) {
        this.biomeTags = biomeTags;
        this.disabledStructures = disabledStructures;
        this.defaultWorldgenStructures = defaultWorldgenStructures;
    }

    public void refreshUnused(Registry<Structure> structureRegistry) {
        var allStructures = structureRegistry.keySet();

        Set<ResourceLocation> activeStructures = biomeTags.stream()
                .flatMap(biomeTag -> biomeTag.structures().stream())
                .collect(Collectors.toSet());

        var structures = new HashSet<>(allStructures);
        for (ResourceLocation structure : activeStructures) {
            if (!allStructures.contains(structure)) {
                TFCStructuresMod.LOGGER.warn("Structure {} is not valid", structure);
            } else {
                structures.remove(structure);
            }
        }

        for (var structure : disabledStructures) {
            if (allStructures.contains(structure)) {
                structures.remove(structure);
            }
        }

        defaultWorldgenStructures = structures.stream().sorted(ResourceLocation::compareNamespaced).toList();
    }

    public static WorldgenConfig getDefaultConfig() {
        return new WorldgenConfig(
                getDefaultBiomeTags(),
                getDisabledVanillaStructures(),
                Collections.emptyList());
    }

    private static List<BiomeTag> getDefaultBiomeTags() {
        var list = new ArrayList<>(BiomeTag.getBuiltinBiomeTags());
        list.add(buildBiomeTag(BuiltinStructures.BURIED_TREASURE, BiomeTag.BEACH.getTagId(), BiomeTag.OCEANIC_MOUNTAIN_LAKE.getTagId()));
        list.add(buildBiomeTag(BuiltinStructures.DESERT_PYRAMID, BiomeTag.ANY_BADLANDS.getTagId(), BiomeTag.HILL.getTagId(), getBiomeId(TFCBiomes.SHORE)));
        list.add(buildBiomeTag(BuiltinStructures.PILLAGER_OUTPOST, BiomeTag.CANYONS.getTagId(), BiomeTag.ANY_BADLANDS.getTagId(), BiomeTag.ANY_MOUNTAINS.getTagId(), BiomeTag.HIGHLANDS.getTagId()));
        list.add(buildBiomeTag(BuiltinStructures.SHIPWRECK, BiomeTag.ANY_OCEAN.getTagId()));
        list.add(buildBiomeTag(BuiltinStructures.SHIPWRECK_BEACHED, BiomeTag.BEACH.getTagId(), BiomeTag.SWAMP.getTagId()));
        list.add(buildBiomeTag(BuiltinStructures.STRONGHOLD, BiomeTag.ANY_MOUNTAINS.getTagId()));
        list.add(buildBiomeTag(BuiltinStructures.SWAMP_HUT, BiomeTag.SWAMP.getTagId(), BiomeTag.ANY_LAKE.getTagId(), BiomeTag.BEACH.getTagId()));
        list.add(buildBiomeTag(BuiltinStructures.TRAIL_RUINS, BiomeTag.HIGHLANDS.getTagId(), getBiomeId(TFCBiomes.OLD_MOUNTAINS), getBiomeId(TFCBiomes.LOW_CANYONS), getBiomeId(TFCBiomes.HILLS), getBiomeId(TFCBiomes.PLATEAU)));
        list.add(buildBiomeTag(BuiltinStructures.WOODLAND_MANSION, BiomeTag.ANY_BADLANDS.getTagId(), BiomeTag.PLAINS.getTagId()));
        list.add(buildBiomeTag(BuiltinStructures.JUNGLE_TEMPLE, BiomeTag.ANY_BADLANDS.getTagId(), BiomeTag.CANYONS.getTagId(), BiomeTag.HIGHLANDS.getTagId()));
        list.add(buildBiomeTag(BuiltinStructures.OCEAN_RUIN_COLD, BiomeTag.DEEP_OCEAN.getTagId()));
        list.add(buildBiomeTag(BuiltinStructures.OCEAN_RUIN_WARM, BiomeTag.COMMON_OCEAN.getTagId()));
        list.add(buildBiomeTag(BuiltinStructures.OCEAN_MONUMENT, BiomeTag.DEEP_OCEAN.getTagId()));
        list.add(buildBiomeTag(BuiltinStructures.RUINED_PORTAL_STANDARD, BiomeTag.HILL.getTagId(), BiomeTag.PLAINS.getTagId()));
        list.add(buildBiomeTag(BuiltinStructures.RUINED_PORTAL_SWAMP, BiomeTag.SWAMP.getTagId()));
        list.add(buildBiomeTag(BuiltinStructures.RUINED_PORTAL_MOUNTAIN, BiomeTag.ANY_MOUNTAINS.getTagId()));
        list.add(buildBiomeTag(BuiltinStructures.RUINED_PORTAL_OCEAN, BiomeTag.ANY_OCEAN.getTagId()));
        list.add(buildBiomeTag(BuiltinStructures.RUINED_PORTAL_DESERT, BiomeTag.ANY_BADLANDS.getTagId()));
        list.add(buildBiomeTag(BuiltinStructures.RUINED_PORTAL_JUNGLE, BiomeTag.HIGHLANDS.getTagId(), BiomeTag.HILL.getTagId(), BiomeTag.CANYONS.getTagId()));
        list.add(buildBiomeTag(BuiltinStructures.MINESHAFT, BiomeTag.ANY_MOUNTAINS.getTagId()));
        list.add(buildBiomeTag("villages",
                List.of(BuiltinStructures.VILLAGE_DESERT, BuiltinStructures.VILLAGE_PLAINS, BuiltinStructures.VILLAGE_SAVANNA, BuiltinStructures.VILLAGE_TAIGA),
                List.of(BiomeTag.HILL.getTagId(), BiomeTag.PLAINS.getTagId())
        ));
        return list;
    }

    private static BiomeTag buildBiomeTag(ResourceKey<Structure> structure, ExtraCodecs.TagOrElementLocation... biomes) {
        return buildBiomeTag(structure.location().getPath(), List.of(structure), Arrays.stream(biomes).toList());
    }

    @SuppressWarnings("SameParameterValue")
    private static BiomeTag buildBiomeTag(String name, List<ResourceKey<Structure>> structures, List<ExtraCodecs.TagOrElementLocation> biomes) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(TFCStructuresMod.MODID, name);
        List<ResourceLocation> structureIds = structures.stream().map(ResourceKey::location).toList();
        return new BiomeTag(location, biomes, structureIds);
    }

    private static ExtraCodecs.TagOrElementLocation getBiomeId(BiomeExtension biomeExtension) {
        return new ExtraCodecs.TagOrElementLocation(biomeExtension.key().location(), false);
    }

    private static List<ResourceLocation> getDisabledVanillaStructures() {
        return List.of(
                BuiltinStructures.MINESHAFT_MESA.location(),
                BuiltinStructures.IGLOO.location(),
                BuiltinStructures.VILLAGE_SNOWY.location()
        );
    }

    public TagKey<Biome> getStructureTag(ResourceKey<Structure> structureKey) {
        if (structureToTagMap == null) {
            structureToTagMap = new HashMap<>();
            for (BiomeTag biomeTag : biomeTags) {
                var tagKey = biomeTag.getTagKey();
                for (ResourceLocation structureId : biomeTag.structures()) {
                    structureToTagMap.put(structureId.toString(), tagKey);
                }
            }
        }

        String structureId = structureKey.location().toString();
        return structureToTagMap.get(structureId);
    }
}
