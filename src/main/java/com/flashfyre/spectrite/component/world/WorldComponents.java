package com.flashfyre.spectrite.component.world;

import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;

public final class WorldComponents implements WorldComponentInitializer
{
    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry)
    {
        registry.register(SuperchromaticCompatibleWorldComponent.KEY, world -> new SuperchromaticCompatibleWorldComponent());
    }
}
