package com.flashfyre.spectrite.world.gen;

import com.flashfyre.spectrite.Spectrite;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.*;

import java.util.List;
import java.util.function.Predicate;

public class PlacedFeatures
{
    private static PlacedFeature ORE_SPECTRITE_OVERWORLD = new PlacedFeature(RegistryEntry.of(ConfiguredFeatures.ORE_SPECTRITE_OVERWORLD),
            modifiersWithRarity(2, HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-80), YOffset.aboveBottom(80))));

    private static PlacedFeature ORE_SPECTRITE_OVERWORLD_LARGE = new PlacedFeature(RegistryEntry.of(ConfiguredFeatures.ORE_SPECTRITE_OVERWORLD_LARGE),
            modifiersWithRarity(21, HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-80), YOffset.aboveBottom(80))));

    private static PlacedFeature ORE_SPECTRITE_NETHER = new PlacedFeature(RegistryEntry.of(ConfiguredFeatures.ORE_SPECTRITE_NETHER),
            modifiersWithRarity(14, net.minecraft.world.gen.feature.PlacedFeatures.TEN_ABOVE_AND_BELOW_RANGE));

    private static PlacedFeature ORE_SPECTRITE_NETHER_ENCLOSED_LARGE = new PlacedFeature(RegistryEntry.of(ConfiguredFeatures.ORE_SPECTRITE_NETHER_ENCLOSED_LARGE),
            modifiersWithRarity(14, HeightRangePlacementModifier.trapezoid(YOffset.fixed(8), YOffset.fixed(24))));

    private static PlacedFeature ORE_SPECTRITE_NETHER_ENCLOSED_SMALL = new PlacedFeature(RegistryEntry.of(ConfiguredFeatures.ORE_SPECTRITE_NETHER_ENCLOSED_SMALL),
            modifiersWithRarity(14, net.minecraft.world.gen.feature.PlacedFeatures.EIGHT_ABOVE_AND_BELOW_RANGE));

    private static PlacedFeature ORE_SPECTRITE_END = new PlacedFeature(RegistryEntry.of(ConfiguredFeatures.ORE_SPECTRITE_END_LARGE),
            modifiersWithRarity(21, HeightRangePlacementModifier.trapezoid(YOffset.fixed(4), YOffset.fixed(55))));

    private static PlacedFeature ORE_SPECTRITE_END_LARGE = new PlacedFeature(RegistryEntry.of(ConfiguredFeatures.ORE_SPECTRITE_END_LARGE),
            modifiersWithRarity(147, HeightRangePlacementModifier.trapezoid(YOffset.fixed(4), YOffset.fixed(55))));

    private static PlacedFeature ORE_SPECTRITE_END_HUGE = new PlacedFeature(RegistryEntry.of(ConfiguredFeatures.ORE_SPECTRITE_END_HUGE),
            modifiersWithRarity(1029, HeightRangePlacementModifier.trapezoid(YOffset.fixed(4), YOffset.fixed(55))));

    private static PlacedFeature SUPERCHROMATIC_CHORUS_PLANT = new PlacedFeature(RegistryEntry.of(ConfiguredFeatures.SUPERCHROMATIC_CHORUS_PLANT),
            List.of(CountPlacementModifier.of(1), RarityFilterPlacementModifier.of(4900), SquarePlacementModifier.of(),
                    net.minecraft.world.gen.feature.PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()));

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
