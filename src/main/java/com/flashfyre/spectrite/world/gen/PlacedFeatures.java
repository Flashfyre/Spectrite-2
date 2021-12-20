package com.flashfyre.spectrite.world.gen;

import com.flashfyre.spectrite.Spectrite;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.*;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.List;
import java.util.function.Predicate;

public class PlacedFeatures
{
    private static PlacedFeature ORE_SPECTRITE_OVERWORLD = ConfiguredFeatures.ORE_SPECTRITE_OVERWORLD
            .withPlacement(modifiersWithRarity(2, HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-80), YOffset.aboveBottom(80))));

    private static PlacedFeature ORE_SPECTRITE_OVERWORLD_LARGE = ConfiguredFeatures.ORE_SPECTRITE_OVERWORLD_LARGE
            .withPlacement(modifiersWithRarity(21, HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-80), YOffset.aboveBottom(80))));

    private static PlacedFeature ORE_SPECTRITE_NETHER = ConfiguredFeatures.ORE_SPECTRITE_NETHER
            .withPlacement(modifiersWithRarity(14, net.minecraft.world.gen.feature.PlacedFeatures.TEN_ABOVE_AND_BELOW_RANGE));

    private static PlacedFeature ORE_SPECTRITE_NETHER_ENCLOSED_LARGE = ConfiguredFeatures.ORE_SPECTRITE_NETHER_ENCLOSED_LARGE
            .withPlacement(modifiersWithRarity(14, HeightRangePlacementModifier.trapezoid(YOffset.fixed(8), YOffset.fixed(24))));

    private static PlacedFeature ORE_SPECTRITE_NETHER_ENCLOSED_SMALL = ConfiguredFeatures.ORE_SPECTRITE_NETHER_ENCLOSED_SMALL
            .withPlacement(modifiersWithRarity(14, net.minecraft.world.gen.feature.PlacedFeatures.EIGHT_ABOVE_AND_BELOW_RANGE));

    private static PlacedFeature ORE_SPECTRITE_END = ConfiguredFeatures.ORE_SPECTRITE_END_LARGE
            .withPlacement(modifiersWithRarity(21, HeightRangePlacementModifier.trapezoid(YOffset.fixed(4), YOffset.fixed(55))));

    private static PlacedFeature ORE_SPECTRITE_END_LARGE = ConfiguredFeatures.ORE_SPECTRITE_END_LARGE
            .withPlacement(modifiersWithRarity(147, HeightRangePlacementModifier.trapezoid(YOffset.fixed(4), YOffset.fixed(55))));

    private static PlacedFeature ORE_SPECTRITE_END_HUGE = ConfiguredFeatures.ORE_SPECTRITE_END_HUGE
            .withPlacement(modifiersWithRarity(1029, HeightRangePlacementModifier.trapezoid(YOffset.fixed(4), YOffset.fixed(55))));

    private static PlacedFeature SUPERCHROMATIC_CHORUS_PLANT = ConfiguredFeatures.SUPERCHROMATIC_CHORUS_PLANT
            .withPlacement(CountPlacementModifier.of(1), RarityFilterPlacementModifier.of(4900), SquarePlacementModifier.of(),
                    net.minecraft.world.gen.feature.PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of());

    public static void initPlacedFeatures()
    {
        registerPlacedFeature("ore_spectrite_overworld", ORE_SPECTRITE_OVERWORLD,
                BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES);
        registerPlacedFeature("ore_spectrite_overworld_large", ORE_SPECTRITE_OVERWORLD_LARGE,
                BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES);
        registerPlacedFeature("ore_spectrite_nether", ORE_SPECTRITE_NETHER,
                BiomeSelectors.foundInTheNether(), GenerationStep.Feature.UNDERGROUND_ORES);
        registerPlacedFeature("ore_spectrite_nether_enclosed_large", ORE_SPECTRITE_NETHER_ENCLOSED_LARGE,
                BiomeSelectors.foundInTheNether(), GenerationStep.Feature.UNDERGROUND_ORES);
        registerPlacedFeature("ore_spectrite_nether_enclosed_small", ORE_SPECTRITE_NETHER_ENCLOSED_SMALL,
                BiomeSelectors.foundInTheNether(), GenerationStep.Feature.UNDERGROUND_ORES);
        registerPlacedFeature("ore_spectrite_end", ORE_SPECTRITE_END,
                BiomeSelectors.foundInTheEnd(), GenerationStep.Feature.UNDERGROUND_ORES);
        registerPlacedFeature("ore_spectrite_end_large", ORE_SPECTRITE_END_LARGE,
                BiomeSelectors.foundInTheEnd(), GenerationStep.Feature.UNDERGROUND_ORES);
        registerPlacedFeature("ore_spectrite_end_huge", ORE_SPECTRITE_END_HUGE,
                BiomeSelectors.foundInTheEnd(), GenerationStep.Feature.UNDERGROUND_ORES);
        registerPlacedFeature("superchromatic_chorus_plant", SUPERCHROMATIC_CHORUS_PLANT,
                BiomeSelectors.foundInTheEnd(), GenerationStep.Feature.VEGETAL_DECORATION);
    }

    private static RegistryKey<PlacedFeature> registerPlacedFeature(String name, PlacedFeature feature,
                                                                    Predicate<BiomeSelectionContext> biomeSelector,
                                                                    GenerationStep.Feature step)
    {
        final RegistryKey<PlacedFeature> key = RegistryKey.of(Registry.PLACED_FEATURE_KEY,
                Spectrite.getId(name));
        Registry.register(BuiltinRegistries.PLACED_FEATURE, key.getValue(), feature);
        BiomeModifications.addFeature(biomeSelector, step, key);
        return key;
    }

    private static List<PlacementModifier> modifiers(PlacementModifier countModifier, PlacementModifier heightModifier)
    {
        return List.of(countModifier, SquarePlacementModifier.of(), heightModifier, BiomePlacementModifier.of());
    }

    private static List<PlacementModifier> modifiersWithRarity(int chance, PlacementModifier heightModifier)
    {
        return modifiers(RarityFilterPlacementModifier.of(chance), heightModifier);
    }
}
