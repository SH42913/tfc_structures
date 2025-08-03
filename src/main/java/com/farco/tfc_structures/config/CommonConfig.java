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
            .comment("List to separate mossy blocks from others (it will generate MossyBlock tag)")
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

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private static boolean validateBlockIds(Object blockId) {
        return blockId instanceof String && ForgeRegistries.BLOCKS.containsKey(ResourceLocation.parse((String) blockId));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
    }
}
