package com.flashfyre.spectrite.item;

import com.flashfyre.spectrite.util.SpectriteItemUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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
        super.inventoryTick(stack, world, entity, slot, selected);
        SpectriteItemUtils.spectriteDamageableItemInventoryTick(stack, world, entity, slot);
    }
}
