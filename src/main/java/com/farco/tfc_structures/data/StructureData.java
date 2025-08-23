package com.farco.tfc_structures.data;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public record StructureData(String id, List<String> allowedBiomes, Map<String, String> lootTablesMap) {
    public ResourceLocation getResourceLocation() {
        return ResourceLocation.parse(id);
    }
}
