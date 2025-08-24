package com.farco.tfc_structures.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record StructureData(String id, List<String> allowedBiomes) {
    public static final Codec<StructureData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(StructureData::id),
            Codec.STRING.listOf().fieldOf("allowedBiomes").forGetter(StructureData::allowedBiomes)
    ).apply(instance, StructureData::new));

    public ResourceLocation getResourceLocation() {
        return ResourceLocation.parse(id);
    }
}
