package com.flashfyre.spectrite.world;

import com.flashfyre.spectrite.Spectrite;
import com.flashfyre.spectrite.block.Blocks;
import com.google.common.collect.ImmutableList;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreConfiguredFeatures;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class ConfiguredFeatures
{
    public static ImmutableList<OreFeatureConfig.Target> SPECTRITE_ORE_OVERWORLD_TARGETS = ImmutableList.of(
            OreFeatureConfig.createTarget(OreConfiguredFeatures.STONE_ORE_REPLACEABLES, Blocks.SPECTRITE_ORE.getDefaultState()),
            OreFeatureConfig.createTarget(OreConfiguredFeatures.DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_SPECTRITE_ORE.getDefaultState()));

    public static ImmutableList<OreFeatureConfig.Target> SPECTRITE_ORE_NETHER_TARGETS = ImmutableList.of(
            OreFeatureConfig.createTarget(OreConfiguredFeatures.NETHERRACK, Blocks.NETHER_SPECTRITE_ORE.getDefaultState()),
            OreFeatureConfig.createTarget(new BlockMatchRuleTest(net.minecraft.block.Blocks.BLACKSTONE), Blocks.BLACKSTONE_SPECTRITE_ORE.getDefaultState()));

    public static ImmutableList<OreFeatureConfig.Target> SPECTRITE_ORE_END_TARGETS = ImmutableList.of(
            OreFeatureConfig.createTarget(new BlockMatchRuleTest(net.minecraft.block.Blocks.END_STONE), Blocks.END_SPECTRITE_ORE.getDefaultState()));

    public static ConfiguredFeature<?, ?> ORE_SPECTRITE_OVERWORLD = Feature.ORE
            .configure(new OreFeatureConfig(SPECTRITE_ORE_OVERWORLD_TARGETS, 3));

    public static ConfiguredFeature<?, ?> ORE_SPECTRITE_OVERWORLD_LARGE = Feature.ORE
            .configure(new OreFeatureConfig(SPECTRITE_ORE_OVERWORLD_TARGETS, 7));

    public static ConfiguredFeature<?, ?> ORE_SPECTRITE_NETHER = Feature.ORE
            .configure(new OreFeatureConfig(SPECTRITE_ORE_NETHER_TARGETS, 3));

    public static ConfiguredFeature<?, ?> ORE_SPECTRITE_NETHER_ENCLOSED_LARGE = Feature.SCATTERED_ORE
            .configure(new OreFeatureConfig(SPECTRITE_ORE_NETHER_TARGETS, 3, 1.0F));

    public static ConfiguredFeature<?, ?> ORE_SPECTRITE_NETHER_ENCLOSED_SMALL = Feature.SCATTERED_ORE
            .configure(new OreFeatureConfig(SPECTRITE_ORE_NETHER_TARGETS, 1, 1.0F));

    public static ConfiguredFeature<?, ?> ORE_SPECTRITE_END = Feature.ORE
            .configure(new OreFeatureConfig(SPECTRITE_ORE_END_TARGETS, 7));

    public static ConfiguredFeature<?, ?> ORE_SPECTRITE_END_LARGE = Feature.ORE
            .configure(new OreFeatureConfig(SPECTRITE_ORE_END_TARGETS, 14));

    public static ConfiguredFeature<?, ?> ORE_SPECTRITE_END_HUGE = Feature.ORE
            .configure(new OreFeatureConfig(SPECTRITE_ORE_END_TARGETS, 21));

    public static void initConfiguredFeatures()
    {
        registerFeature("ore_spectrite_overworld", ORE_SPECTRITE_OVERWORLD);
        registerFeature("ore_spectrite_overworld_large", ORE_SPECTRITE_OVERWORLD_LARGE);
        registerFeature("ore_spectrite_nether", ORE_SPECTRITE_NETHER);
        registerFeature("ore_spectrite_nether_enclosed_large", ORE_SPECTRITE_NETHER_ENCLOSED_LARGE);
        registerFeature("ore_spectrite_nether_enclosed_small", ORE_SPECTRITE_NETHER_ENCLOSED_SMALL);
        registerFeature("ore_spectrite_end", ORE_SPECTRITE_END);
        registerFeature("ore_spectrite_end_large", ORE_SPECTRITE_END_LARGE);
        registerFeature("ore_spectrite_end_huge", ORE_SPECTRITE_END_HUGE);
    }

    private static RegistryKey<ConfiguredFeature<?, ?>> registerFeature(String name, ConfiguredFeature<?, ?> feature)
    {
        final RegistryKey<ConfiguredFeature<?, ?>> key = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY,
                Spectrite.getId(name));
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, key.getValue(), feature);
        return key;
    }
}
