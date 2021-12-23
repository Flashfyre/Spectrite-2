package com.flashfyre.spectrite.item;

import com.flashfyre.spectrite.util.SpectriteItemUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpectritePickaxeItem extends PickaxeItem implements SpectriteToolItem, SpectriteMeleeWeaponItem
{
    private final boolean depleted;

    protected SpectritePickaxeItem(ToolMaterial material, int attackDamage, float attackSpeed, boolean depleted, Settings settings)
    {
        super(material, attackDamage, attackSpeed, settings);
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
        return 2;
    }

    @Override
    public float getStackDamageMultiplier()
    {
        return 12f;
    }

    @Override
    public float getCooldownMultiplier()
    {
        return 0.75f;
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
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner)
    {
        if (!world.isClient && state.getHardness(world, pos) != 0.0F)
            stack.damage(isCharged(stack) ? 2 : 1, miner, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));

        return true;
    }

    @Override
    public int getMaxUseTime(ItemStack stack)
    {
        return 72000;
    }

    @Override
    public boolean isUsedOnRelease(ItemStack stack)
    {
        return true;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
    {
        SpectriteItemUtils.spectriteDamageableItemInventoryTick(stack, world, entity, slot);
    }
}
