package com.flashfyre.spectrite.component.chunk;

import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentInitializer;

public final class ChunkComponents implements ChunkComponentInitializer
{
    @Override
    public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry)
    {
        registry.register(SuperchromaticChunkComponent.KEY, chunk -> new SuperchromaticChunkComponent());
    }
}
