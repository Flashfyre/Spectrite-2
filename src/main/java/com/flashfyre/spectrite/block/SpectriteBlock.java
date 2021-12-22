package com.flashfyre.spectrite.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface SpectriteBlock
{
    BlockState withOdd(BlockState blockState, BlockPos pos);
}
