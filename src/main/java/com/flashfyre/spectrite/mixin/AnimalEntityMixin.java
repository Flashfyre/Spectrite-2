package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.entity.SpectriteCompatibleMobEntity;
import com.flashfyre.spectrite.util.SuperchromaticEntityUtils;
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
        boolean superchromatic;
        if (isParentSuperchromatic != ((SpectriteCompatibleMobEntity) other).isSuperchromatic())
            superchromatic = world.random.nextFloat() < (1f / 49f);
        else
            superchromatic = isParentSuperchromatic && world.random.nextFloat() < (1f / 7f);
        if (superchromatic || SuperchromaticEntityUtils.trySetMobSuperchromatic(passiveEntity))
            SuperchromaticEntityUtils.initSuperchromaticMobAttributes(passiveEntity);
    }
}
