package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.entity.effect.StatusEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WitherEntity.class)
public class WitherEntityMixin
{
    @Inject(method = "addStatusEffect", at = @At(value = "RETURN"), cancellable = true)
    private void injectAddStatusEffectSuperchromatic(StatusEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir)
    {
        final WitherEntity witherEntity = (WitherEntity) (Object) this;
        if (effect.getEffectType() == StatusEffects.SUPERCHROMATIC)
        {
            final StatusEffectInstance statusEffectInstance = witherEntity.getActiveStatusEffects().get(effect.getEffectType());
            if (statusEffectInstance == null)
            {
                witherEntity.getActiveStatusEffects().put(effect.getEffectType(), effect);
                ((LivingEntityAccessor) witherEntity).invokeOnStatusEffectApplied(effect, source);
                cir.setReturnValue(true);
            } else if (statusEffectInstance.upgrade(effect))
            {
                ((LivingEntityAccessor) witherEntity).invokeOnStatusEffectUpgraded(statusEffectInstance, true, source);
                cir.setReturnValue(true);
            } else
                cir.setReturnValue(false);
        }
    }
}
