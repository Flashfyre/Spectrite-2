package com.flashfyre.spectrite.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.util.math.BlockPos;

public class SpectriteChestBlockEntity extends ChestBlockEntity
{
    public SpectriteChestBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntities.SPECTRITE_CHEST, pos, state);
    }
}
