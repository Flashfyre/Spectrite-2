package com.flashfyre.spectrite.item;

import com.flashfyre.spectrite.util.SpectriteItemUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpectriteSwordItem extends SwordItem implements SpectriteToolItem, SpectriteMeleeWeaponItem
{
    private boolean depleted;

    public SpectriteSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, boolean depleted, Settings settings)
    {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        this.depleted = depleted;
    }

    @Override
    public boolean isDepleted()
    {
        return depleted;
    }

    @Override
    public float getChargedEfficiencyMultiplier()
    {
        return 1.5f;
    }

    @Override
    public boolean hasPassiveChromaBlast()
    {
        return false;
    }

    @Override
    public int getChromaBlastLevel()
    {
        return 3;
    }

    @Override
    public float getStackDamageMultiplier()
    {
        return 1f;
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
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state)
    {
        float ret = super.getMiningSpeedMultiplier(stack, state);

        if (ret > 1.0f && ((SpectriteToolItem) stack.getItem()).isCharged(stack))
            ret *= getChargedEfficiencyMultiplier();

        return ret;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
    {
        SpectriteItemUtils.spectriteDamageableItemInventoryTick(stack, world, entity, slot);
    }
}
