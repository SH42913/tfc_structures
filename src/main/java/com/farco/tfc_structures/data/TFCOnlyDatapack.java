package com.farco.tfc_structures.data;

import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.packs.BuiltInMetadata;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResourcesBuilder;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraftforge.fml.ModList;

import java.nio.file.Path;

public class TFCOnlyDatapack {
    private static final MutableComponent metadataDesc = Component.literal("tfc_structures built-in resources for TFC only");
    private static final int packVersion = SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA);
    private static final PackMetadataSection packMetadataSection = new PackMetadataSection(metadataDesc, packVersion);
    private static final BuiltInMetadata metadata = BuiltInMetadata.of(PackMetadataSection.TYPE, packMetadataSection);

    @SuppressWarnings("resource")
    public static RepositorySource getSource() {
        return (consumer) -> {
            Path jarPath = ModList.get().getModFileById("tfc_structures").getFile().getFilePath();
            PackResources resources = new VanillaPackResourcesBuilder()
                    .setMetadata(metadata)
                    .exposeNamespace("tfc_structures")
                    .pushUniversalPath(jarPath.resolve("data-tfc-only"))
                    .build();

            Pack pack = Pack.readMetaAndCreate(
                    "tfc_structures_data_for_tfc",
                    Component.literal("tfc_structures_data_for_tfc"),
                    true,
                    ignored -> resources,
                    PackType.SERVER_DATA,
                    Pack.Position.TOP,
                    PackSource.DEFAULT
            );

            consumer.accept(pack);
        };
    }
}
