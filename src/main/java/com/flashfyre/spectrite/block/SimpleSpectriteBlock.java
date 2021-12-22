package com.flashfyre.spectrite.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;

public class SimpleSpectriteBlock extends Block implements SpectriteBlock
{
    public static final BooleanProperty ODD = BooleanProperty.of("odd");

    public SimpleSpectriteBlock(AbstractBlock.Settings settings)
    {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(ODD, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(ODD);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        final BlockPos pos = ctx.getBlockPos();
        return withOdd(super.getPlacementState(ctx), pos);
    }

    public BlockState withOdd(BlockState blockState, BlockPos pos)
    {
        return blockState.with(ODD, (pos.getX() + pos.getY() + pos.getZ()) % 2 != 0);
    }
}
