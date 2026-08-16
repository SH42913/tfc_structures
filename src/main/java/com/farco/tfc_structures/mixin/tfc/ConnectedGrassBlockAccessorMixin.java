package com.farco.tfc_structures.mixin.tfc;

import net.dries007.tfc.common.blocks.soil.ConnectedGrassBlock;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Supplier;

@Mixin(ConnectedGrassBlock.class)
public interface ConnectedGrassBlockAccessorMixin {
    @Accessor(value = "farmland", remap = false)
    Supplier<? extends Block> getFarmlandSupplier();
}
