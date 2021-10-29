package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.entity.SpectriteCompatibleMobEntity;
import com.flashfyre.spectrite.entity.SpectriteCompatibleWeaponEntity;
import com.flashfyre.spectrite.entity.effect.StatusEffects;
import com.flashfyre.spectrite.util.SpectriteEntityUtils;
import com.flashfyre.spectrite.util.SpectriteUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
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
    private void injectOnCollisionChromaBlast(HitResult hitResult, CallbackInfo ci)
    {
        if (hitResult.getType() != HitResult.Type.MISS)
        {
            final ProjectileEntity projectileEntity = (ProjectileEntity) (Object) this;
            if (!projectileEntity.world.isClient)
            {
                if (projectileEntity instanceof EnderPearlEntity || projectileEntity instanceof PotionEntity || projectileEntity instanceof ExperienceBottleEntity)
                    return;
                final Entity target;
                final SpectriteCompatibleWeaponEntity spectriteCompatibleWeaponEntity = projectileEntity instanceof SpectriteCompatibleWeaponEntity
                        ? (SpectriteCompatibleWeaponEntity) projectileEntity
                        : null;
                final boolean isTrident = spectriteCompatibleWeaponEntity != null && spectriteCompatibleWeaponEntity instanceof TridentEntity;
                final boolean isSuperchromatic = spectriteCompatibleWeaponEntity != null && spectriteCompatibleWeaponEntity.isSuperchromatic();
                // Charged Spectite Trident logic is handled in TridentEntityMixin
                if (hitResult.getType() == HitResult.Type.ENTITY && (isTrident && isSuperchromatic && spectriteCompatibleWeaponEntity.isSpectriteCharged()))
                    return;
                final LivingEntity ownerEntity = projectileEntity.getOwner() instanceof LivingEntity
                        ? (LivingEntity) projectileEntity.getOwner()
                        : null;
                final int superchromaticLevel = ownerEntity != null
                        && ownerEntity.hasStatusEffect(StatusEffects.SUPERCHROMATIC)
                        ? ownerEntity.getStatusEffect(StatusEffects.SUPERCHROMATIC).getAmplifier() + 1
                        : 0;
                final int superchromaticMobPowerBonus = ownerEntity instanceof SpectriteCompatibleMobEntity spectriteCompatibleMobEntity
                        && spectriteCompatibleMobEntity.isSuperchromatic()
                        ? SpectriteEntityUtils.getSuperchromaticMobPowerBonus(((MobEntity) ownerEntity))
                        : 0;
                int power = isSuperchromatic ? 1 + (spectriteCompatibleWeaponEntity.isSpectriteCharged() ? 1 : 0) : 0;

                final boolean isWitherSkull = projectileEntity instanceof WitherSkullEntity;
                final Explosion.DestructionType destructionType;
                if (isWitherSkull)
                {
                    destructionType = projectileEntity.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)
                            ? Explosion.DestructionType.DESTROY
                            : Explosion.DestructionType.NONE;
                    power--;
                } else
                {
                    destructionType = Explosion.DestructionType.NONE;
                    if (isTrident && isSuperchromatic && spectriteCompatibleWeaponEntity.isSpectriteCharged())
                        power--;
                }

                if (power + superchromaticLevel == 0)
                    return;

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

                SpectriteUtils.newChromaBlast(projectileEntity.world, projectileEntity,
                        target != null && target.getType() == EntityType.ENDERMAN ? null : target,
                        null, projectileEntity.getX(), projectileEntity.getY(), projectileEntity.getZ(),
                        power + superchromaticLevel + superchromaticMobPowerBonus, false, destructionType);
                if ((isSuperchromatic && !isTrident) && (hitResult.getType() == HitResult.Type.BLOCK || isWitherSkull))
                    projectileEntity.discard();
            }
        }
    }
}
