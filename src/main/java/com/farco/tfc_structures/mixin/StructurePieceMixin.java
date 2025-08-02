package com.farco.tfc_structures.mixin;

import com.farco.tfc_structures.TFCStructuresMod;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(StructurePiece.class)
public abstract class StructurePieceMixin {

    @ModifyArg(method = "placeBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
            ),
            index = 1
    )
    private BlockState modifyPlacedBlock(BlockState original) {
        return TFCStructuresMod.getStructureProcessor().replaceBlock(original);
    }
}
