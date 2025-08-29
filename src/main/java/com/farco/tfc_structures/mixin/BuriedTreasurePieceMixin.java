package com.farco.tfc_structures.mixin;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.structures.BuriedTreasurePieces;
import net.minecraftforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BuriedTreasurePieces.BuriedTreasurePiece.class)
public class BuriedTreasurePieceMixin {
    @ModifyVariable(
            method = "postProcess",
            at = @At("STORE"),
            ordinal = 1
    )
    private BlockState modifyBlockstate1(BlockState original) {
        if (original.is(Tags.Blocks.SANDSTONE)) {
            return Blocks.SANDSTONE.defaultBlockState();
        }

        if (original.is(Tags.Blocks.STONE)) {
            return Blocks.STONE.defaultBlockState();
        }

        return original;
    }
}
