package com.flashfyre.spectrite.world;

import com.flashfyre.spectrite.Spectrite;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

public class Dimensions
{
    public static final RegistryKey<World> SUPERCHROMATIC = RegistryKey.of(Registry.WORLD_KEY, Spectrite.getId("superchromatic"));
    public static final RegistryKey<DimensionOptions> SUPERCHROMATIC_DIMENSION = RegistryKey.of(Registry.DIMENSION_KEY, Spectrite.getId("superchromatic"));
    public static final RegistryKey<DimensionType> SUPERCHROMATIC_DIMENSION_TYPE = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, Spectrite.getId("superchromatic"));
}
