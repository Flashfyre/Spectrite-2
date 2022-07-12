package com.flashfyre.spectrite.world;

import net.minecraft.world.WorldAccess;

public interface SuperchromaticCompatibleChunkRegion extends WorldAccess
{
    boolean isSuperchromaticFlag();

    void setSuperchromaticFlag(boolean superchromaticFlag);
}
