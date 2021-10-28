package com.flashfyre.spectrite.util;

import com.flashfyre.spectrite.SpectriteConfig;
import com.flashfyre.spectrite.component.Components;
import com.flashfyre.spectrite.component.SpectriteWeaponEntityAttributesComponent;
import com.flashfyre.spectrite.component.SuperchromaticEntityComponent;
import com.flashfyre.spectrite.entity.effect.StatusEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SpectriteEntityUtils
{
    public static final Map<EntityAttribute, Map.Entry<Supplier<Double>, Supplier<Double>>> ENTITY_ATTRIBUTE_MODIFIERS = new HashMap<>();

    static
    {
        ENTITY_ATTRIBUTE_MODIFIERS.put(EntityAttributes.GENERIC_MAX_HEALTH, new AbstractMap.SimpleEntry<>(
                () -> SpectriteConfig.getSuperchromaticEntityAttributes().getSuperchromaticMobHealthBonus(),
                () -> SpectriteConfig.getSuperchromaticEntityAttributes().getSuperchromaticMobHealthMultiplier()
        ));
        ENTITY_ATTRIBUTE_MODIFIERS.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new AbstractMap.SimpleEntry<>(
                () -> SpectriteConfig.getSuperchromaticEntityAttributes().getSuperchromaticMobAttackDamageBonus(),
                () -> SpectriteConfig.getSuperchromaticEntityAttributes().getSuperchromaticMobAttackDamageMultiplier()
        ));
        ENTITY_ATTRIBUTE_MODIFIERS.put(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, new AbstractMap.SimpleEntry<>(
                () -> SpectriteConfig.getSuperchromaticEntityAttributes().getSuperchromaticMobAttackKnockbackBonus(),
                () -> SpectriteConfig.getSuperchromaticEntityAttributes().getSuperchromaticMobAttackKnockbackMultiplier()
        ));
        ENTITY_ATTRIBUTE_MODIFIERS.put(EntityAttributes.GENERIC_ATTACK_SPEED, new AbstractMap.SimpleEntry<>(
                () -> SpectriteConfig.getSuperchromaticEntityAttributes().getSuperchromaticMobAttackSpeedBonus(),
                () -> SpectriteConfig.getSuperchromaticEntityAttributes().getSuperchromaticMobAttackSpeedMultiplier()
        ));
        ENTITY_ATTRIBUTE_MODIFIERS.put(EntityAttributes.GENERIC_ARMOR, new AbstractMap.SimpleEntry<>(
                () -> SpectriteConfig.getSuperchromaticEntityAttributes().getSuperchromaticMobArmorBonus(),
                () -> SpectriteConfig.getSuperchromaticEntityAttributes().getSuperchromaticMobArmorMultiplier()
        ));
        ENTITY_ATTRIBUTE_MODIFIERS.put(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new AbstractMap.SimpleEntry<>(
                () -> SpectriteConfig.getSuperchromaticEntityAttributes().getSuperchromaticMobArmorToughnessBonus(),
                () -> SpectriteConfig.getSuperchromaticEntityAttributes().getSuperchromaticMobArmorToughnessMultiplier()
        ));
        ENTITY_ATTRIBUTE_MODIFIERS.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, new AbstractMap.SimpleEntry<>(
                () -> SpectriteConfig.getSuperchromaticEntityAttributes().getSuperchromaticMobMovementSpeedBonus(),
                () -> SpectriteConfig.getSuperchromaticEntityAttributes().getSuperchromaticMobMovementSpeedMultiplier()
        ));
        ENTITY_ATTRIBUTE_MODIFIERS.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new AbstractMap.SimpleEntry<>(
                () -> SpectriteConfig.getSuperchromaticEntityAttributes().getSuperchromaticMobKnockbackResistanceBonus(),
                () -> SpectriteConfig.getSuperchromaticEntityAttributes().getSuperchromaticMobKnockbackResistanceMultiplier()
        ));
    }

    public static boolean isSuperchromatic(Entity entity)
    {
        final SuperchromaticEntityComponent superchromaticEntityComponent;
        superchromaticEntityComponent = Components.SUPERCHROMATIC_ENTITY.maybeGet(entity).orElse(null);
        return superchromaticEntityComponent != null && superchromaticEntityComponent.isSuperchromatic();
    }

    public static void setSuperchromatic(Entity entity, boolean superchromatic)
    {
        Components.SUPERCHROMATIC_ENTITY.maybeGet(entity).ifPresent(
                superchromaticEntityComponent -> superchromaticEntityComponent.setSuperchromatic(superchromatic));
    }

    public static int getSpectriteDamage(Entity entity)
    {
        final SpectriteWeaponEntityAttributesComponent spectriteWeaponEntityAttributesComponent =
                Components.SPECTRITE_WEAPON_ENTITY_ATTRIBUTES.maybeGet(entity).orElse(null);
        return spectriteWeaponEntityAttributesComponent != null ? spectriteWeaponEntityAttributesComponent.getSpectriteDamage() : 0;
    }

    public static void setSpectriteDamage(Entity entity, int spectriteDamage)
    {
        Components.SPECTRITE_WEAPON_ENTITY_ATTRIBUTES.maybeGet(entity).ifPresent(
                spectriteWeaponEntityAttributesComponent -> spectriteWeaponEntityAttributesComponent.setSpectriteDamage(spectriteDamage));
    }

    public static boolean isSpectriteCharged(Entity entity)
    {
        final SpectriteWeaponEntityAttributesComponent spectriteWeaponEntityAttributesComponent =
                Components.SPECTRITE_WEAPON_ENTITY_ATTRIBUTES.maybeGet(entity).orElse(null);
        return spectriteWeaponEntityAttributesComponent != null && spectriteWeaponEntityAttributesComponent.isSpectriteCharged();
    }

    public static void setSpectriteCharged(Entity entity, boolean spectriteCharged)
    {
        Components.SPECTRITE_WEAPON_ENTITY_ATTRIBUTES.maybeGet(entity).ifPresent(
                spectriteWeaponEntityAttributesComponent -> spectriteWeaponEntityAttributesComponent.setSpectriteCharged(spectriteCharged));
    }

    public static void addPassiveSuperchromaticEffectIfNotPresent(LivingEntity livingEntity)
    {
        if (!livingEntity.hasStatusEffect(StatusEffects.SUPERCHROMATIC))
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SUPERCHROMATIC, 16, 0, true, false, true));
    }
}
