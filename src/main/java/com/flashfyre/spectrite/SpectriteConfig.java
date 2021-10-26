package com.flashfyre.spectrite;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = Spectrite.MODID)
public class SpectriteConfig implements ConfigData
{
    public static SpectriteConfig INSTANCE;

    private float spectriteToolCooldown = 10f;

    private float spectriteMobSpawnRate = 0.048828125f;

    private float spectriteMobCrystalSpawnRate = 0.390625f;

    @ConfigEntry.Gui.CollapsibleObject
    private SpectriteEntityAttributes spectriteEntityAttributes = new SpectriteEntityAttributes();

    public static class SpectriteEntityAttributes
    {
        private double spectriteMobHealthBonus = 40.0d;

        private double spectriteMobHealthMultiplier = 2.0d;

        private double spectriteMobAttackDamageBonus = 3.0d;

        private double spectriteMobAttackDamageMultiplier = 1.5d;

        private double spectriteMobAttackKnockbackBonus = 1.0d;

        private double spectriteMobAttackKnockbackMultiplier = 1.5d;

        private double spectriteMobAttackSpeedBonus = 0.0d;

        private double spectriteMobAttackSpeedMultiplier = 1.27202d;

        private double spectriteMobArmorBonus = 2.0d;

        private double spectriteMobArmorMultiplier = 1.5d;

        private double spectriteMobArmorToughnessBonus = 0.0d;

        private double spectriteMobArmorToughnessMultiplier = 1.0d;

        private double spectriteMobMovementSpeedBonus = 0.1d;

        private double spectriteMobMovementSpeedMultiplier = 1.27202d;

        private double spectriteMobKnockbackResistanceBonus = 0.25d;

        private double spectriteMobKnockbackResistanceMultiplier = 1.27202d;

        public double getSpectriteMobHealthBonus()
        {
            return spectriteMobHealthBonus;
        }

        public double getSpectriteMobHealthMultiplier()
        {
            return spectriteMobHealthMultiplier;
        }

        public double getSpectriteMobAttackDamageBonus()
        {
            return spectriteMobAttackDamageBonus;
        }

        public double getSpectriteMobAttackDamageMultiplier()
        {
            return spectriteMobAttackDamageMultiplier;
        }

        public double getSpectriteMobAttackKnockbackBonus()
        {
            return spectriteMobAttackKnockbackBonus;
        }

        public double getSpectriteMobAttackKnockbackMultiplier()
        {
            return spectriteMobAttackKnockbackMultiplier;
        }

        public double getSpectriteMobAttackSpeedBonus()
        {
            return spectriteMobAttackSpeedBonus;
        }

        public double getSpectriteMobAttackSpeedMultiplier()
        {
            return spectriteMobAttackSpeedMultiplier;
        }

        public double getSpectriteMobArmorBonus()
        {
            return spectriteMobArmorBonus;
        }

        public double getSpectriteMobArmorMultiplier()
        {
            return spectriteMobArmorMultiplier;
        }

        public double getSpectriteMobArmorToughnessBonus()
        {
            return spectriteMobArmorToughnessBonus;
        }

        public double getSpectriteMobArmorToughnessMultiplier()
        {
            return spectriteMobArmorToughnessMultiplier;
        }

        public double getSpectriteMobMovementSpeedBonus()
        {
            return spectriteMobMovementSpeedBonus;
        }

        public double getSpectriteMobMovementSpeedMultiplier()
        {
            return spectriteMobMovementSpeedMultiplier;
        }

        public double getSpectriteMobKnockbackResistanceBonus()
        {
            return spectriteMobKnockbackResistanceBonus;
        }

        public double getSpectriteMobKnockbackResistanceMultiplier()
        {
            return spectriteMobKnockbackResistanceMultiplier;
        }
    }

    public static float getSpectriteToolCooldown()
    {
        return INSTANCE.spectriteToolCooldown;
    }

    public static float getSpectriteMobSpawnRate()
    {
        return INSTANCE.spectriteMobSpawnRate;
    }

    public static float getSpectriteMobCrystalSpawnRate()
    {
        return INSTANCE.spectriteMobCrystalSpawnRate;
    }

    public static SpectriteEntityAttributes getSpectriteEntityAttributes()
    {
        return INSTANCE.spectriteEntityAttributes;
    }
}
