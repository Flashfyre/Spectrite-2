package com.flashfyre.spectrite.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChorusFlowerBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class SuperchromaticChorusFlowerBlock extends ChorusFlowerBlock
{
    public static final BooleanProperty ODD = BooleanProperty.of("odd");

    protected SuperchromaticChorusFlowerBlock(SuperchromaticChorusPlantBlock plantBlock, Settings settings)
    {
        super(plantBlock, settings);
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
        return withOdd(super.getPlacementState(ctx), pos);
    }

    private BlockState withOdd(BlockState blockState, BlockPos pos)
    {
        return blockState.with(ODD, pos.getY() % 2 != 0);
    }

    @Override
    public void grow(World world, BlockPos pos, int age)
    {
        world.setBlockState(pos, withOdd(getDefaultState(), pos).with(AGE, age), Block.NOTIFY_LISTENERS);
        world.syncWorldEvent(WorldEvents.CHORUS_FLOWER_GROWS, pos, 0);
    }

    @Override
    public void die(World world, BlockPos pos)
    {
        world.setBlockState(pos, withOdd(getDefaultState(), pos).with(AGE, 5), Block.NOTIFY_LISTENERS);
        world.syncWorldEvent(WorldEvents.CHORUS_FLOWER_DIES, pos, 0);
    }

    private static boolean isSurroundedByAir(WorldView world, BlockPos pos, @Nullable Direction exceptDirection)
    {
        for (Direction direction : Direction.Type.HORIZONTAL)
        {
            if (direction == exceptDirection || world.isAir(pos.offset(direction))) continue;
            return false;
        }
        return true;
    }

    public static void generate(WorldAccess world, BlockPos pos, Random random, int size)
    {
        world.setBlockState(pos, ((SuperchromaticChorusPlantBlock) Blocks.SUPERCHROMATIC_CHORUS_PLANT).withConnectionProperties(world, pos), Block.NOTIFY_LISTENERS);
        SuperchromaticChorusFlowerBlock.generate(world, pos, random, pos, size, 0);
    }

    private static void generate(WorldAccess world, BlockPos pos, Random random, BlockPos rootPos, int size, int layer)
    {
        int j;
        final SuperchromaticChorusPlantBlock chorusPlantBlock = (SuperchromaticChorusPlantBlock) Blocks.SUPERCHROMATIC_CHORUS_PLANT;
        int i = random.nextInt(4) + 1;
        if (layer == 0)
            ++i;
        for (j = 0; j < i; ++j)
        {
            final BlockPos blockPos = pos.up(j + 1);
            if (!SuperchromaticChorusFlowerBlock.isSurroundedByAir(world, blockPos, null))
                return;
            world.setBlockState(blockPos, chorusPlantBlock.withConnectionProperties(world, blockPos), Block.NOTIFY_LISTENERS);
            world.setBlockState(blockPos.down(), chorusPlantBlock.withConnectionProperties(world, blockPos.down()), Block.NOTIFY_LISTENERS);
        }
        j = 0;
        if (layer < 4)
        {
            int blockPos = random.nextInt(4);
            if (layer == 0)
                ++blockPos;
            for (int k = 0; k < blockPos; ++k)
            {
                final Direction direction = Direction.Type.HORIZONTAL.random(random);
                final BlockPos blockPos2 = pos.up(i).offset(direction);
                if (Math.abs(blockPos2.getX() - rootPos.getX()) >= size || Math.abs(blockPos2.getZ() - rootPos.getZ()) >= size
                        || !world.isAir(blockPos2) || !world.isAir(blockPos2.down())
                        || !SuperchromaticChorusFlowerBlock.isSurroundedByAir(world, blockPos2, direction.getOpposite()))
                    continue;
                j = 1;
                world.setBlockState(blockPos2, chorusPlantBlock.withConnectionProperties(world, blockPos2), Block.NOTIFY_LISTENERS);
                world.setBlockState(blockPos2.offset(direction.getOpposite()),
                        chorusPlantBlock.withConnectionProperties(world, blockPos2.offset(direction.getOpposite())), Block.NOTIFY_LISTENERS);
                SuperchromaticChorusFlowerBlock.generate(world, blockPos2, random, rootPos, size, layer + 1);
            }
        }
        if (j == 0)
            world.setBlockState(pos.up(i), ((SuperchromaticChorusFlowerBlock) Blocks.SUPERCHROMATIC_CHORUS_FLOWER)
                    .withOdd(Blocks.SUPERCHROMATIC_CHORUS_FLOWER.getDefaultState(), pos).with(AGE, 5), Block.NOTIFY_LISTENERS);
    }
}
