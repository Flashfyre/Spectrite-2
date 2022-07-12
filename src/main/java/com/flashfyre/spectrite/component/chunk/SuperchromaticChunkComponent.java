package com.flashfyre.spectrite.component.chunk;

import com.flashfyre.spectrite.Spectrite;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SuperchromaticChunkComponent implements AutoSyncedComponent
{
    public static final ComponentKey<SuperchromaticChunkComponent> KEY =
            ComponentRegistryV3.INSTANCE.getOrCreate(Spectrite.getId("superchromatic_chunk"), SuperchromaticChunkComponent.class);

    private final Set<BlockPos> superchromaticBlocks = new HashSet<>();

    public SuperchromaticChunkComponent()
    {

    }

    public Set<BlockPos> getSuperchromaticBlocks()
    {
        return superchromaticBlocks;
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf)
    {
        AutoSyncedComponent.super.applySyncPacket(buf);
    }

    @Override
    public void readFromNbt(NbtCompound tag)
    {
        superchromaticBlocks.clear();
        if (tag.contains("superchromatic_blocks"))
        {
            final long[] superchromaticBlockPositions = tag.getLongArray("superchromatic_blocks");
            for (long pos : superchromaticBlockPositions)
                superchromaticBlocks.add(BlockPos.fromLong(pos));
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag)
    {
        if (!superchromaticBlocks.isEmpty())
        {
            final List<Long> superchromaticBlockPositions = new ArrayList<>();
            for (BlockPos pos : superchromaticBlocks)
                superchromaticBlockPositions.add(pos.asLong());
            tag.putLongArray("superchromatic_blocks", superchromaticBlockPositions);
        }
    }
}
