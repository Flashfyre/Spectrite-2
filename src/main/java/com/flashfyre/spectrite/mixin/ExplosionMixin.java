package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.etc.SpectriteDamageTargetType;
import com.flashfyre.spectrite.etc.SpectriteExplosion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(Explosion.class)
public class ExplosionMixin
{
    @Shadow
    @Final
    @Nullable
    private Entity entity;

    @ModifyVariable(method = "collectBlocksAndDamageEntities", at = @At(value = "STORE",
            target = "Lnet/minecraft/world/World;getOtherEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Ljava/util/List;"))
    private List<Entity> spectrite$modifyVariableCollectBlocksAndDamageEntitiesList(List<Entity> list)
    {
        if (((Object) this) instanceof SpectriteExplosion spectriteExplosion)
        {
            Entity entity = this.entity;
            if (entity instanceof ProjectileEntity projectileEntity && projectileEntity.getOwner() != null)
            {
                entity = projectileEntity.getOwner();
                list.remove(entity);
            }

            final Entity sourceEntity = entity;

            if (sourceEntity != null)
            {
                final SpectriteDamageTargetType sourceEntityTargetType = SpectriteDamageTargetType.getEntityTargetType(sourceEntity);
                if (sourceEntityTargetType == SpectriteDamageTargetType.OTHER)
                    return list;

                final Entity targetEntity = spectriteExplosion.getTargetEntity();
                if (targetEntity != null && SpectriteDamageTargetType.getEntityTargetType(targetEntity) == sourceEntityTargetType)
                    return list;

                list.removeIf(e -> (targetEntity == null || e != targetEntity)
                        && (SpectriteDamageTargetType.getCollateralEntityTargetType(sourceEntity, e) == sourceEntityTargetType));
            }
        }

        return list;
    }

    @ModifyArg(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), index = 1)
    private float spectrite$injectCollectBlocksAndDamageEntitiesDamage(float amount)
    {
        if (((Object) this) instanceof SpectriteExplosion)
            return amount / 2f;

        return amount;
    }
}
