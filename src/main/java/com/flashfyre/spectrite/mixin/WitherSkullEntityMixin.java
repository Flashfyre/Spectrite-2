package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.entity.SpectriteCompatibleEntity;
import com.flashfyre.spectrite.entity.SpectriteCompatibleWeaponEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitherSkullEntity.class)
public class WitherSkullEntityMixin implements SpectriteCompatibleWeaponEntity
{
    @Override
    public boolean isSuperchromatic()
    {
        final Entity owner = ((ProjectileEntityAccessor) this).getOwner();
        return owner != null && owner instanceof SpectriteCompatibleEntity spectriteEntity && spectriteEntity.isSuperchromatic();
    }

    @Override
    public void setSuperchromatic(boolean superchromatic)
    {
    }

    @Override
    public int getSpectriteDamage()
    {
        return 0;
    }

    @Override
    public void setSpectriteDamage(int spectriteDamage)
    {
    }

    @Override
    public boolean isSpectriteCharged()
    {
        return ((WitherSkullEntity) (Object) this).isCharged();
    }

    @Override
    public void setSpectriteCharged(boolean spectriteCharged)
    {
    }

    @Override
    public int getBaseChromaBlastLevel()
    {
        return 0;
    }

    @Override
    public void setBaseChromaBlastLevel(int baseChromaBlastLevel)
    {
    }

    @Inject(method = "onCollision", at = @At(value = "FIELD",
            target = "Lnet/minecraft/entity/projectile/WitherSkullEntity;world:Lnet/minecraft/world/World;"),
            cancellable = true)
    private void injectOnCollisionWorldIgnoreDefaultExplosion(HitResult hitResult, CallbackInfo ci)
    {
        if (((SpectriteCompatibleWeaponEntity) this).isSuperchromatic())
            ci.cancel();
    }
}
