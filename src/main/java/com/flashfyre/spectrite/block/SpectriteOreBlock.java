package com.flashfyre.spectrite.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class SpectriteOreBlock extends OreBlock implements SpectriteBlock
{
    public static final BooleanProperty ODD = BooleanProperty.of("odd");

    public SpectriteOreBlock(AbstractBlock.Settings settings, UniformIntProvider experienceDropped)
    {
        super(settings, experienceDropped);
        this.setDefaultState(this.stateManager.getDefaultState().with(ODD, false));
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
        return blockState.with(ODD, pos.getY() % 2 != 0);
    }
}
