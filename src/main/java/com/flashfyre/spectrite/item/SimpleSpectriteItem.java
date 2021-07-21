package com.flashfyre.spectrite.item;

import net.minecraft.item.Item;

public class SimpleSpectriteItem extends Item implements SpectriteItem
{
    public SimpleSpectriteItem(Settings settings)
    {
        super(settings);
    }

    @Override
    public boolean isFireproof()
    {
        return true;
    }
}
