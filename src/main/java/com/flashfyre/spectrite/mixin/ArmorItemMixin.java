package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.item.SpectriteArmorItem;
import com.flashfyre.spectrite.util.SuperchromaticEntityUtils;
import com.flashfyre.spectrite.util.SuperchromaticItemUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Iterator;

@Mixin(ArmorItem.class)
public class ArmorItemMixin
{
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
    {
        final ArmorItem armorItem = (ArmorItem) (Object) this;
        if (!entity.world.isClient && entity instanceof LivingEntity livingEntity && armorItem.getSlotType() == EquipmentSlot.CHEST)
        {
            final Iterator<ItemStack> entityArmorIterator = livingEntity.getArmorItems().iterator();
            int superchromaticArmorCount = 0;
            while (entityArmorIterator.hasNext())
            {
                final ItemStack armorItemStack = entityArmorIterator.next();
                if (!armorItemStack.isEmpty()
                        && ((armorItemStack.getItem() instanceof SpectriteArmorItem spectriteArmorItem && !spectriteArmorItem.isDepleted())
                        || SuperchromaticItemUtils.isSuperchromatic(armorItemStack)))
                    superchromaticArmorCount++;
            }
            if (superchromaticArmorCount >= 4)
                SuperchromaticEntityUtils.addPassiveSuperchromaticEffectIfNotPresent(livingEntity);
        }
    }
}
