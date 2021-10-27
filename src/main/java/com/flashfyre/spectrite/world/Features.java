package com.flashfyre.spectrite.world;

import com.flashfyre.spectrite.Spectrite;
import com.flashfyre.spectrite.block.Blocks;
import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

import java.util.function.Predicate;

public class Features
{
    private static ImmutableList<OreFeatureConfig.Target> SPECTRITE_ORE_OVERWORLD_TARGETS = ImmutableList.of(
            OreFeatureConfig.createTarget(OreFeatureConfig.Rules.STONE_ORE_REPLACEABLES, Blocks.SPECTRITE_ORE.getDefaultState()),
            OreFeatureConfig.createTarget(OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_SPECTRITE_ORE.getDefaultState()));

    private static ImmutableList<OreFeatureConfig.Target> SPECTRITE_ORE_NETHER_TARGETS = ImmutableList.of(
            OreFeatureConfig.createTarget(OreFeatureConfig.Rules.NETHERRACK, Blocks.NETHER_SPECTRITE_ORE.getDefaultState()),
            OreFeatureConfig.createTarget(new BlockMatchRuleTest(net.minecraft.block.Blocks.BLACKSTONE), Blocks.BLACKSTONE_SPECTRITE_ORE.getDefaultState()));

    private static ImmutableList<OreFeatureConfig.Target> SPECTRITE_ORE_END_TARGETS = ImmutableList.of(
            OreFeatureConfig.createTarget(new BlockMatchRuleTest(net.minecraft.block.Blocks.END_STONE), Blocks.END_SPECTRITE_ORE.getDefaultState()));

    private static ConfiguredFeature<?, ?> ORE_SPECTRITE_OVERWORLD = Feature.ORE
            .configure(new OreFeatureConfig(SPECTRITE_ORE_OVERWORLD_TARGETS, 3))
            .range(new RangeDecoratorConfig(
                    UniformHeightProvider.create(YOffset.getBottom(), YOffset.fixed(16))))
            .spreadHorizontally()
            .applyChance(2);

    private static ConfiguredFeature<?, ?> ORE_SPECTRITE_OVERWORLD_LARGE = Feature.ORE
            .configure(new OreFeatureConfig(SPECTRITE_ORE_OVERWORLD_TARGETS,
                    7))
            .range(new RangeDecoratorConfig(
                    UniformHeightProvider.create(YOffset.getBottom(), YOffset.fixed(16))))
            .spreadHorizontally()
            .applyChance(21);

    private static ConfiguredFeature<?, ?> ORE_SPECTRITE_NETHER = Feature.ORE
            .configure(new OreFeatureConfig(SPECTRITE_ORE_NETHER_TARGETS, 3))
            .range(new RangeDecoratorConfig(UniformHeightProvider.create(YOffset.aboveBottom(10), YOffset.belowTop(10))))
            .spreadHorizontally()
            .applyChance(14);

    private static ConfiguredFeature<?, ?> ORE_SPECTRITE_NETHER_ENCLOSED_LARGE = Feature.SCATTERED_ORE
            .configure(new OreFeatureConfig(SPECTRITE_ORE_NETHER_TARGETS, 3, 1.0F))
            .triangleRange(YOffset.fixed(8), YOffset.fixed(24))
            .spreadHorizontally()
            .applyChance(14);

    private static ConfiguredFeature<?, ?> ORE_SPECTRITE_NETHER_ENCLOSED_SMALL = Feature.SCATTERED_ORE
            .configure(new OreFeatureConfig(SPECTRITE_ORE_NETHER_TARGETS, 1, 1.0F))
            .range(new RangeDecoratorConfig(UniformHeightProvider.create(YOffset.aboveBottom(8), YOffset.belowTop(8))))
            .spreadHorizontally()
            .applyChance(14);

    private static ConfiguredFeature<?, ?> ORE_SPECTRITE_END = Feature.ORE
            .configure(new OreFeatureConfig(SPECTRITE_ORE_END_TARGETS, 7))
            .range(new RangeDecoratorConfig(
                    UniformHeightProvider.create(YOffset.fixed(4), YOffset.fixed(55))))
            .spreadHorizontally()
            .applyChance(21);

    private static ConfiguredFeature<?, ?> ORE_SPECTRITE_END_LARGE = Feature.ORE
            .configure(new OreFeatureConfig(SPECTRITE_ORE_END_TARGETS, 14))
            .range(new RangeDecoratorConfig(
                    UniformHeightProvider.create(YOffset.fixed(4), YOffset.fixed(55))))
            .spreadHorizontally()
            .applyChance(147);

    private static ConfiguredFeature<?, ?> ORE_SPECTRITE_END_HUGE = Feature.ORE
            .configure(new OreFeatureConfig(SPECTRITE_ORE_END_TARGETS, 21))
            .range(new RangeDecoratorConfig(
                    UniformHeightProvider.create(YOffset.fixed(4), YOffset.fixed(55))))
            .spreadHorizontally()
            .applyChance(1029);

    public static void initFeatures()
    {
        registerOreFeature("ore_spectrite_overworld", ORE_SPECTRITE_OVERWORLD,
                BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES);
        registerOreFeature("ore_spectrite_overworld_large", ORE_SPECTRITE_OVERWORLD_LARGE,
                BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES);
        registerOreFeature("ore_spectrite_nether", ORE_SPECTRITE_NETHER,
                BiomeSelectors.foundInTheNether(), GenerationStep.Feature.UNDERGROUND_ORES);
        registerOreFeature("ore_spectrite_nether_enclosed_large", ORE_SPECTRITE_NETHER_ENCLOSED_LARGE,
                BiomeSelectors.foundInTheNether(), GenerationStep.Feature.UNDERGROUND_ORES);
        registerOreFeature("ore_spectrite_nether_enclosed_small", ORE_SPECTRITE_NETHER_ENCLOSED_SMALL,
                BiomeSelectors.foundInTheNether(), GenerationStep.Feature.UNDERGROUND_ORES);
        registerOreFeature("ore_spectrite_end", ORE_SPECTRITE_END,
                BiomeSelectors.foundInTheEnd(), GenerationStep.Feature.UNDERGROUND_ORES);
        registerOreFeature("ore_spectrite_end_large", ORE_SPECTRITE_END_LARGE,
                BiomeSelectors.foundInTheEnd(), GenerationStep.Feature.UNDERGROUND_ORES);
        registerOreFeature("ore_spectrite_end_huge", ORE_SPECTRITE_END_HUGE,
                BiomeSelectors.foundInTheEnd(), GenerationStep.Feature.UNDERGROUND_ORES);
    }

    private static RegistryKey<ConfiguredFeature<?, ?>> registerOreFeature(String name, ConfiguredFeature<?, ?> feature,
                                                                           Predicate<BiomeSelectionContext> biomeSelector,
                                                                           GenerationStep.Feature step)
    {
        final RegistryKey<ConfiguredFeature<?, ?>> key = registerFeature(name, feature);
        BiomeModifications.addFeature(biomeSelector, step, key);
        return key;
    }

    private static RegistryKey<ConfiguredFeature<?, ?>> registerFeature(String name, ConfiguredFeature<?, ?> feature)
    {
        final RegistryKey<ConfiguredFeature<?, ?>> key = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY,
                Spectrite.getId(name));
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, key.getValue(), feature);
        return key;
    }
}
