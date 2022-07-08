package com.flashfyre.spectrite.item;

import com.flashfyre.spectrite.util.SpectriteItemUtils;
import com.flashfyre.spectrite.util.SuperchromaticEntityUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpectriteShieldItem extends ShieldItem implements SpectriteDamageableItem
{
    private boolean depleted;

    public SpectriteShieldItem(boolean depleted, Settings settings)
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
    public boolean canRepair(ItemStack stack, ItemStack ingredient)
    {
        return ingredient.isOf(Items.SPECTRITE_GEM) || (!ingredient.isIn(ItemTags.PLANKS) && super.canRepair(stack, ingredient));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
    {
        SpectriteItemUtils.spectriteDamageableItemInventoryTick(stack, world, entity, slot);
        if (!entity.world.isClient && entity instanceof LivingEntity livingEntity
                && stack.isItemEqual(livingEntity.getOffHandStack()))
            SuperchromaticEntityUtils.addPassiveChromaGuardEffectIfNotPresent(livingEntity, 0);
    }
}
