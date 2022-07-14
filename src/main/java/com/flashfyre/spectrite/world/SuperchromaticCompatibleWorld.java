package com.flashfyre.spectrite.world;

import net.minecraft.world.WorldAccess;

public interface SuperchromaticCompatibleWorld extends WorldAccess
{
    boolean isClearSuperchromaticFlag();

    void setClearSuperchromaticFlag(boolean clearSuperchromaticFlag);

    void setIgnoreSyncStateFlag(boolean ignoreSyncStateFlag);
}
