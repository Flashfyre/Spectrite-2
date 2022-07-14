package com.flashfyre.spectrite.component.world;

import com.flashfyre.spectrite.Spectrite;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

// Currently unused
public class SuperchromaticCompatibleWorldComponent implements AutoSyncedComponent
{
    public static final ComponentKey<SuperchromaticCompatibleWorldComponent> KEY =
            ComponentRegistryV3.INSTANCE.getOrCreate(Spectrite.getId("superchromatic_compatible_world"),
                    SuperchromaticCompatibleWorldComponent.class);

    public SuperchromaticCompatibleWorldComponent()
    {

    }

    @Override
    public void applySyncPacket(PacketByteBuf buf)
    {
        AutoSyncedComponent.super.applySyncPacket(buf);
    }

    @Override
    public void readFromNbt(NbtCompound tag)
    {
    }

    @Override
    public void writeToNbt(NbtCompound tag)
    {
    }
}
