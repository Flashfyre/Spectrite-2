package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.entity.SpectriteCompatibleMobEntity;
import com.flashfyre.spectrite.item.Items;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AxolotlEntity.class)
public class AxolotlEntityMixin
{
    @Inject(method = "getBucketItem", at = @At("RETURN"), cancellable = true)
    private void injectGetBucketItemSuperchromatic(CallbackInfoReturnable<ItemStack> cir)
    {
        final AxolotlEntity axolotlEntity = (AxolotlEntity) (Object) this;
        if (((SpectriteCompatibleMobEntity) axolotlEntity).isSuperchromatic())
            cir.setReturnValue(new ItemStack(Items.SUPERCHROMATIC_AXOLOTL_BUCKET));
    }
}
