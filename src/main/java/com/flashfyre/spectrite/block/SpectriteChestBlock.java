package com.flashfyre.spectrite.block;

import com.flashfyre.spectrite.block.entity.SpectriteChestBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.util.math.BlockPos;

import java.util.function.Supplier;

public class SpectriteChestBlock extends ChestBlock
{
    protected SpectriteChestBlock(Settings settings, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier)
    {
        super(settings, supplier);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new SpectriteChestBlockEntity(pos, state);
    }
}
