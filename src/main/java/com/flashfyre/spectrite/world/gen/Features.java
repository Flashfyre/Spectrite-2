package com.flashfyre.spectrite.world.gen;

import com.flashfyre.spectrite.Spectrite;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class Features
{
    public static final Feature<DefaultFeatureConfig> SUPERCHROMATIC_CHORUS_PLANT = new SuperchromaticChorusPlantFeature(DefaultFeatureConfig.CODEC);

    public static void initFeatures()
    {
        registerFeature("superchromatic_chorus_plant", SUPERCHROMATIC_CHORUS_PLANT);
    }

    private static RegistryKey<Feature<?>> registerFeature(String name, Feature<?> feature)
    {
        final RegistryKey<Feature<?>> key = RegistryKey.of(Registry.FEATURE_KEY, Spectrite.getId(name));
        Registry.register(Registry.FEATURE, key.getValue(), feature);
        return key;
    }
}
