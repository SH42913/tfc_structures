package com.farco.tfc_structures.mixin;

import com.farco.tfc_structures.TFCStructuresMod;
import com.farco.tfc_structures.config.CommonConfig;
import com.farco.tfc_structures.processors.StructureReplacementProcessor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureStart.class)
public abstract class StructureStartMixin {
    @Final
    @Shadow
    private Structure structure;

    @Shadow
    public abstract ChunkPos getChunkPos();

    @Inject(method = "placeInChunk", at = @At("HEAD"))
    private void onPlaceInChunkHead(WorldGenLevel level, StructureManager manager, ChunkGenerator generator,
                                    RandomSource random, BoundingBox box, ChunkPos chunkPos,
                                    CallbackInfo ci) {
        if (!CommonConfig.isAvailableToReplace(level)) {
            return;
        }

        Registry<Structure> structureRegistry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        ResourceLocation location = structureRegistry.getKey(structure);
        if (location == null) {
            TFCStructuresMod.LOGGER.error("Can't get structure location");
            return;
        }

        StructureReplacementProcessor processor = new StructureReplacementProcessor(TFCStructuresMod.replacementConfig);
        StructureReplacementProcessor.THREAD_LOCAL.set(processor);
//        TFCStructuresMod.LOGGER.info("Start structure replacement for {} at {}", location, getChunkPos());
    }

    @Inject(method = "placeInChunk", at = @At("TAIL"))
    private void onPlaceInChunkTail(WorldGenLevel level, StructureManager manager, ChunkGenerator generator,
                                    RandomSource random, BoundingBox box, ChunkPos chunkPos,
                                    CallbackInfo ci) {
        var processor = StructureReplacementProcessor.THREAD_LOCAL.get();
        if (processor != null) {
            StructureReplacementProcessor.THREAD_LOCAL.remove();
            long worldSeed = level.getSeed();
            var rootChunkPos = getChunkPos();
            var worldGenRandom = new WorldgenRandom(WorldgenRandom.Algorithm.XOROSHIRO.newInstance(worldSeed));
            worldGenRandom.setLargeFeatureSeed(worldSeed, rootChunkPos.x, rootChunkPos.z);
            processor.applyReplacements(level, worldGenRandom, rootChunkPos, box, chunkPos);
        }
    }
}
