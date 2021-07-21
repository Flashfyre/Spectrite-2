package com.flashfyre.spectrite.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public interface SpectriteChargeableItem extends SpectriteDamageableItem
{
    default boolean isCharged(ItemStack stack)
    {
        final NbtCompound nbtCompound = stack.getTag();
        return nbtCompound != null && nbtCompound.getBoolean("Charged");
    }

    default void setCharged(ItemStack stack, boolean charged)
    {
        final NbtCompound nbtCompound = stack.getOrCreateTag();
        nbtCompound.putBoolean("Charged", charged);
    }
}
