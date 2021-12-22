package com.flashfyre.spectrite.util;

import com.flashfyre.spectrite.block.SpectriteBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class SpectriteBlockUtils
{
    private static final HashMap<Block, Block[]> BLOCK_SPECTRITE_MATCHES = new HashMap<>();

    static
    {
        BLOCK_SPECTRITE_MATCHES.put(com.flashfyre.spectrite.block.Blocks.SUPERCHROMATIC_STONE_BRICKS,
                new Block[]{
                        Blocks.COBBLESTONE,
                        Blocks.STONE_BRICKS,
                });
        BLOCK_SPECTRITE_MATCHES.put(com.flashfyre.spectrite.block.Blocks.MOSSY_SUPERCHROMATIC_STONE_BRICKS,
                new Block[]{
                        Blocks.MOSSY_COBBLESTONE,
                        Blocks.MOSSY_STONE_BRICKS,
                });
        BLOCK_SPECTRITE_MATCHES.put(com.flashfyre.spectrite.block.Blocks.CRACKED_SUPERCHROMATIC_STONE_BRICKS,
                new Block[]{
                        Blocks.CRACKED_STONE_BRICKS,
                });
        BLOCK_SPECTRITE_MATCHES.put(com.flashfyre.spectrite.block.Blocks.CHISELED_SUPERCHROMATIC_STONE_BRICKS,
                new Block[]{
                        Blocks.CHISELED_STONE_BRICKS,
                });
    }

    public static BlockState getSpectriteBlockState(BlockState blockState, BlockPos pos)
    {
        final Block block = blockState.getBlock();
        for (Map.Entry<Block, Block[]> entry : BLOCK_SPECTRITE_MATCHES.entrySet())
        {
            for (Block checkBlock : entry.getValue())
            {
                if (block == checkBlock)
                    return ((SpectriteBlock) entry.getKey()).withOdd(entry.getKey().getDefaultState(), pos);
            }
        }
        return blockState;
    }
}
