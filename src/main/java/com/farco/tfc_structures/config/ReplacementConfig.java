package com.farco.tfc_structures.config;

public record ReplacementConfig(Direct[] directReplacements) {
    public static final String CONFIG_NAME = "replacement_config.json";

    public record Direct(String original, String replacement) {
    }

    public static ReplacementConfig getDefaultConfig() {
        return new ReplacementConfig(new Direct[]{
                new Direct("minecraft:cobblestone", "minecraft:diamond_block"),
        });
    }
}
