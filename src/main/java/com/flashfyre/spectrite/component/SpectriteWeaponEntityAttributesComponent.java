package com.flashfyre.spectrite.component;

import net.minecraft.nbt.NbtCompound;

public class SpectriteWeaponEntityAttributesComponent implements SpectriteWeaponAttributesComponent
{
    private int spectriteDamage;

    private boolean spectriteCharged;

    @Override
    public int getSpectriteDamage()
    {
        return spectriteDamage;
    }

    @Override
    public void setSpectriteDamage(int spectriteDamage)
    {
        this.spectriteDamage = spectriteDamage;
    }

    @Override
    public boolean isSpectriteCharged()
    {
        return spectriteCharged;
    }

    @Override
    public void setSpectriteCharged(boolean spectriteCharged)
    {
        this.spectriteCharged = spectriteCharged;
    }

    @Override
    public void readFromNbt(NbtCompound tag)
    {
        spectriteDamage = tag.getInt("spectrite_damage");
        spectriteCharged = tag.getBoolean("spectrite_charged");
    }

    @Override
    public void writeToNbt(NbtCompound tag)
    {
        tag.putInt("spectrite_damage", spectriteDamage);
        tag.putBoolean("spectrite_charged", spectriteCharged);
    }
}
