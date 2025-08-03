package com.farco.tfc_structures.config;

import com.farco.tfc_structures.TFCStructuresMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

@Mod.EventBusSubscriber(modid = TFCStructuresMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

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

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private static boolean validateBlockIds(Object blockId) {
        return blockId instanceof String && ForgeRegistries.BLOCKS.containsKey(ResourceLocation.parse((String) blockId));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
    }
}
