package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.component.entity.EntityComponents;
import com.flashfyre.spectrite.component.entity.SuperchromaticEntityComponent;
import com.flashfyre.spectrite.entity.SpectriteCompatibleEntity;
import com.flashfyre.spectrite.entity.SpectriteCompatibleWeaponEntity;
import com.flashfyre.spectrite.entity.player.SuperchromaticCooldownPlayerEntity;
import com.flashfyre.spectrite.item.SpectriteBowItem;
import com.flashfyre.spectrite.item.SpectriteWeaponItem;
import com.flashfyre.spectrite.util.SpectriteUtils;
import com.flashfyre.spectrite.util.SuperchromaticEntityUtils;
import com.flashfyre.spectrite.util.SuperchromaticItemUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public class PersistentProjectileEntityMixin implements SpectriteCompatibleWeaponEntity
{
    @Override
    public boolean isSuperchromatic()
    {
        final SuperchromaticEntityComponent superchromaticEntityComponent = EntityComponents.SUPERCHROMATIC_ENTITY.maybeGet(this).orElse(null);
        return superchromaticEntityComponent != null && superchromaticEntityComponent.isSuperchromatic();
    }

    @Override
    public void setSuperchromatic(boolean superchromatic)
    {
        EntityComponents.SUPERCHROMATIC_ENTITY.maybeGet(this).ifPresent(superchromaticEntityComponent -> superchromaticEntityComponent.setSuperchromatic(superchromatic));
        if (!superchromatic)
        {
            setSpectriteDamage(0);
            setSpectriteCharged(false);
        }
    }

    @Override
    public int getSpectriteDamage()
    {
        return SuperchromaticEntityUtils.getSpectriteDamage((Entity) (Object) this);
    }

    @Override
    public void setSpectriteDamage(int spectriteDamage)
    {
        SuperchromaticEntityUtils.setSpectriteDamage((Entity) (Object) this, spectriteDamage);
    }

    @Override
    public boolean isSpectriteCharged()
    {
        return SuperchromaticEntityUtils.isSpectriteCharged((Entity) (Object) this);
    }

    @Override
    public void setSpectriteCharged(boolean spectriteCharged)
    {
        SuperchromaticEntityUtils.setSpectriteCharged((Entity) (Object) this, spectriteCharged);
    }

    @Override
    public int getBaseChromaBlastLevel()
    {
        return SuperchromaticEntityUtils.getBaseChromaBlastLevel((Entity) (Object) this);
    }

    @Override
    public void setBaseChromaBlastLevel(int baseChromaBlastLevel)
    {
        SuperchromaticEntityUtils.setBaseChromaBlastLevel((Entity) (Object) this, baseChromaBlastLevel);
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;)V",
            at = @At("TAIL"))
    private void spectrite$injectSetDamageTrackedData(EntityType<? extends PersistentProjectileEntity> type, LivingEntity owner, World world, CallbackInfo ci)
    {
        if (world.isClient)
            return;
        final ItemStack mainHandStack = owner != null ? owner.getMainHandStack() : null;
        final ItemStack superchromaticWeaponStack;
        final PersistentProjectileEntity persistentProjectileEntity = (PersistentProjectileEntity) (Object) this;
        if (!(persistentProjectileEntity instanceof TridentEntity) && (mainHandStack.getItem() instanceof SpectriteBowItem
                || SuperchromaticItemUtils.isSuperchromatic(mainHandStack)))
            superchromaticWeaponStack = mainHandStack;
        else
            superchromaticWeaponStack = null;

        if (superchromaticWeaponStack != null)
        {
            final Item superchromaticWeaponItem = superchromaticWeaponStack.getItem();
            final SpectriteWeaponItem spectriteWeaponItem = superchromaticWeaponItem instanceof SpectriteWeaponItem
                    ? (SpectriteWeaponItem) superchromaticWeaponItem
                    : null;
            if (spectriteWeaponItem == null || !spectriteWeaponItem.isDepleted())
            {
                final boolean isCoolingDown = owner instanceof PlayerEntity playerEntity
                        && ((SuperchromaticCooldownPlayerEntity) playerEntity).getSuperchromaticItemCooldownManager().isCoolingDown();
                final boolean superchromaticCharged;
                final int chromaBlastLevel;
                final int passiveChromaBlastLevel;
                if (spectriteWeaponItem != null)
                {
                    superchromaticCharged = !isCoolingDown && spectriteWeaponItem.isCharged(superchromaticWeaponStack);
                    chromaBlastLevel = spectriteWeaponItem.getChromaBlastLevel();
                    passiveChromaBlastLevel = !isCoolingDown && spectriteWeaponItem.hasPassiveChromaBlast() ? 1 : 0;
                } else
                {
                    superchromaticCharged = !isCoolingDown && SuperchromaticItemUtils.isSuperchromaticCharged(superchromaticWeaponStack);
                    chromaBlastLevel = SuperchromaticItemUtils.getSuperchromaticItemChromaBlastLevel(superchromaticWeaponItem);
                    passiveChromaBlastLevel = !isCoolingDown ? SuperchromaticItemUtils.getSuperchromaticItemPassiveChromaBlastLevel(superchromaticWeaponItem) : 0;
                }
                ((SpectriteCompatibleEntity) persistentProjectileEntity).setSuperchromatic(true);
                ((SpectriteCompatibleWeaponEntity) persistentProjectileEntity).setSpectriteDamage(
                        spectriteWeaponItem != null ? SpectriteUtils.getItemStackStDamage(superchromaticWeaponStack) : 0);
                ((SpectriteCompatibleWeaponEntity) persistentProjectileEntity).setSpectriteCharged(superchromaticCharged);
                ((SpectriteCompatibleWeaponEntity) persistentProjectileEntity).setBaseChromaBlastLevel(passiveChromaBlastLevel);
                if (superchromaticCharged)
                {
                    final int power = chromaBlastLevel + passiveChromaBlastLevel + (superchromaticCharged ? 1 : 0);
                    final float stackDamageMultiplier = spectriteWeaponItem != null
                            ? spectriteWeaponItem.getStackDamageMultiplier()
                            : SuperchromaticItemUtils.getSuperchromaticWeaponItemDamageMultiplier(superchromaticWeaponItem);
                    superchromaticWeaponStack.damage((int) (Math.pow(power, 3f) * stackDamageMultiplier), owner,
                            (e) -> e.sendToolBreakStatus(e.getActiveHand()));
                    if (owner instanceof PlayerEntity playerEntity)
                        SuperchromaticItemUtils.tryActivateSuperchromaticOrSpectriteChargeableItemCooldown(playerEntity, superchromaticWeaponStack);
                } else if (superchromaticWeaponStack.getItem() instanceof BowItem)
                    superchromaticWeaponStack.damage(3, owner, (e) -> e.sendToolBreakStatus(e.getActiveHand()));
            }
        }
    }
}
