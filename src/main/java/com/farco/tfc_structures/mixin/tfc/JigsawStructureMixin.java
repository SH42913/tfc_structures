package com.farco.tfc_structures.mixin.tfc;

import net.dries007.tfc.world.TFCChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(JigsawStructure.class)
public class JigsawStructureMixin {
    @ModifyVariable(
            method = "findGenerationPoint",
            at = @At(value = "STORE", ordinal = 0),
            ordinal = 0
    )
    private int modifyStartHeight(int value, Structure.GenerationContext context) {
        boolean isTfcGenerator = context.chunkGenerator() instanceof TFCChunkGenerator;
        if (isTfcGenerator) {
            return value + 1;
        }

        return value;
    }

}