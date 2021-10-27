package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.item.SpectriteChargeableItem;
import com.flashfyre.spectrite.item.SpectriteItem;
import com.flashfyre.spectrite.text.SpectriteText;
import com.flashfyre.spectrite.util.SpectriteItemUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixin
{
    @ModifyVariable(method = "getTooltip", at = @At(value = "STORE"))
    private MutableText spectrite$modifyVariableGetTooltipMutableText(MutableText mutableText)
    {
        final ItemStack stack = (ItemStack) (Object) this;
        if (stack.getItem() instanceof SpectriteItem spectriteItem)
        {
            final boolean charged = spectriteItem instanceof SpectriteChargeableItem spectriteChargeableItem
                    && spectriteChargeableItem.isCharged(stack);
            return new SpectriteText(mutableText, charged);
        }

        return mutableText;
    }

    @Inject(method = "onStoppedUsing", at = @At("HEAD"))
    private void injectOnStoppedUsingChargeSpectriteChargeableItem(World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci)
    {
        if (user instanceof PlayerEntity playerEntity && playerEntity.getActiveHand() == Hand.OFF_HAND)
        {
            final ItemStack mainHandStack = playerEntity.getMainHandStack();
            if (mainHandStack.getItem() instanceof SpectriteChargeableItem mainHandSpectriteChargeableItem
                    && !mainHandSpectriteChargeableItem.isDepleted())
            {
                final ItemStack offHandStack = playerEntity.getOffHandStack();
                if (!(offHandStack.getItem() instanceof SpectriteChargeableItem)
                        && offHandStack.getMaxUseTime() >= SpectriteItemUtils.SPECTRITE_CHARGEABLE_USE_TICKS_THRESHOLD
                        && offHandStack.getMaxUseTime() - remainingUseTicks <= SpectriteItemUtils.SPECTRITE_CHARGEABLE_USE_TICKS_THRESHOLD)
                    SpectriteItemUtils.useSpectriteChargeableItem(playerEntity, mainHandStack, true);
            }
        }
    }
}
