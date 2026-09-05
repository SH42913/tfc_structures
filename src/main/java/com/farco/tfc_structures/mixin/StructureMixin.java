package com.farco.tfc_structures.mixin;

import com.farco.tfc_structures.TFCStructuresMod;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Structure.class)
public class StructureMixin {
    @Inject(method = "biomes", at = @At("RETURN"), cancellable = true)
    private void injectBiomes(CallbackInfoReturnable<HolderSet<Biome>> cir) {
        Structure self = (Structure) (Object) this;
        HolderSet<Biome> biomes = TFCStructuresMod.worldgenConfig.getStructureBiomes(self);
        if (biomes != null) {
            cir.setReturnValue(biomes);
        }
    }
}
