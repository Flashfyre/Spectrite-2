package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.component.Components;
import com.flashfyre.spectrite.component.SpectriteEntityComponent;
import com.flashfyre.spectrite.entity.SpectriteCompatibleEntity;
import com.flashfyre.spectrite.entity.SpectriteCompatibleWeaponEntity;
import com.flashfyre.spectrite.item.SpectriteBowItem;
import com.flashfyre.spectrite.item.SpectriteWeaponItem;
import com.flashfyre.spectrite.util.SpectriteEntityUtils;
import com.flashfyre.spectrite.util.SpectriteUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
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
    public boolean isSpectriteEntity()
    {
        final SpectriteEntityComponent spectriteEntityComponent = Components.SPECTRITE_ENTITY.maybeGet(this).orElse(null);
        return spectriteEntityComponent != null && spectriteEntityComponent.isSpectrite();
    }

    @Override
    public void setSpectriteEntity(boolean spectriteEntity)
    {
        Components.SPECTRITE_ENTITY.maybeGet(this).ifPresent(spectriteEntityComponent -> spectriteEntityComponent.setSpectrite(spectriteEntity));
        if (!spectriteEntity)
        {
            setSpectriteDamage(0);
            setSpectriteCharged(false);
        }
    }

    @Override
    public int getSpectriteDamage()
    {
        return SpectriteEntityUtils.getSpectriteDamage((Entity) (Object) this);
    }

    @Override
    public void setSpectriteDamage(int spectriteDamage)
    {
        SpectriteEntityUtils.setSpectriteDamage((Entity) (Object) this, spectriteDamage);
    }

    @Override
    public boolean isSpectriteCharged()
    {
        return SpectriteEntityUtils.isSpectriteCharged((Entity) (Object) this);
    }

    @Override
    public void setSpectriteCharged(boolean spectriteCharged)
    {
        SpectriteEntityUtils.setSpectriteCharged((Entity) (Object) this, spectriteCharged);
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;)V",
            at = @At("TAIL"))
    private void spectrite$injectSetDamageTrackedData(EntityType<? extends PersistentProjectileEntity> type, LivingEntity owner, World world, CallbackInfo ci)
    {
        if (world.isClient)
            return;
        final ItemStack mainHandStack = owner != null ? owner.getMainHandStack() : null;
        final ItemStack spectriteWeaponStack;
        final PersistentProjectileEntity persistentProjectileEntity = (PersistentProjectileEntity) (Object) this;
        if (!(persistentProjectileEntity instanceof TridentEntity) && mainHandStack.getItem() instanceof SpectriteBowItem)
            spectriteWeaponStack = mainHandStack;
        else
            spectriteWeaponStack = null;

        if (spectriteWeaponStack != null)
        {
            final boolean spectriteCharged = ((SpectriteWeaponItem) spectriteWeaponStack.getItem()).isCharged(spectriteWeaponStack);
            final int power = 1 + (spectriteCharged ? 1 : 0);
            ((SpectriteCompatibleEntity) persistentProjectileEntity).setSpectriteEntity(true);
            ((SpectriteCompatibleWeaponEntity) persistentProjectileEntity).setSpectriteDamage(
                    SpectriteUtils.getItemStackStDamage(spectriteWeaponStack));
            ((SpectriteCompatibleWeaponEntity) persistentProjectileEntity).setSpectriteCharged(spectriteCharged);
            if (spectriteCharged)
            {
                spectriteWeaponStack.damage((int) Math.pow(power, 3f), owner, (e) ->
                {
                });
                if (owner instanceof PlayerEntity playerEntity)
                    SpectriteUtils.tryActivateSpectriteChargeableItemCooldown(playerEntity, spectriteWeaponStack);
            }
        }
    }
}
