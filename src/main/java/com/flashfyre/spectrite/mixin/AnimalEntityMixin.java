package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.entity.SpectriteCompatibleMobEntity;
import com.flashfyre.spectrite.util.SpectriteEntityUtils;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AnimalEntity.class)
public class AnimalEntityMixin
{
    @Inject(method = "breed", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/advancement/criterion/BredAnimalsCriterion;trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/entity/passive/AnimalEntity;Lnet/minecraft/entity/passive/AnimalEntity;Lnet/minecraft/entity/passive/PassiveEntity;)V"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void injectBreedCriteriaBredAnimalsTriggerTrySetSuperchromaticEntity(ServerWorld world, AnimalEntity other, CallbackInfo ci,
                                                                                 PassiveEntity passiveEntity)
    {
        final AnimalEntity parent = (AnimalEntity) (Object) this;
        final boolean isParentSuperchromatic = ((SpectriteCompatibleMobEntity) parent).isSuperchromatic();
        if (isParentSuperchromatic != ((SpectriteCompatibleMobEntity) other).isSuperchromatic())
        {
            if (world.random.nextFloat() < (1f / 49f))
                SpectriteEntityUtils.setSuperchromatic(passiveEntity, true);
        } else if (isParentSuperchromatic && world.random.nextFloat() < (1f / 7f))
            SpectriteEntityUtils.setSuperchromatic(passiveEntity, true);
    }
}
