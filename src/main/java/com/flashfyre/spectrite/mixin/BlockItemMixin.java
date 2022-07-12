package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.component.chunk.SuperchromaticChunkComponent;
import com.flashfyre.spectrite.world.SuperchromaticCompatibleWorld;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin
{
    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At("HEAD"))
    public void injectPlaceSetWorldClearSuperchromatic(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir)
    {
        ((SuperchromaticCompatibleWorld) context.getWorld()).setClearSuperchromaticFlag(true);
    }

    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At("RETURN"))
    public void injectPlaceClearSuperchromatic(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir)
    {
        if (cir.getReturnValue() == ActionResult.SUCCESS)
        {
            final SuperchromaticChunkComponent superchromaticChunkComponent = SuperchromaticChunkComponent.KEY.get(context.getWorld().getChunk(context.getBlockPos()));
            superchromaticChunkComponent.getSuperchromaticBlocks().remove(context.getBlockPos());
        }
        ((SuperchromaticCompatibleWorld) context.getWorld()).setClearSuperchromaticFlag(false);
    }
}
