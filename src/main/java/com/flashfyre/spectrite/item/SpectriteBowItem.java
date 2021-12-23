package com.flashfyre.spectrite.item;

import com.flashfyre.spectrite.util.SpectriteItemUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpectriteBowItem extends BowItem implements SpectriteWeaponItem
{
    private boolean depleted;

    public SpectriteBowItem(boolean depleted, Settings settings)
    {
        super(settings);
        this.depleted = depleted;
    }

    @Override
    public boolean isDepleted()
    {
        return depleted;
    }

    @Override
    public boolean hasPassiveChromaBlast()
    {
        return true;
    }

    @Override
    public int getChromaBlastLevel()
    {
        return 1;
    }

    @Override
    public float getStackDamageMultiplier()
    {
        return 1.5f;
    }

    @Override
    public float getCooldownMultiplier()
    {
        return 1f;
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
    }
}
