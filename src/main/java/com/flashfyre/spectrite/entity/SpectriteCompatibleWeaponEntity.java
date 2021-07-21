package com.flashfyre.spectrite.entity;

public interface SpectriteCompatibleWeaponEntity extends SpectriteCompatibleEntity
{
    int getSpectriteDamage();

    void setSpectriteDamage(int spectriteDamage);

    boolean isSpectriteCharged();

    void setSpectriteCharged(boolean spectriteCharged);
}
