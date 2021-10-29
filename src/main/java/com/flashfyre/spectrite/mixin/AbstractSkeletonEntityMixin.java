package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.item.Items;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSkeletonEntity.class)
public class AbstractSkeletonEntityMixin
{
    private boolean checkSpectriteBow;

    @ModifyArg(method = "updateAttackType", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/mob/AbstractSkeletonEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"))
    private Hand modifyArgUpdateAttackTypeGetStackInHandSpectriteBow(Hand hand)
    {
        final AbstractSkeletonEntity skeletonEntity = (AbstractSkeletonEntity) (Object) this;
        checkSpectriteBow = hand == Hand.OFF_HAND && !skeletonEntity.getOffHandStack().isOf(net.minecraft.item.Items.BOW);
        if (checkSpectriteBow)
            return ProjectileUtil.getHandPossiblyHolding(skeletonEntity, Items.SPECTRITE_BOW);
        return hand;
    }

    @ModifyArg(method = "updateAttackType", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private Item modifyArgUpdateAttackTypeItemStackIsOfSuperchromatic(Item bowItem)
    {
        return checkSpectriteBow ? Items.SPECTRITE_BOW : bowItem;
    }

    @ModifyArg(method = "attack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/mob/AbstractSkeletonEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"))
    private Hand modifyArgAttackGetStackInHandSpectriteBow(Hand hand)
    {
        final AbstractSkeletonEntity skeletonEntity = (AbstractSkeletonEntity) (Object) this;
        if (hand == Hand.OFF_HAND && !skeletonEntity.getOffHandStack().isOf(net.minecraft.item.Items.BOW))
            return ProjectileUtil.getHandPossiblyHolding(skeletonEntity, Items.SPECTRITE_BOW);
        return hand;
    }

    @Inject(method = "canUseRangedWeapon", at = @At("RETURN"), cancellable = true)
    private void injectCanUseRangedWeaponSpectriteBow(RangedWeaponItem weapon, CallbackInfoReturnable<Boolean> cir)
    {
        if (!cir.getReturnValue() && weapon == Items.SPECTRITE_BOW)
            cir.setReturnValue(true);
    }
}
