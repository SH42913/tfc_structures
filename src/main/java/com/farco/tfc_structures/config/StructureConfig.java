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

public final class StructureConfig implements JsonConfigProvider.HasFieldsToSort {
    public static final Codec<StructureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(ResourceLocation.CODEC, Data.CODEC).fieldOf("structures").forGetter(cfg -> cfg.structures)
    ).apply(instance, StructureConfig::new));

    public record Data(String replacementPreset,
                       String emptyChestLootTable,
                       Map<String, String> lootTablesOverrideMap) {
        public static final Codec<Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("replacementPreset").forGetter(Data::replacementPreset),
                Codec.STRING.fieldOf("emptyChestLootTable").forGetter(Data::emptyChestLootTable),
                Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("lootTablesOverrideMap").forGetter(Data::lootTablesOverrideMap)
        ).apply(instance, Data::new));
    }

    public static final String CONFIG_NAME = "structures_config.json";

    public Map<ResourceLocation, Data> structures;

    public StructureConfig(Map<ResourceLocation, Data> structures) {
        this.structures = new HashMap<>(structures);
    }

    @Override
    public boolean needSort(String fieldName) {
        return fieldName.equals("structures");
    }

    public static StructureConfig getDefaultConfig() {
        return new StructureConfig(Collections.emptyMap());
    }

    public @Nullable Data getDataByLocation(ResourceLocation location) {
        return structures.get(location);
    }

    public void refreshUnused(Registry<Structure> structuresRegistry) {
        String defaultPreset = PresetContainer.DEFAULT_OVERWORLD_PRESET_NAME;
        String defaultEmptyChest = CommonConfig.DEFAULT_EMPTY_CHEST_LOOT_TABLE.get();
        Map<String, String> defaultLootOverrideMAp = Collections.emptyMap();
        Data defaultData = new Data(defaultPreset, defaultEmptyChest, defaultLootOverrideMAp);
        for (ResourceLocation location : structuresRegistry.keySet()) {
            if (!structures.containsKey(location)) {
                structures.put(location, defaultData);
            }
        }
    }
}
