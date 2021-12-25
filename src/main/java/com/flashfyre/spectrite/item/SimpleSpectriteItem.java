package com.flashfyre.spectrite.item;

import net.minecraft.item.Item;

public class SimpleSpectriteItem extends Item implements SpectriteItem
{
    private boolean hasCustomTooltip;

    public SimpleSpectriteItem(Settings settings)
    {
        this(settings, false);
    }

    public SimpleSpectriteItem(Settings settings, boolean hasCustomTooltip)
    {
        super(settings);
        this.hasCustomTooltip = hasCustomTooltip;
    }

    @Override
    public boolean isFireproof()
    {
        return true;
    }

    public boolean hasCustomTooltip()
    {
        return hasCustomTooltip;
    }
}
