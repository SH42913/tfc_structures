package com.farco.tfc_structures.mixin;

import net.dries007.tfc.world.chunkdata.RockData;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SurfaceBuilderContext.class)
public interface SurfaceBuilderContextAccessorMixin {
    @Mutable
    @Accessor(value = "rockData", remap = false)
    void setRockData(RockData rockData);

    @Accessor(value = "rockData", remap = false)
    RockData getRockData();

    @Mutable
    @Accessor(value = "cursor", remap = false)
    void setCursor(BlockPos.MutableBlockPos cursor);

    @Accessor(value = "cursor", remap = false)
    BlockPos.MutableBlockPos getCursor();

    @Accessor(value = "rainfall", remap = false)
    void setRainfall(float rainfall);
}
