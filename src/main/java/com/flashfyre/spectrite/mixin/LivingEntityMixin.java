package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.entity.SpectriteCompatibleMobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
    @Inject(method = "damage", at = @At("RETURN"))
    private void injectDamageSpectriteBossBarAddPlayer(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
    {
        if (cir.getReturnValue())
        {
            final LivingEntity entity = (LivingEntity) ((Object) this);
            if (entity instanceof SpectriteCompatibleMobEntity && ((SpectriteCompatibleMobEntity) entity).getSpectriteBossBar() != null
                    && source.getSource() instanceof ServerPlayerEntity player)
                ((SpectriteCompatibleMobEntity) entity).getSpectriteBossBar().addPlayer(player);
        }
    }
}
