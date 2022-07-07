package com.flashfyre.spectrite.world.gen;

import com.flashfyre.spectrite.block.SuperchromaticChorusFlowerBlock;
import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.ChorusPlantFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SuperchromaticChorusPlantFeature extends ChorusPlantFeature
{
    public SuperchromaticChorusPlantFeature(Codec<DefaultFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context)
    {
        final StructureWorldAccess structureWorldAccess = context.getWorld();
        final BlockPos blockPos = context.getOrigin();
        final Random random = context.getRandom();
        if (structureWorldAccess.isAir(blockPos) && structureWorldAccess.getBlockState(blockPos.down()).isOf(Blocks.END_STONE))
        {
            SuperchromaticChorusFlowerBlock.generate(structureWorldAccess, blockPos, random, 8);
            return true;
        }
        return false;
    }
}
