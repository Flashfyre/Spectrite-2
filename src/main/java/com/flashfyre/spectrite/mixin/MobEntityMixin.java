package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.SpectriteConfig;
import com.flashfyre.spectrite.client.particle.Particles;
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
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
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
    private ServerBossBar spectrite$spectriteBossBar;

    @Override
    public boolean isSpectriteEntity()
    {
        return SpectriteEntityUtils.isSpectriteEntity((Entity) (Object) this);
    }

    @Override
    public void setSpectriteEntity(boolean spectriteEntity)
    {
        SpectriteEntityUtils.setSpectriteEntity((Entity) (Object) this, spectriteEntity);
    }

    @Override
    public ServerBossBar getSpectriteBossBar()
    {
        return spectrite$spectriteBossBar;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectSpectriteEntity(EntityType<?> type, World world, CallbackInfo ci)
    {
        if (world instanceof ServerWorld)
        {
            final MobEntity entity = (MobEntity) ((Object) this);
            if (entity instanceof EnderDragonEntity)
                return;
            final boolean isCrystalInRange = false;//SpectriteUtils.isCrystalInRange(e.getWorld(), entity.getPosition());
            final float spectriteMobSpawnRate = SpectriteConfig.getSpectriteMobSpawnRate();
            final float spectriteMobCrystalSpawnRate = SpectriteConfig.getSpectriteMobCrystalSpawnRate();
            final long leastSignificantBits = Math.abs(entity.getUuid().getLeastSignificantBits());
            if ((!isCrystalInRange && spectriteMobSpawnRate > 0f
                    && (int) (leastSignificantBits % (long) (100l / spectriteMobSpawnRate)) == 0)
                    || (isCrystalInRange && spectriteMobCrystalSpawnRate > 0f
                    && (int) (leastSignificantBits % (long) (100l / spectriteMobCrystalSpawnRate)) == 0))
                setSpectriteEntity(true);
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectSpectriteBossBar(EntityType<? extends MobEntity> entityType, World world, CallbackInfo ci)
    {
        final MobEntity entity = (MobEntity) (Object) this;
        if (((SpectriteCompatibleMobEntity) entity).isSpectriteEntity() && entity instanceof Monster)
            spectrite$spectriteBossBar = (new ServerBossBar(entity.getDisplayName(),
                    BossBar.Color.PURPLE, BossBar.Style.PROGRESS));
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void injectReadCustomDataFromNbtSetSpectriteBossBarName(NbtCompound nbt, CallbackInfo ci)
    {
        final MobEntity entity = (MobEntity) (Object) this;
        if (entity.hasCustomName() && spectrite$spectriteBossBar != null)
            spectrite$spectriteBossBar.setName(entity.getDisplayName());
    }

    @Inject(method = "mobTick", at = @At("TAIL"))
    private void injectMobTickUpdateSpectriteBossBar(CallbackInfo ci)
    {
        final MobEntity entity = (MobEntity) (Object) this;
        if (spectrite$spectriteBossBar != null)
            spectrite$spectriteBossBar.setPercent(entity.getHealth() / entity.getMaxHealth());
    }

    @Inject(method = "initialize", at = @At("TAIL"))
    private void injectInitializeSpectriteMob(ServerWorldAccess world, LocalDifficulty difficulty,
                                              SpawnReason spawnReason, EntityData entityData,
                                              NbtCompound entityNbt, CallbackInfoReturnable<EntityData> cir)
    {
        final MobEntity entity = (MobEntity) (Object) this;
        if (((SpectriteCompatibleMobEntity) entity).isSpectriteEntity())
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
                                new EntityAttributeModifier("Spectrite mob bonus", bonus, EntityAttributeModifier.Operation.ADDITION));
                    if (multiplier != null && multiplier.doubleValue() != 1.0d)
                        entity.getAttributeInstance(attribute).addPersistentModifier(
                                new EntityAttributeModifier("Spectrite mob multiplier", multiplier - 1.0d, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
                }
            }
            final float currentMaxHealth = entity.getMaxHealth();
            if (currentMaxHealth > initialMaxHealth)
                entity.setHealth(currentMaxHealth * healthRatio);
        }
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void injectTickMovementSpectriteGlowParticle(CallbackInfo ci)
    {
        final MobEntity mobEntity = (MobEntity) (Object) this;

        if (((SpectriteCompatibleMobEntity) mobEntity).isSpectriteEntity() && mobEntity.world.getTime() % 7 == 0)
            mobEntity.world.addParticle(Particles.SPECTRITE_GLOW,
                    mobEntity.getParticleX(0.6D), mobEntity.getRandomBodyY(), mobEntity.getParticleZ(0.6D),
                    0.0D, 0.0D, 0.0D);
    }
}
