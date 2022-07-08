package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.entity.SpectriteCompatibleMobEntity;
import com.flashfyre.spectrite.util.SuperchromaticEntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin implements SpectriteCompatibleMobEntity
{
    @Shadow
    protected abstract void enchantMainHandItem(float power);

    private ServerBossBar superchromaticBossBar;

    @Override
    public boolean isSuperchromatic()
    {
        return SuperchromaticEntityUtils.isSuperchromatic((Entity) (Object) this);
    }

    @Override
    public void setSuperchromatic(boolean superchromatic)
    {
        SuperchromaticEntityUtils.setSuperchromatic((Entity) (Object) this, superchromatic);
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

    @Override
    public void tryUpgradeEquipmentForSuperchromatic()
    {
        final MobEntity mobEntity = (MobEntity) (Object) this;
        if (mobEntity instanceof SkeletonEntity)
        {
            final ItemStack mainHandStack = mobEntity.getMainHandStack();
            if (mainHandStack.getItem() == Items.BOW && mobEntity.world.random.nextInt(7) == 0)
            {
                mobEntity.setStackInHand(Hand.MAIN_HAND, new ItemStack(com.flashfyre.spectrite.item.Items.SPECTRITE_BOW));
                this.enchantMainHandItem(mobEntity.world.getLocalDifficulty(mobEntity.getBlockPos()).getClampedLocalDifficulty());
            }
        }
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
            SuperchromaticEntityUtils.initSuperchromaticMobAttributes(entity);
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void injectTickMovementAddSuperchromaticEffect(CallbackInfo ci)
    {
        final MobEntity mobEntity = (MobEntity) (Object) this;

        if (((SpectriteCompatibleMobEntity) mobEntity).isSuperchromatic())
        {
            SuperchromaticEntityUtils.addPassiveSuperchromaticEffectIfNotPresent(mobEntity);
            final int superchromaticMobPowerBonus = SuperchromaticEntityUtils.getSuperchromaticMobPowerBonus(mobEntity);
            if (superchromaticMobPowerBonus > 0)
                SuperchromaticEntityUtils.addPassiveChromaGuardEffectIfNotPresent(mobEntity, MathHelper.floor((superchromaticMobPowerBonus - 1.0f) / 2.0f));
        }
    }
}
