package com.flashfyre.spectrite.item;

public interface SpectriteWeaponItem extends SpectriteChargeableItem
{
    boolean hasPassiveChromaBlast();

    int getChromaBlastLevel();

    float getStackDamageMultiplier();

    float getCooldownMultiplier();
}
