package com.flashfyre.spectrite.component;

import net.minecraft.nbt.NbtCompound;

public class SuperchromaticEntityComponent implements SuperchromaticComponent
{
    private boolean superchromatic;

    @Override
    public boolean isSuperchromatic()
    {
        return superchromatic;
    }

    @Override
    public void setSuperchromatic(boolean superchromatic)
    {
        this.superchromatic = superchromatic;
    }

    @Override
    public void readFromNbt(NbtCompound tag)
    {
        superchromatic = tag.getBoolean("superchromatic");
    }

    @Override
    public void writeToNbt(NbtCompound tag)
    {
        tag.putBoolean("superchromatic", superchromatic);
    }
}
