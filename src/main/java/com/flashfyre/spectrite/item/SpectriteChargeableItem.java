package com.flashfyre.spectrite.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public interface SpectriteChargeableItem extends SpectriteDamageableItem
{
    default boolean isCharged(ItemStack stack)
    {
        if (isDepleted())
            return false;
        final NbtCompound nbtCompound = stack.getNbt();
        return nbtCompound != null && nbtCompound.getBoolean("Charged");
    }

    default void setCharged(ItemStack stack, boolean charged)
    {
        final NbtCompound nbtCompound = stack.getOrCreateNbt();
        nbtCompound.putBoolean("Charged", charged);
    }
}
