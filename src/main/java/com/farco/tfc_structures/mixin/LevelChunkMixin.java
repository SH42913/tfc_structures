package com.farco.tfc_structures.mixin;

import com.farco.tfc_structures.processors.TempStructureData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunk.class)
public class LevelChunkMixin {
    @Inject(method = "setBlockState", at = @At("RETURN"))
    private void onSetBlockState(BlockPos pos, BlockState state, boolean isMoving, CallbackInfoReturnable<BlockState> cir) {
        var blockState = cir.getReturnValue();
        TempStructureData structureData = TempStructureData.CURRENT.get();
        if (blockState != null && structureData != null) {
            structureData.blockPosSet.add(pos);
        }
    }
}
