package com.flashfyre.spectrite.item;

public interface SpectriteWeaponItem extends SpectriteChargeableItem
{
    int getSpectriteDamageLevel();

    float getStackDamageMultiplier();

    float getCooldownMultiplier();
}
