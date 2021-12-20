package com.flashfyre.spectrite.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChorusPlantBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class SuperchromaticChorusPlantBlock extends ChorusPlantBlock
{
    public static final BooleanProperty ODD = BooleanProperty.of("odd");

    protected SuperchromaticChorusPlantBlock(Settings settings)
    {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(ODD, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(ODD);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        final BlockPos pos = ctx.getBlockPos();
        return super.getPlacementState(ctx).with(ODD, pos.getY() % 2 != 0);
    }

    @Override
    public BlockState withConnectionProperties(BlockView world, BlockPos pos)
    {
        final BlockState blockState = world.getBlockState(pos.down());
        final BlockState blockState2 = world.getBlockState(pos.up());
        final BlockState blockState3 = world.getBlockState(pos.north());
        final BlockState blockState4 = world.getBlockState(pos.east());
        final BlockState blockState5 = world.getBlockState(pos.south());
        final BlockState blockState6 = world.getBlockState(pos.west());
        return (((((this.getDefaultState()
                .with(DOWN, blockState.isOf(this) || blockState.isOf(Blocks.SUPERCHROMATIC_CHORUS_FLOWER) || blockState.isOf(net.minecraft.block.Blocks.END_STONE)))
                .with(UP, blockState2.isOf(this) || blockState2.isOf(Blocks.SUPERCHROMATIC_CHORUS_FLOWER)))
                .with(NORTH, blockState3.isOf(this) || blockState3.isOf(Blocks.SUPERCHROMATIC_CHORUS_FLOWER)))
                .with(EAST, blockState4.isOf(this) || blockState4.isOf(Blocks.SUPERCHROMATIC_CHORUS_FLOWER)))
                .with(SOUTH, blockState5.isOf(this) || blockState5.isOf(Blocks.SUPERCHROMATIC_CHORUS_FLOWER)))
                .with(WEST, blockState6.isOf(this) || blockState6.isOf(Blocks.SUPERCHROMATIC_CHORUS_FLOWER))
                .with(ODD, pos.getY() % 2 != 0);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        if (!state.canPlaceAt(world, pos))
        {
            world.createAndScheduleBlockTick(pos, this, 1);
            return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        }
        final boolean bl = neighborState.isOf(this) || neighborState.isOf(Blocks.SUPERCHROMATIC_CHORUS_FLOWER) || direction == Direction.DOWN && neighborState.isOf(net.minecraft.block.Blocks.END_STONE);
        return state.with((Property) FACING_PROPERTIES.get(direction), bl);
    }
}
