package com.flashfyre.spectrite;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = Spectrite.MODID)
public class SpectriteConfig implements ConfigData
{
    public static SpectriteConfig INSTANCE;

    private boolean spectriteToolsBreakPermanently = false;

    private float spectriteToolBaseCooldown = 10f;

    private float superchromaticToolBaseCooldown = 10f;

    private int superchromaticFoodUses = 0;

    private float superchromaticFoodCooldown = 300f;

    private int superchromaticChorusFruitUses = 0;

    private float superchromaticChorusFruitCooldown = 10f;

    private int superchromaticEnderPearlUses = 0;

    private float superchromaticMobSpawnRate = 0.48828f;

    private float superchromaticMobSpawnRateBeaconBlockBonus = 0.037977f;

    private float superchromaticStructureRate = 2.040816f;

    @ConfigEntry.Gui.CollapsibleObject
    private SuperchromaticEntityAttributes superchromaticEntityAttributes = new SuperchromaticEntityAttributes();

    public static class SuperchromaticEntityAttributes
    {
        private double superchromaticMobHealthBonus = 40.0d;

        private double superchromaticMobHealthMultiplier = 2.0d;

        private double superchromaticMobAttackDamageBonus = 3.0d;

        private double superchromaticMobAttackDamageMultiplier = 1.5d;

        private double superchromaticMobAttackKnockbackBonus = 1.0d;

        private double superchromaticMobAttackKnockbackMultiplier = 1.5d;

        private double superchromaticMobAttackSpeedBonus = 0.0d;

        private double superchromaticMobAttackSpeedMultiplier = 1.27202d;

        private double superchromaticMobArmorBonus = 2.0d;

        private double superchromaticMobArmorMultiplier = 1.5d;

        private double superchromaticMobArmorToughnessBonus = 0.0d;

        private double superchromaticMobArmorToughnessMultiplier = 1.0d;

        private double superchromaticMobMovementSpeedBonus = 0.1d;

        private double superchromaticMobMovementSpeedMultiplier = 1.0d;

        private double superchromaticMobKnockbackResistanceBonus = 0.25d;

        private double superchromaticMobKnockbackResistanceMultiplier = 1.27202d;

        public double getSuperchromaticMobHealthBonus()
        {
            return superchromaticMobHealthBonus;
        }

        public double getSuperchromaticMobHealthMultiplier()
        {
            return superchromaticMobHealthMultiplier;
        }

        public double getSuperchromaticMobAttackDamageBonus()
        {
            return superchromaticMobAttackDamageBonus;
        }

        public double getSuperchromaticMobAttackDamageMultiplier()
        {
            return superchromaticMobAttackDamageMultiplier;
        }

        public double getSuperchromaticMobAttackKnockbackBonus()
        {
            return superchromaticMobAttackKnockbackBonus;
        }

        public double getSuperchromaticMobAttackKnockbackMultiplier()
        {
            return superchromaticMobAttackKnockbackMultiplier;
        }

        public double getSuperchromaticMobAttackSpeedBonus()
        {
            return superchromaticMobAttackSpeedBonus;
        }

        public double getSuperchromaticMobAttackSpeedMultiplier()
        {
            return superchromaticMobAttackSpeedMultiplier;
        }

        public double getSuperchromaticMobArmorBonus()
        {
            return superchromaticMobArmorBonus;
        }

        public double getSuperchromaticMobArmorMultiplier()
        {
            return superchromaticMobArmorMultiplier;
        }

        public double getSuperchromaticMobArmorToughnessBonus()
        {
            return superchromaticMobArmorToughnessBonus;
        }

        public double getSuperchromaticMobArmorToughnessMultiplier()
        {
            return superchromaticMobArmorToughnessMultiplier;
        }

        public double getSuperchromaticMobMovementSpeedBonus()
        {
            return superchromaticMobMovementSpeedBonus;
        }

        public double getSuperchromaticMobMovementSpeedMultiplier()
        {
            return superchromaticMobMovementSpeedMultiplier;
        }

        public double getSuperchromaticMobKnockbackResistanceBonus()
        {
            return superchromaticMobKnockbackResistanceBonus;
        }

        public double getSuperchromaticMobKnockbackResistanceMultiplier()
        {
            return superchromaticMobKnockbackResistanceMultiplier;
        }
    }

    public static boolean getSpectriteToolsBreakPermanently()
    {
        return INSTANCE.spectriteToolsBreakPermanently;
    }

    public static float getSpectriteToolBaseCooldown()
    {
        return INSTANCE.spectriteToolBaseCooldown;
    }

    public static float getSuperchromaticToolBaseCooldown()
    {
        return INSTANCE.superchromaticToolBaseCooldown;
    }

    public static int getSuperchromaticFoodUses()
    {
        return INSTANCE.superchromaticFoodUses;
    }

    public static float getSuperchromaticFoodCooldown()
    {
        return INSTANCE.superchromaticFoodCooldown;
    }

    public static int getSuperchromaticChorusFruitUses()
    {
        return INSTANCE.superchromaticChorusFruitUses;
    }

    public static float getSuperchromaticChorusFruitCooldown()
    {
        return INSTANCE.superchromaticChorusFruitCooldown;
    }

    public static int getSuperchromaticEnderPearlUses()
    {
        return INSTANCE.superchromaticEnderPearlUses;
    }

    public static float getSuperchromaticMobSpawnRate()
    {
        return INSTANCE.superchromaticMobSpawnRate;
    }

    public static float getSuperchromaticMobSpawnRateBeaconBlockBonus()
    {
        return INSTANCE.superchromaticMobSpawnRateBeaconBlockBonus;
    }

    public static float getSuperchromaticStructureRate()
    {
        return INSTANCE.superchromaticStructureRate;
    }

    public static SuperchromaticEntityAttributes getSuperchromaticEntityAttributes()
    {
        return INSTANCE.superchromaticEntityAttributes;
    }
}
