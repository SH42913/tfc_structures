package com.farco.tfc_structures.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class StructureConfig {
    public static final Codec<StructureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(ResourceLocation.CODEC, Data.CODEC).fieldOf("structures").forGetter(cfg -> cfg.structures)
    ).apply(instance, StructureConfig::new));

    public record Data(Map<String, String> lootTablesMap) {
        public static final Codec<Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("lootTablesMap").forGetter(Data::lootTablesMap)
        ).apply(instance, Data::new));

        public static final Data EMPTY = new Data(Collections.emptyMap());
    }

    public static final String CONFIG_NAME = "structures_config.json";

    public Map<ResourceLocation, Data> structures;

    public StructureConfig(Map<ResourceLocation, Data> structures) {
        this.structures = new HashMap<>(structures);
    }

    public static StructureConfig getDefaultConfig() {
        return new StructureConfig(Collections.emptyMap());
    }

    public @Nullable Data getDataByLocation(ResourceLocation location) {
        return structures.get(location);
    }

    public void refreshUnused(Registry<Structure> structuresRegistry) {
        for (ResourceLocation location : structuresRegistry.keySet()) {
            if (!structures.containsKey(location)) {
                structures.put(location, Data.EMPTY);
            }
        }
    }
}
