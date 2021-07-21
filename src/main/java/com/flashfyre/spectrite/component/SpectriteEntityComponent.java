package com.flashfyre.spectrite.component;

import net.minecraft.nbt.NbtCompound;

public class SpectriteEntityComponent implements SpectriteComponent
{
    private boolean spectrite;

    @Override
    public boolean isSpectrite()
    {
        return spectrite;
    }

    @Override
    public void setSpectrite(boolean spectrite)
    {
        this.spectrite = spectrite;
    }

    @Override
    public void readFromNbt(NbtCompound tag)
    {
        spectrite = tag.getBoolean("spectrite");
    }

    @Override
    public void writeToNbt(NbtCompound tag)
    {
        tag.putBoolean("spectrite", spectrite);
    }
}
