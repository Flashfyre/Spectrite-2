package com.flashfyre.spectrite.item;

import com.flashfyre.spectrite.mixin.LivingEntityAccessor;
import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

import java.util.function.Consumer;

public class SpectriteDamageableDamageHandler implements CustomDamageHandler
{
    public int damage(ItemStack stack, int amount, LivingEntity entity, Consumer<LivingEntity> breakCallback)
    {
        if (stack.isDamageable() && stack.getDamage() + amount >= stack.getMaxDamage())
        {
            final int ret = (stack.getMaxDamage() - 1) - stack.getDamage();
            if (!((SpectriteDamageableItem) stack.getItem()).isDepleted() && (!(entity instanceof ServerPlayerEntity player) || !player.isCreative()))
            {
                final Item item = stack.getItem();
                final Item depletedItem = Items.DEPLETED_DAMAGEABLE_ITEMS_MAP.get(stack.getItem());
                if (depletedItem != null)
                {
                    final ItemStack depletedStack = new ItemStack(depletedItem);
                    if (stack.hasTag())
                        depletedStack.setTag(stack.getTag());
                    if (item instanceof SpectriteChargeableItem)
                    {
                        if (entity.getStackInHand(Hand.MAIN_HAND) == stack)
                        {
                            entity.setStackInHand(Hand.MAIN_HAND, depletedStack);
                            return ret;
                        } else if (item instanceof SpectriteTridentItem)
                            return ret;
                    } else if (item instanceof SpectriteArmorItem armorItem)
                    {
                        final EquipmentSlot armorSlot = armorItem.getSlotType();
                        if (entity.getEquippedStack(armorSlot) == stack)
                        {
                            ((LivingEntityAccessor) entity).invokeSetArmorInSlot(armorSlot, depletedStack);
                            return ret;
                        }
                    }
                }
                stack.decrement(1);
                return 0;
            }

            return ret;
        }

        return amount;
    }
}
