package com.flashfyre.spectrite.item;

import net.minecraft.item.HorseArmorItem;

public class SpectriteHorseArmorItem extends HorseArmorItem implements SpectriteItem
{
    public SpectriteHorseArmorItem(int bonus, Settings settings)
    {
        super(bonus, "spectrite", settings);
    }

    @Override
    public boolean isFireproof()
    {
        return true;
    }
}
