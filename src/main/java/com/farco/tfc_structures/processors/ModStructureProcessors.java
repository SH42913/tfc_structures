package com.farco.tfc_structures.processors;

import com.farco.tfc_structures.TFCStructuresMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModStructureProcessors {
    public static final DeferredRegister<StructureProcessorType<?>> PROCESSORS = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, TFCStructuresMod.MODID);

    public static final RegistryObject<StructureProcessorType<TFCStructureProcessor>> TFC_PROCESSOR = PROCESSORS.register("tfc_processor", () -> () -> TFCStructureProcessor.CODEC);

    public static void register(IEventBus bus) {
        PROCESSORS.register(bus);
    }
}
