package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.block.Blocks;
import com.flashfyre.spectrite.util.SuperchromaticEntityUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.AbstractMap;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin
{
    @Inject(method = "tick", at = @At(value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/block/entity/BeaconBlockEntity;updateLevel(Lnet/minecraft/world/World;III)I", shift = At.Shift.AFTER))
    private static void injectTickUpdateLevelUpdateBeaconLocations(
            World world, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity, CallbackInfo ci)
    {
        if (!world.isClient)
        {
            final int beaconLevel = ((BeaconBlockEntityAccessor) blockEntity).getLevel();
            final int spectriteBaseBlocks = countBeaconBaseSpectriteBlocks(world, pos.getX(), pos.getY(), pos.getZ(), beaconLevel);
            SuperchromaticEntityUtils.BEACON_LOCATIONS.put(pos, new AbstractMap.SimpleEntry(beaconLevel, spectriteBaseBlocks));
        }
    }

    private static int countBeaconBaseSpectriteBlocks(World world, int x, int y, int z, int level)
    {
        int spectriteBaseBlocks = 0;

        for (int j = 1; j <= level; j++)
        {
            int k = y - j;

            for (int l = x - j; l <= x + j; l++)
            {
                for (int m = z - j; m <= z + j; m++)
                {
                    if (world.getBlockState(new BlockPos(l, k, m)).isOf(Blocks.SPECTRITE_BLOCK))
                        spectriteBaseBlocks++;
                }
            }
        }

        return spectriteBaseBlocks;
    }

    @Inject(method = "markRemoved", at = @At(value = "HEAD"))
    private void injectMarkRemovedRemoveBeaconLocation(CallbackInfo ci)
    {
        SuperchromaticEntityUtils.BEACON_LOCATIONS.remove(((BlockEntityAccessor) this).getPos());
    }
}
