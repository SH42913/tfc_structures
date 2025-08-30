package com.farco.tfc_structures.mixin;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.RuinedPortalPiece;
import net.minecraft.world.level.levelgen.structure.structures.RuinedPortalStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RuinedPortalStructure.class)
public class RuinedPortalStructureMixin {
    /**
     * @author SH42913
     * @reason We can't use full version with TFCChunkGenerator
     */
    @Overwrite
    private static int findSuitableY(RandomSource p_229267_, ChunkGenerator p_229268_, RuinedPortalPiece.VerticalPlacement p_229269_, boolean p_229270_, int p_229271_, int p_229272_, BoundingBox p_229273_, LevelHeightAccessor p_229274_, RandomState p_229275_) {
        int j = p_229274_.getMinBuildHeight() + 15;
        int i;
        if (p_229269_ == RuinedPortalPiece.VerticalPlacement.IN_NETHER) {
            if (p_229270_) {
                i = Mth.randomBetweenInclusive(p_229267_, 32, 100);
            } else if (p_229267_.nextFloat() < 0.5F) {
                i = Mth.randomBetweenInclusive(p_229267_, 27, 29);
            } else {
                i = Mth.randomBetweenInclusive(p_229267_, 29, 100);
            }
        } else if (p_229269_ == RuinedPortalPiece.VerticalPlacement.IN_MOUNTAIN) {
            int k = p_229271_ - p_229272_;
            i = invokeGetRandomWithinInterval(p_229267_, 70, k);
        } else if (p_229269_ == RuinedPortalPiece.VerticalPlacement.UNDERGROUND) {
            int j1 = p_229271_ - p_229272_;
            i = invokeGetRandomWithinInterval(p_229267_, j, j1);
        } else if (p_229269_ == RuinedPortalPiece.VerticalPlacement.PARTLY_BURIED) {
            i = p_229271_ - p_229272_ + Mth.randomBetweenInclusive(p_229267_, 2, 8);
        } else {
            i = p_229271_;
        }

        return i;
    }

    @Invoker("getRandomWithinInterval")
    static int invokeGetRandomWithinInterval(RandomSource random, int min, int max) {
        throw new AssertionError();
    }
}
