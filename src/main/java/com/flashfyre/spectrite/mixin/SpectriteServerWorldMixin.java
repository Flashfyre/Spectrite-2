package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.entity.SpectriteCompatibleMobEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public class SpectriteServerWorldMixin
{
    @Inject(method = "spawnEntity", at = @At("HEAD"))
    private void injectSpawnEntityUpdateSuperchromaticEquipment(Entity entity, CallbackInfoReturnable<Boolean> cir)
    {
        if (entity instanceof SpectriteCompatibleMobEntity spectriteCompatibleMobEntity && spectriteCompatibleMobEntity.isSuperchromatic())
            spectriteCompatibleMobEntity.tryUpgradeEquipmentForSuperchromatic();
    }
}
