package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.item.Items;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BowAttackGoal.class)
public class BowAttackGoalMixin<T extends HostileEntity & RangedAttackMob>
{
    @Shadow
    @Final
    private T actor;

    @Inject(method = "isHoldingBow", at = @At("RETURN"), cancellable = true)
    private void injectIsHoldingBowSpectriteBow(CallbackInfoReturnable<Boolean> cir)
    {
        if (!cir.getReturnValue() && this.actor.isHolding(Items.SPECTRITE_BOW))
            cir.setReturnValue(true);
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/mob/HostileEntity;setCurrentHand(Lnet/minecraft/util/Hand;)V"))
    private Hand injectTickActorSetCurrentHandSpectriteBow(Hand hand)
    {
        if (hand == Hand.OFF_HAND && !actor.getOffHandStack().isOf(net.minecraft.item.Items.BOW))
            return ProjectileUtil.getHandPossiblyHolding(this.actor, Items.SPECTRITE_BOW);
        return hand;
    }
}
