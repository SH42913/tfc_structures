package com.farco.tfc_structures.mixin;

import com.farco.tfc_structures.TFCStructuresMod;
import com.farco.tfc_structures.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StructurePiece.class)
public abstract class StructurePieceMixin {

    @Redirect(
            method = "placeBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
            )
    )
    private boolean redirectSetBlock(
            WorldGenLevel worldGenLevel,
            BlockPos pos,
            BlockState state,
            int flags
    ) {
        if (CommonConfig.isAvailableToReplace(worldGenLevel)) {
            BlockState replacedState = TFCStructuresMod.getStructureProcessor().replaceBlock(worldGenLevel, pos, state);
            return worldGenLevel.setBlock(pos, replacedState, flags);
        } else {
            return worldGenLevel.setBlock(pos, state, flags);
        }
    }
}
