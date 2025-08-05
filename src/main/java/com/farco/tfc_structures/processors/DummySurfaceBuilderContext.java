package com.farco.tfc_structures.processors;

import com.farco.tfc_structures.mixin.SurfaceBuilderContextAccessorMixin;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.settings.RockLayerSettings;
import net.dries007.tfc.world.settings.RockSettings;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import org.jetbrains.annotations.NotNull;

public class DummySurfaceBuilderContext extends SurfaceBuilderContext {
    public DummySurfaceBuilderContext(ChunkData chunkData, RockLayerSettings rockLayerSettings) {
        //noinspection DataFlowIssue
        super(null, null, chunkData, null, 0, rockLayerSettings, 0, 0);
    }

    @Override
    public @NotNull RockSettings getRock() {
        var pos = getAccessor().getCursor();
        return getAccessor().getRockData().getSurfaceRock(pos.getX(), pos.getZ());
    }

    public SurfaceBuilderContextAccessorMixin getAccessor() {
        return (SurfaceBuilderContextAccessorMixin) this;
    }
}
