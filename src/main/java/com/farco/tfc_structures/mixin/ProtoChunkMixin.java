package com.farco.tfc_structures.mixin;

import com.farco.tfc_structures.processors.StructureReplacementProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ProtoChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProtoChunk.class)
public class ProtoChunkMixin {
    @Inject(method = "setBlockState", at = @At("RETURN"))
    private void onSetBlockState(BlockPos pos, BlockState state, boolean isMoving, CallbackInfoReturnable<BlockState> cir) {
        var processor = StructureReplacementProcessor.THREAD_LOCAL.get();
        if (processor != null) {
            var blockPos = pos instanceof BlockPos.MutableBlockPos mutableBlockPos ? mutableBlockPos.immutable() : pos;
            processor.registerBlock(state, blockPos);
        }
    }
}
