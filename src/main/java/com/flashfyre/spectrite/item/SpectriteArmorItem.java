package com.flashfyre.spectrite.item;

import com.flashfyre.spectrite.util.SpectriteEntityUtils;
import com.flashfyre.spectrite.util.SpectriteItemUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class SpectriteArmorItem extends ArmorItem implements SpectriteDamageableItem
{
    private boolean depleted;

    public SpectriteArmorItem(ArmorMaterial material, EquipmentSlot slot, boolean depleted, Settings settings)
    {
        super(material, slot, settings);
        this.depleted = depleted;
    }

    @Override
    public boolean isDepleted()
    {
        return depleted;
    }

    @Override
    public boolean isFireproof()
    {
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
    {
        SpectriteItemUtils.appendSpectriteDamageableItemTooltip(stack, world, tooltip, context);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
    {
        SpectriteItemUtils.spectriteDamageableItemInventoryTick(stack, world, entity, slot);
        if (!entity.world.isClient && entity instanceof LivingEntity livingEntity && getSlotType() == EquipmentSlot.CHEST)
        {
            final Iterator<ItemStack> entityArmorIterator = entity.getArmorItems().iterator();
            int spectriteArmorCount = 0;
            while (entityArmorIterator.hasNext())
            {
                final ItemStack armorItemStack = entityArmorIterator.next();
                if (!armorItemStack.isEmpty() && armorItemStack.getItem() instanceof SpectriteArmorItem)
                    spectriteArmorCount++;
            }
            if (spectriteArmorCount >= 4)
                SpectriteEntityUtils.addPassiveSuperchromaticEffectIfNotPresent(livingEntity);
        }
    }
}
