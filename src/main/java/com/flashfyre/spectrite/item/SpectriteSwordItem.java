package com.flashfyre.spectrite.item;

import com.flashfyre.spectrite.util.SpectriteItemUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SpectriteSwordItem extends SwordItem implements SpectriteToolItem, SpectriteWeaponItem
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
    public int getSpectriteDamageLevel()
    {
        return 3;
    }

    @Override
    public boolean isFireproof()
    {
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
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        final TypedActionResult<ItemStack> ret = SpectriteItemUtils.useSpectriteChargeableItem(user, hand, false);
        return ret.getResult() != ActionResult.PASS ? ret : super.use(world, user, hand);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
    {
        SpectriteItemUtils.stopUsingSpectriteChargeableItem(user, stack, remainingUseTicks);
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        final boolean ret = super.postHit(stack, target, attacker);
        return SpectriteItemUtils.spectriteWeaponPostHit(ret, stack, target, attacker);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
    {
        SpectriteItemUtils.spectriteDamageableItemInventoryTick(stack, world, entity, slot);
    }
}
