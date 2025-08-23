package com.farco.tfc_structures.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public record StructureData(String id, List<String> allowedBiomes, Map<String, String> lootTablesMap) {
    public static final Codec<StructureData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(StructureData::id),
            Codec.STRING.listOf().fieldOf("allowedBiomes").forGetter(StructureData::allowedBiomes),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("lootTablesMap").forGetter(StructureData::lootTablesMap)
    ).apply(instance, StructureData::new));

    public ResourceLocation getResourceLocation() {
        return ResourceLocation.parse(id);
    }
}
