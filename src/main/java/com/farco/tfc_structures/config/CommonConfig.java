package com.farco.tfc_structures.config;

import com.farco.tfc_structures.TFCStructuresMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = TFCStructuresMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue BIOMES_TAGS_STRUCTURES_TO_LOGS = BUILDER
            .comment("Print all biomes, biome tags and structures to logs upon world loading")
            .comment("You'll able to find it with [BIOME], [BIOME_TAG] and [STRUCTURE] tags")
            .define("biomesTagsStructuresToLogs", false);

    public static final ForgeConfigSpec.BooleanValue FALLBACK_TO_TFC_STRUCTURES_LOOT = BUILDER
            .comment("Should Dynamic TFC Structures fallback to mod built-in loot tables or not")
            .comment("It will be happen if mod can't get overridden loot table from lootTablesOverrideMap of Structure")
            .comment("(eg, it will use tfc_structures:chests/desert_pyramid instead of minecraft:chests/desert_pyramid)")
            .define("fallbackToTfcStructuresLoot", true);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ALLOWED_DIMENSIONS = BUILDER
            .comment("Dimensions where structure blocks will be replaced")
            .defineList("allowedDimensions", List.of(Level.OVERWORLD.location().toString()), str -> str instanceof String);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> MOSSY_BLOCKS = BUILDER
            .comment("List to separate mossy blocks from others (it will generate #mossy_stones tag)")
            .defineList("mossyBlocks", List.of(
                    "minecraft:mossy_cobblestone",
                    "minecraft:mossy_cobblestone_slab",
                    "minecraft:mossy_cobblestone_stairs",
                    "minecraft:mossy_cobblestone_wall",
                    "minecraft:mossy_stone_brick_slab",
                    "minecraft:mossy_stone_brick_stairs",
                    "minecraft:mossy_stone_brick_wall",
                    "minecraft:mossy_stone_bricks"
            ), CommonConfig::validateBlockIds);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> STRIPPED_LOGS = BUILDER
            .comment("List to separate stripped logs from others (it will generate #stripped_log tag)")
            .defineList("strippedLogs", List.of(
                    "minecraft:stripped_oak_log",
                    "minecraft:stripped_spruce_log",
                    "minecraft:stripped_birch_log",
                    "minecraft:stripped_jungle_log",
                    "minecraft:stripped_acacia_log",
                    "minecraft:stripped_dark_oak_log",
                    "minecraft:stripped_mangrove_log",
                    "minecraft:stripped_cherry_log"
            ), CommonConfig::validateBlockIds);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> STRIPPED_WOOD = BUILDER
            .comment("List to separate stripped wood from others (it will generate #stripped_wood tag)")
            .defineList("strippedWood", List.of(
                    "minecraft:stripped_oak_wood",
                    "minecraft:stripped_spruce_wood",
                    "minecraft:stripped_birch_wood",
                    "minecraft:stripped_jungle_wood",
                    "minecraft:stripped_acacia_wood",
                    "minecraft:stripped_dark_oak_wood",
                    "minecraft:stripped_mangrove_wood",
                    "minecraft:stripped_cherry_wood"
            ), CommonConfig::validateBlockIds);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> CRACKED_BRICKS = BUILDER
            .comment("List to separate cracked bricks from others (it will generate #cracked_bricks tag)")
            .defineList("crackedBricks", List.of(
                    "minecraft:cracked_stone_bricks",
                    "minecraft:cracked_deepslate_bricks",
                    "minecraft:cracked_deepslate_tiles"
            ), CommonConfig::validateBlockIds);

    public static final ForgeConfigSpec.ConfigValue<String> DEFAULT_EMPTY_CHEST_LOOT_TABLE = BUILDER
            .comment("Loot table that will be used as emptyChestLootTable field for every new Structure in StructureConfig")
            .define("defaultEmptyChestLootTable", "");

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static Set<ResourceLocation> allowedDimensionsSet;

    private static boolean validateBlockIds(Object blockId) {
        return blockId instanceof String && ForgeRegistries.BLOCKS.containsKey(ResourceLocation.parse((String) blockId));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        allowedDimensionsSet = ALLOWED_DIMENSIONS.get().stream().map(ResourceLocation::parse).collect(Collectors.toSet());
    }

    public static boolean isAvailableToReplace(@NotNull WorldGenLevel worldGenLevel) {
        return allowedDimensionsSet.contains(worldGenLevel.getLevel().dimension().location());
    }
}
