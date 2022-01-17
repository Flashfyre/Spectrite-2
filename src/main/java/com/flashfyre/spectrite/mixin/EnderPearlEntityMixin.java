package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.item.Items;
import com.flashfyre.spectrite.particle.Particles;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderPearlEntity.class)
public class EnderPearlEntityMixin
{
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/thrown/ThrownItemEntity;tick()V"))
    private void injectTickSuperSuperchromaticGlow(CallbackInfo ci)
    {
        final EnderPearlEntity entity = (EnderPearlEntity) (Object) this;
        if (entity.getStack().getItem() == Items.SUPERCHROMATIC_ENDER_PEARL && entity.world.getTime() % 3 == 0)
            entity.world.addParticle(Particles.SUPERCHROMATIC_GLOW, entity.getParticleX(0.6D), entity.getRandomBodyY(), entity.getParticleZ(0.6D), 0.0D, 0.0D, 0.0D);
    }
}
