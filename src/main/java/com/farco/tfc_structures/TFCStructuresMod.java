package com.farco.tfc_structures;

import com.farco.tfc_structures.config.*;
import com.farco.tfc_structures.data.DatapackGenerator;
import com.farco.tfc_structures.mixin.SurfaceBuilderContextAccessorMixin;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
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

    public static final String MOSSY_TAG_NAME = "mossy_stones";
    public static final String STRIPPED_LOG_TAG_NAME = "stripped_log";
    public static final String STRIPPED_WOOD_TAG_NAME = "stripped_wood";
    public static final String CRACKED_BRICKS_TAG_NAME = "cracked_bricks";
    public static final TagKey<Block> MOSSY_TAG = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MODID, MOSSY_TAG_NAME));
    public static final TagKey<Block> STRIPPED_LOG_TAG = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MODID, STRIPPED_LOG_TAG_NAME));
    public static final TagKey<Block> STRIPPED_WOOD_TAG = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MODID, STRIPPED_WOOD_TAG_NAME));
    public static final TagKey<Block> CRACKED_BRICKS_TAG = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MODID, CRACKED_BRICKS_TAG_NAME));

    public static final Logger LOGGER;
    private static final Path CONFIG_FOLDER_PATH;
    private static final JsonConfigProvider CONFIG_PROVIDER;
    private static final DatapackGenerator DATAPACK_GENERATOR;

    public static ReplacementConfig replacementConfig;
    public static StructureConfig structureConfig;
    public static WorldgenConfig worldgenConfig;

    static {
        LOGGER = LogUtils.getLogger();
        CONFIG_FOLDER_PATH = FMLPaths.CONFIGDIR.get().resolve(TFCStructuresMod.MODID);
        CONFIG_PROVIDER = new JsonConfigProvider(CONFIG_FOLDER_PATH);
        DATAPACK_GENERATOR = new DatapackGenerator(FMLPaths.GAMEDIR.get().resolve(MODID + "_datapacks"));

        @SuppressWarnings("unused") Class<?> unused = SurfaceBuilderContextAccessorMixin.class;
    }

    public TFCStructuresMod(FMLJavaModLoadingContext modLoadingContext) {
        IEventBus modEventBus = modLoadingContext.getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addPackFinder);
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC, MODID + "/common-config.toml");

        MinecraftForge.EVENT_BUS.addListener(this::onServerStarted);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Common setup of {}", MODID);
        structureConfig = CONFIG_PROVIDER.load(StructureConfig.CONFIG_NAME, StructureConfig.CODEC, StructureConfig::getDefaultConfig);
        worldgenConfig = CONFIG_PROVIDER.load(WorldgenConfig.CONFIG_NAME, WorldgenConfig.CODEC, WorldgenConfig::getDefaultConfig);
        replacementConfig = CONFIG_PROVIDER.load(ReplacementConfig.CONFIG_NAME, ReplacementConfig.CODEC, ReplacementConfig::getDefaultConfig);
    }

    private void addPackFinder(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.SERVER_DATA) {
            DATAPACK_GENERATOR.refreshDatapack(worldgenConfig);
            event.addRepositorySource(DATAPACK_GENERATOR.getDatapackSource());
            LOGGER.info("Added generated datapack for {}", MODID);
        }
    }

    private void onServerStarted(ServerStartedEvent event) {
        RegistryAccess.Frozen registryAccess = event.getServer().registryAccess();

        Registry<Structure> structureRegistry = registryAccess.registryOrThrow(Registries.STRUCTURE);
        worldgenConfig.refreshUnused(structureRegistry);
        CONFIG_PROVIDER.save(WorldgenConfig.CONFIG_NAME, worldgenConfig, WorldgenConfig.CODEC);

        structureConfig.refreshUnused(structureRegistry);
        CONFIG_PROVIDER.save(StructureConfig.CONFIG_NAME, structureConfig, StructureConfig.CODEC);

        if (CommonConfig.BIOMES_TAGS_STRUCTURES_TO_LOGS.get()) {
            Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);
            for (ResourceLocation location : biomeRegistry.keySet()) {
                LOGGER.info("[BIOME] {}", location.toString());
            }

            for (var pair : biomeRegistry.getTags().toList()) {
                var tagLocation = pair.getFirst().location();
                var biomes = pair.getSecond().stream()
                        .map(Holder::unwrapKey)
                        .map(optional -> optional.map(key -> key.location().toString()).orElse("INVALID_BIOME"))
                        .toList();
                LOGGER.info("[BIOME_TAG] {} contains {}", tagLocation, String.join(", ", biomes));
            }

            for (ResourceLocation location : structureRegistry.keySet()) {
                LOGGER.info("[STRUCTURE] {}", location.toString());
            }
        }
    }
}
