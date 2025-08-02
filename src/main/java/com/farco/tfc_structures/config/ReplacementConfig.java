package com.farco.tfc_structures.config;

public record ReplacementConfig(Direct[] directReplacements, String[] tfcReplacements) {
    public static final String CONFIG_NAME = "replacement_config.json";

    public record Direct(String original, String replacement) {
    }

    public static ReplacementConfig getDefaultConfig() {
        return new ReplacementConfig(new Direct[0], new String[]{"minecraft:cobblestone"});
    }
}
