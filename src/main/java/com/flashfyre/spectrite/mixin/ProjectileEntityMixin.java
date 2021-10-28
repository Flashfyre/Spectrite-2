package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.entity.SpectriteCompatibleWeaponEntity;
import com.flashfyre.spectrite.entity.effect.StatusEffects;
import com.flashfyre.spectrite.util.SpectriteUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntity.class)
public class ProjectileEntityMixin
{
    @Inject(method = "onCollision", at = @At("TAIL"))
    private void injectOnCollisionSpectriteExplosion(HitResult hitResult, CallbackInfo ci)
    {
        if (hitResult.getType() != HitResult.Type.MISS)
        {
            final ProjectileEntity projectileEntity = (ProjectileEntity) (Object) this;
            if (!projectileEntity.world.isClient && !(projectileEntity instanceof TridentEntity)
                    && projectileEntity instanceof SpectriteCompatibleWeaponEntity spectriteCompatibleWeaponEntity
                    && spectriteCompatibleWeaponEntity.isSuperchromatic())
            {
                final Entity target;
                if (hitResult instanceof EntityHitResult entityHitResult)
                {
                    if (entityHitResult.getEntity() instanceof LivingEntity livingEntity)
                    {
                        if (livingEntity.getType() == EntityType.ENDERMAN)
                            return;
                        livingEntity.timeUntilRegen = 0;
                        livingEntity.hurtTime = 0;
                    }
                    target = entityHitResult.getEntity();
                } else
                    target = null;
                int power = 1 + (spectriteCompatibleWeaponEntity.isSpectriteCharged() ? 1 : 0);
                final int superchromaticLevel = projectileEntity.getOwner() instanceof LivingEntity ownerEntity
                        && ownerEntity.hasStatusEffect(StatusEffects.SUPERCHROMATIC)
                        ? ownerEntity.getStatusEffect(StatusEffects.SUPERCHROMATIC).getAmplifier() + 1
                        : 0;
                final boolean isWitherSkull = projectileEntity instanceof WitherSkullEntity;
                final Explosion.DestructionType destructionType;
                if (isWitherSkull)
                {
                    destructionType = projectileEntity.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE;
                    power++;
                } else
                    destructionType = Explosion.DestructionType.NONE;
                SpectriteUtils.newChromaBlast(projectileEntity.world, projectileEntity,
                        target != null && target.getType() == EntityType.ENDERMAN ? null : target,
                        null, projectileEntity.getX(), projectileEntity.getY(), projectileEntity.getZ(),
                        power + superchromaticLevel, false, destructionType);
                if (hitResult.getType() == HitResult.Type.BLOCK || isWitherSkull)
                    projectileEntity.discard();
            }
        }
    }
}
