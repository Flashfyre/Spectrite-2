package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.entity.SpectriteCompatibleMobEntity;
import com.flashfyre.spectrite.util.SpectriteEntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(MobEntity.class)
public class MobEntityMixin implements SpectriteCompatibleMobEntity
{
    private ServerBossBar superchromaticBossBar;

    @Override
    public boolean isSuperchromatic()
    {
        return SpectriteEntityUtils.isSuperchromatic((Entity) (Object) this);
    }

    @Override
    public void setSuperchromatic(boolean superchromatic)
    {
        SpectriteEntityUtils.setSuperchromatic((Entity) (Object) this, superchromatic);
    }

    @Override
    public ServerBossBar getSuperchromaticBossBar()
    {
        return superchromaticBossBar;
    }

    @Override
    public void setSuperchromaticBossBar(ServerBossBar superchromaticBossBar)
    {
        this.superchromaticBossBar = superchromaticBossBar;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectSpectriteBossBar(EntityType<? extends MobEntity> entityType, World world, CallbackInfo ci)
    {
        final MobEntity entity = (MobEntity) (Object) this;
        if (((SpectriteCompatibleMobEntity) entity).isSuperchromatic() && entity instanceof Monster)
            superchromaticBossBar = (new ServerBossBar(entity.getDisplayName(),
                    BossBar.Color.PURPLE, BossBar.Style.PROGRESS));
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void injectReadCustomDataFromNbtSetSpectriteBossBarName(NbtCompound nbt, CallbackInfo ci)
    {
        final MobEntity entity = (MobEntity) (Object) this;
        if (entity.hasCustomName() && superchromaticBossBar != null)
            superchromaticBossBar.setName(entity.getDisplayName());
    }

    @Inject(method = "mobTick", at = @At("TAIL"))
    private void injectMobTickUpdateSuperchromaticBossBar(CallbackInfo ci)
    {
        final MobEntity entity = (MobEntity) (Object) this;
        if (superchromaticBossBar != null)
            superchromaticBossBar.setPercent(entity.getHealth() / entity.getMaxHealth());
    }

    @Inject(method = "initialize", at = @At("TAIL"))
    private void injectInitializeSuperchromaticMob(ServerWorldAccess world, LocalDifficulty difficulty,
                                                   SpawnReason spawnReason, EntityData entityData,
                                                   NbtCompound entityNbt, CallbackInfoReturnable<EntityData> cir)
    {
        final MobEntity entity = (MobEntity) (Object) this;
        if (((SpectriteCompatibleMobEntity) entity).isSuperchromatic())
        {
            final float initialMaxHealth = entity.getMaxHealth();
            final float healthRatio = entity.getHealth() / initialMaxHealth;
            final AttributeContainer attributes = entity.getAttributes();
            for (Map.Entry<EntityAttribute, Map.Entry<Supplier<Double>, Supplier<Double>>> e : SpectriteEntityUtils.ENTITY_ATTRIBUTE_MODIFIERS.entrySet())
            {
                final EntityAttribute attribute = e.getKey();
                if (attributes.hasAttribute(attribute))
                {
                    final Double bonus = e.getValue().getKey().get();
                    final Double multiplier = e.getValue().getValue().get();
                    if (bonus != null && bonus.doubleValue() > 0.0d)
                        entity.getAttributeInstance(attribute).addPersistentModifier(
                                new EntityAttributeModifier("Superchromatic mob bonus", bonus, EntityAttributeModifier.Operation.ADDITION));
                    if (multiplier != null && multiplier.doubleValue() != 1.0d)
                        entity.getAttributeInstance(attribute).addPersistentModifier(
                                new EntityAttributeModifier("Superchromatic mob multiplier", multiplier - 1.0d, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
                }
            }
            final float currentMaxHealth = entity.getMaxHealth();
            if (currentMaxHealth > initialMaxHealth)
                entity.setHealth(currentMaxHealth * healthRatio);
        }
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void injectTickMovementAddSuperchromaticEffect(CallbackInfo ci)
    {
        final MobEntity mobEntity = (MobEntity) (Object) this;

        if (((SpectriteCompatibleMobEntity) mobEntity).isSuperchromatic())
            SpectriteEntityUtils.addPassiveSuperchromaticEffectIfNotPresent(mobEntity);
    }
}
