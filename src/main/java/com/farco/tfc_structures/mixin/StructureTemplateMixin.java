package com.farco.tfc_structures.mixin;

import com.farco.tfc_structures.TFCStructuresMod;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructureTemplate.class)
public class StructureTemplateMixin {
    @Inject(method = "placeInWorld", at = @At("HEAD"))
    private void processorIntegration(ServerLevelAccessor p_230329_, BlockPos p_230330_, BlockPos p_230331_, StructurePlaceSettings p_230332_, RandomSource p_230333_, int p_230334_, CallbackInfoReturnable<Boolean> cir) {
        p_230332_.addProcessor(TFCStructuresMod.getStructureProcessor());
    }
}