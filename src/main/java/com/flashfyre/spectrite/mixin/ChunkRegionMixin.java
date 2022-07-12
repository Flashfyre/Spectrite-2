package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.component.chunk.SuperchromaticChunkComponent;
import com.flashfyre.spectrite.util.SuperchromaticEntityUtils;
import com.flashfyre.spectrite.world.SuperchromaticCompatibleChunkRegion;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ChunkRegion.class)
public abstract class ChunkRegionMixin implements SuperchromaticCompatibleChunkRegion
{
    private boolean superchromaticFlag;

    @Override
    public boolean isSuperchromaticFlag()
    {
        return superchromaticFlag;
    }

    @Override
    public void setSuperchromaticFlag(boolean superchromaticFlag)
    {
        this.superchromaticFlag = superchromaticFlag;
    }

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)Lnet/minecraft/block/BlockState;",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    public void injectSetBlockStateSetSuperchromatic(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir, Chunk chunk)
    {
        if (superchromaticFlag)
        {
            final BlockPos blockPos = pos instanceof BlockPos.Mutable mutable ? mutable.toImmutable() : pos;
            SuperchromaticChunkComponent.KEY.get(chunk).getSuperchromaticBlocks().add(blockPos);
        }
    }

    @Inject(method = "spawnEntity", at = @At(value = "HEAD"))
    public void injectSpawnEntitySetSuperchromatic(Entity entity, CallbackInfoReturnable<Boolean> cir)
    {
        if (superchromaticFlag && entity instanceof MobEntity mobEntity)
            SuperchromaticEntityUtils.setSuperchromatic(mobEntity, true);
    }
}
