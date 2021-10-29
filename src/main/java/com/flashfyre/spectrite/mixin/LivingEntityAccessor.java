package com.flashfyre.spectrite.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor
{
    @Invoker("onStatusEffectApplied")
    void invokeOnStatusEffectApplied(StatusEffectInstance effect, @Nullable Entity source);

    @Invoker("onStatusEffectUpgraded")
    void invokeOnStatusEffectUpgraded(StatusEffectInstance effect, boolean reapplyEffect, @Nullable Entity source);
}
