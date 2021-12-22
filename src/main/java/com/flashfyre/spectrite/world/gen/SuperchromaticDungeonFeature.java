package com.flashfyre.spectrite.world.gen;

import com.flashfyre.spectrite.util.SpectriteBlockUtils;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.DungeonFeature;

import java.util.function.Predicate;

public class SuperchromaticDungeonFeature extends DungeonFeature
{
    public SuperchromaticDungeonFeature(Codec<DefaultFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    protected void setBlockStateIf(StructureWorldAccess world, BlockPos pos, BlockState state, Predicate<BlockState> predicate)
    {
        super.setBlockStateIf(world, pos, SpectriteBlockUtils.getSpectriteBlockState(state, pos), predicate);
    }
}
