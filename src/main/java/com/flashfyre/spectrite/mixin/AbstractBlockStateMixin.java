package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.block.SimpleSpectriteBlock;
import com.flashfyre.spectrite.util.SpectriteUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin
{
    @Shadow
    public abstract Block getBlock();

    @Inject(method = "getMapColor", at = @At(value = "HEAD"), cancellable = true)
    private void injectSpectriteMapColor(BlockView blockView, BlockPos pos, CallbackInfoReturnable<MapColor> cir)
    {
        if (blockView instanceof World world && getBlock() instanceof SimpleSpectriteBlock)
            cir.setReturnValue(SpectriteUtils.getSpectriteMapColor(world, pos));
    }
}
