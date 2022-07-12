package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.component.chunk.SuperchromaticChunkComponent;
import com.flashfyre.spectrite.world.SuperchromaticCompatibleWorld;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class WorldMixin implements SuperchromaticCompatibleWorld
{
    private boolean clearSuperchromaticFlag;

    @Override
    public void setClearSuperchromaticFlag(boolean clearSuperchromaticFlag)
    {
        this.clearSuperchromaticFlag = clearSuperchromaticFlag;
    }

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z",
            at = @At("RETURN"))
    public void injectSetBlockStateClearSuperchromaticFlag(BlockPos pos, BlockState state, int flags, int maxUpdateDepth,
                                                           CallbackInfoReturnable<Boolean> cir)
    {
        if (clearSuperchromaticFlag && cir.getReturnValue() && !state.isAir())
        {
            final SuperchromaticChunkComponent superchromaticChunkComponent = SuperchromaticChunkComponent.KEY.get(getChunk(pos));
            superchromaticChunkComponent.getSuperchromaticBlocks().remove(pos);
        }
    }
}
