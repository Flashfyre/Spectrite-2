package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.entity.SpectriteCompatibleWeaponEntity;
import com.flashfyre.spectrite.util.SpectriteUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntity.class)
public class ProjectileEntityMixin
{
    @Inject(method = "onCollision", at = @At("TAIL"))
    private void injectOnCollisionDiscardSpectriteProperties(HitResult hitResult, CallbackInfo ci)
    {
        if (hitResult.getType() != HitResult.Type.MISS)
        {
            final ProjectileEntity projectileEntity = (ProjectileEntity) (Object) this;
            if (!projectileEntity.world.isClient && !(projectileEntity instanceof TridentEntity)
                    && projectileEntity instanceof SpectriteCompatibleWeaponEntity spectriteCompatibleWeaponEntity
                    && spectriteCompatibleWeaponEntity.isSpectriteEntity())
            {
                final Entity target;
                if (hitResult instanceof EntityHitResult entityHitResult)
                {
                    if (entityHitResult.getEntity() instanceof LivingEntity livingEntity)
                        livingEntity.hurtTime = 0;
                    target = entityHitResult.getEntity();
                } else
                    target = null;
                final int power = 1 + (spectriteCompatibleWeaponEntity.isSpectriteCharged() ? 1 : 0);
                SpectriteUtils.newSpectriteExplosion(projectileEntity.world, projectileEntity,
                        target != null && target instanceof EndermanEntity ? null : target,
                        null, hitResult.getPos().x, hitResult.getPos().y, hitResult.getPos().z,
                        power, false, Explosion.DestructionType.NONE);
                if (hitResult.getType() == HitResult.Type.BLOCK)
                    projectileEntity.discard();
            }
        }
    }
}
