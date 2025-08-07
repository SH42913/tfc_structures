package com.farco.tfc_structures.data;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record StructureData(String id, List<String> allowedBiomes) {
    public ResourceLocation getResourceLocation() {
        return ResourceLocation.parse(id);
    }

    public DatapackGenerator.TagValues getAllowedBiomesAsTagValues() {
        return new DatapackGenerator.TagValues(allowedBiomes);
    }
}
