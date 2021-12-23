package com.flashfyre.spectrite.component.entity;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;

public interface SpectriteWeaponAttributesComponent extends ComponentV3, AutoSyncedComponent
{
    int getSpectriteDamage();

    void setSpectriteDamage(int spectriteDamage);

    boolean isSpectriteCharged();

    void setSpectriteCharged(boolean spectriteCharged);

    int getBaseChromaBlastLevel();

    void setBaseChromaBlastLevel(int baseChromaBlastLevel);
}
