package com.farco.tfc_structures;

import com.farco.tfc_structures.config.CommonConfig;
import com.farco.tfc_structures.config.JsonConfigProvider;
import com.farco.tfc_structures.config.StructureConfig;
import com.farco.tfc_structures.data.DatapackGenerator;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.nio.file.Path;

@Mod(TFCStructuresMod.MODID)
public class TFCStructuresMod {
    public static final String MODID = "tfc_structures";

    public static final Logger LOGGER;
    private static final Path CONFIG_FOLDER_PATH;
    private static final JsonConfigProvider CONFIG_PROVIDER;
    private static final DatapackGenerator DATAPACK_GENERATOR;

    private StructureConfig structureConfig;

    static {
        LOGGER = LogUtils.getLogger();
        CONFIG_FOLDER_PATH = FMLPaths.CONFIGDIR.get().resolve(TFCStructuresMod.MODID);
        CONFIG_PROVIDER = new JsonConfigProvider(CONFIG_FOLDER_PATH);
        DATAPACK_GENERATOR = new DatapackGenerator(CONFIG_FOLDER_PATH);
    }

    public TFCStructuresMod(FMLJavaModLoadingContext modLoadingContext) {
        IEventBus modEventBus = modLoadingContext.getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addPackFinder);
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC, MODID + "/structures-config.toml");

        MinecraftForge.EVENT_BUS.addListener(this::onServerStarted);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Common setup of {}", MODID);
        structureConfig = CONFIG_PROVIDER.load(StructureConfig.CONFIG_NAME, StructureConfig.class, StructureConfig::getDefaultConfig);
    }

    private void addPackFinder(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.SERVER_DATA) {
            DATAPACK_GENERATOR.refreshDatapack(structureConfig);
            event.addRepositorySource(DATAPACK_GENERATOR.getDatapackSource());
            LOGGER.info("Added generated datapack for {}", MODID);
        }
    }

    private void onServerStarted(ServerStartedEvent event) {
        var registry = event.getServer().registryAccess().registryOrThrow(Registries.STRUCTURE);
        structureConfig.refreshUnused(registry);
        CONFIG_PROVIDER.save(StructureConfig.CONFIG_NAME, structureConfig);
    }
}
