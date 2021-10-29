package com.flashfyre.spectrite.item;

import net.minecraft.item.NetherStarItem;

public class SuperchromaticNetherStarItem extends NetherStarItem implements SpectriteItem
{
    public SuperchromaticNetherStarItem(Settings settings)
    {
        super(settings);
    }

    @Override
    public boolean isFireproof()
    {
        return true;
    }
}
