package com.farco.tfc_structures.mixin;

import com.farco.tfc_structures.TFCStructuresMod;
import com.farco.tfc_structures.config.CommonConfig;
import com.farco.tfc_structures.processors.TempStructureData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
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
public class StructureStartMixin {
    @Final
    @Shadow
    private Structure structure;

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

        TempStructureData.CURRENT.set(new TempStructureData());
    }

    @Inject(method = "placeInChunk", at = @At("TAIL"))
    private void onPlaceInChunkTail(WorldGenLevel level, StructureManager manager, ChunkGenerator generator,
                                    RandomSource random, BoundingBox box, ChunkPos chunkPos,
                                    CallbackInfo ci) {
        var structureData = TempStructureData.CURRENT.get();
        TempStructureData.CURRENT.remove();

        var processor = TFCStructuresMod.getStructureProcessor();
        var chunkAccess = level.getChunk(chunkPos.x, chunkPos.z);
        for (BlockPos pos : structureData.blockPosSet) {
            BlockState originalState = chunkAccess.getBlockState(pos);
            BlockEntity originalEntity = chunkAccess.getBlockEntity(pos);

            BlockState newState = processor.replaceBlock(level, pos, originalState);
            Block newBlock = newState.getBlock();
            chunkAccess.setBlockState(pos, newState, false);

            if (originalEntity == null) {
                continue;
            }

            BlockEntity newEntity = null;
            if (newBlock instanceof EntityBlock entityBlock) {
                newEntity = entityBlock.newBlockEntity(pos, newState);
            }

            if (newEntity == null) {
                var blockRegistry = level.registryAccess().registryOrThrow(Registries.BLOCK);
                var originalLocation = blockRegistry.getKey(originalState.getBlock());
                var newLocation = blockRegistry.getKey(newState.getBlock());
                TFCStructuresMod.LOGGER.error("Replacement block {} can't fully replace {} due first one is not EntityBlock", newLocation, originalLocation);
                continue;
            }

            var originalTag = originalEntity.saveWithFullMetadata();
            newEntity.load(originalTag);
            chunkAccess.setBlockEntity(newEntity);
        }
    }
}
