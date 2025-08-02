package com.farco.tfc_structures.processors;

import com.farco.tfc_structures.config.ReplacementConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TFCStructureProcessor extends StructureProcessor {
    public static final Codec<TFCStructureProcessor> CODEC = Codec.unit(new TFCStructureProcessor(null));

    private final Map<String, String> directReplacements;

    public TFCStructureProcessor(ReplacementConfig replacementConfig) {
        if (replacementConfig == null) {
            directReplacements = new HashMap<>();
        } else {
            directReplacements = Arrays
                    .stream(replacementConfig.directReplacements())
                    .collect(Collectors.toMap(ReplacementConfig.Direct::original, ReplacementConfig.Direct::replacement));
        }
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return ModStructureProcessors.TFC_PROCESSOR.get();
    }

    @Override
    public @Nullable StructureTemplate.StructureBlockInfo process(@NotNull LevelReader level,
                                                                  @NotNull BlockPos pos,
                                                                  @NotNull BlockPos pivot,
                                                                  @NotNull StructureTemplate.StructureBlockInfo original,
                                                                  @NotNull StructureTemplate.StructureBlockInfo current,
                                                                  @NotNull StructurePlaceSettings settings,
                                                                  @Nullable StructureTemplate template) {
        var newBlock = replaceBlock(current.state());
        return new StructureTemplate.StructureBlockInfo(
                current.pos(),
                newBlock,
                current.nbt()
        );
    }

    public BlockState replaceBlock(BlockState original) {
        ResourceLocation resourceLocation = ForgeRegistries.BLOCKS.getKey(original.getBlock());
        if (resourceLocation == null) {
            return original;
        }

        String blockId = resourceLocation.toString();
        String directReplacement = directReplacements.get(blockId);
        if (directReplacement != null) {
            return replaceDirectly(original, directReplacement);
        }

        return original;
    }

    private BlockState replaceDirectly(BlockState original, String directReplacement) {
        ResourceLocation location = ResourceLocation.parse(directReplacement);
        Block replacement = ForgeRegistries.BLOCKS.getValue(location);
        if (replacement == null) {
            return original;
        }

        BlockState newBlockState = replacement.defaultBlockState();
        if (newBlockState.isAir()) {
            return original;
        }

        for (Property<?> property : original.getProperties()) {
            if (newBlockState.hasProperty(property)) {
                Comparable<?> originalValue = original.getValue(property);
                newBlockState = setValueGeneric(newBlockState, property, originalValue);
            }
        }


        return newBlockState;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> BlockState setValueGeneric(BlockState state, Property<T> property, Comparable<?> value) {
        return state.setValue(property, (T) value);
    }
}
