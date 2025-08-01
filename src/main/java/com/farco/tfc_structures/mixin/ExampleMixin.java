package com.farco.tfc_structures.mixin;

import net.minecraft.client.gui.components.SplashRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashRenderer.class)
public class ExampleMixin {
    @Mutable
    @Final
    @Shadow
    private String splash;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void afterConstruct(String p_283500_, CallbackInfo ci) {
        this.splash = "IT WORKS!";
    }
}