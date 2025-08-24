package com.farco.tfc_structures.mixin;

import com.farco.tfc_structures.TFCStructuresMod;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Structure.class)
public class StructureMixin {
    @Unique
    private HolderSet<Biome> tfc_structures$overriddenBiomes;

    @Inject(method = "biomes", at = @At("RETURN"), cancellable = true)
    private void injectBiomes(CallbackInfoReturnable<HolderSet<Biome>> cir) {
        if (tfc_structures$overriddenBiomes != null) {
            cir.setReturnValue(tfc_structures$overriddenBiomes);
            return;
        }

        MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            TFCStructuresMod.LOGGER.error("Can't override Structure biomes due there's no server");
            return;
        }

        RegistryAccess.Frozen registryAccess = server.registryAccess();
        Registry<Structure> structureRegistry = registryAccess.registryOrThrow(Registries.STRUCTURE);
        Structure thisOriginal = (Structure) (Object) this;
        var structureKey = structureRegistry.getResourceKey(thisOriginal).orElse(null);
        if (structureKey == null) {
            TFCStructuresMod.LOGGER.error("Can't override Structure biomes due can't detect resource location");
            return;
        }

        TagKey<Biome> structureTag = TFCStructuresMod.worldgenConfig.getStructureTag(structureKey);
        if (structureTag == null) {
            TFCStructuresMod.LOGGER.debug("There's no biome tag with {}", structureKey.location());
            return;
        }

        HolderSet.Named<Biome> biomes = registryAccess.lookupOrThrow(Registries.BIOME).get(structureTag).orElse(null);
        if (biomes == null) {
            TFCStructuresMod.LOGGER.error("Biome tag {} is not valid", structureTag);
            return;
        }

        tfc_structures$overriddenBiomes = biomes;
        cir.setReturnValue(tfc_structures$overriddenBiomes);
    }
}
