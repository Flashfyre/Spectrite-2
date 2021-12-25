package com.flashfyre.spectrite.item;

public interface SpectriteItem
{
    default boolean hasCustomTooltip()
    {
        return false;
    }
}
