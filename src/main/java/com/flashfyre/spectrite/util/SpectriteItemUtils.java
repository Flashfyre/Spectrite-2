package com.flashfyre.spectrite.util;

import com.flashfyre.spectrite.item.Items;
import com.flashfyre.spectrite.item.SpectriteDamageableItem;
import com.flashfyre.spectrite.mixin.PlayerInventoryAccessor;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

public class SpectriteItemUtils
{
    public static void spectriteDamageableItemInventoryTick(ItemStack stack, World world, Entity entity, int slot)
    {
        if (((SpectriteDamageableItem) stack.getItem()).isDepleted())
        {
            final int newDamage = stack.getDamage() - 1;
            if (newDamage > 0)
            {
                if (stack.getDamage() > 0 && world.getTime() % 13 == 0)
                    stack.setDamage(newDamage);
            } else
            {
                if (entity instanceof PlayerEntity playerEntity)
                {
                    for (DefaultedList<ItemStack> inventory : ((PlayerInventoryAccessor) playerEntity.getInventory()).getCombinedInventory())
                    {
                        if (inventory.size() > slot && inventory.get(slot) == stack)
                        {
                            final Item depletedItem = stack.getItem();
                            final Item originalItem = Items.DEPLETED_DAMAGEABLE_ITEMS_MAP.entrySet().stream()
                                    .filter(e -> e.getValue() == depletedItem).map(e -> e.getKey()).findFirst().orElse(null);

                            if (originalItem != null)
                            {
                                final ItemStack restoredStack = new ItemStack(originalItem);
                                if (stack.hasNbt())
                                    restoredStack.setNbt(stack.getNbt());
                                restoredStack.setDamage(1);
                                inventory.set(slot, restoredStack);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void appendSpectriteDamageableItemTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context)
    {
        if (stack.getItem() instanceof SpectriteDamageableItem spectriteDamageableItem && spectriteDamageableItem.isDepleted())
            tooltip.add(new TranslatableText("item.spectrite.tooltip.depleted").formatted(Formatting.GRAY));
    }
}
