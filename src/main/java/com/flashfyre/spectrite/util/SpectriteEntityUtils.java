package com.flashfyre.spectrite.util;

import com.flashfyre.spectrite.SpectriteConfig;
import com.flashfyre.spectrite.component.Components;
import com.flashfyre.spectrite.component.SpectriteEntityComponent;
import com.flashfyre.spectrite.component.SpectriteWeaponEntityAttributesComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;

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
                () -> SpectriteConfig.getSpectriteEntityAttributes().getSpectriteMobHealthBonus(),
                () -> SpectriteConfig.getSpectriteEntityAttributes().getSpectriteMobHealthMultiplier()
        ));
        ENTITY_ATTRIBUTE_MODIFIERS.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new AbstractMap.SimpleEntry<>(
                () -> SpectriteConfig.getSpectriteEntityAttributes().getSpectriteMobAttackDamageBonus(),
                () -> SpectriteConfig.getSpectriteEntityAttributes().getSpectriteMobAttackDamageMultiplier()
        ));
        ENTITY_ATTRIBUTE_MODIFIERS.put(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, new AbstractMap.SimpleEntry<>(
                () -> SpectriteConfig.getSpectriteEntityAttributes().getSpectriteMobAttackKnockbackBonus(),
                () -> SpectriteConfig.getSpectriteEntityAttributes().getSpectriteMobAttackKnockbackMultiplier()
        ));
        ENTITY_ATTRIBUTE_MODIFIERS.put(EntityAttributes.GENERIC_ATTACK_SPEED, new AbstractMap.SimpleEntry<>(
                () -> SpectriteConfig.getSpectriteEntityAttributes().getSpectriteMobAttackSpeedBonus(),
                () -> SpectriteConfig.getSpectriteEntityAttributes().getSpectriteMobAttackSpeedMultiplier()
        ));
        ENTITY_ATTRIBUTE_MODIFIERS.put(EntityAttributes.GENERIC_ARMOR, new AbstractMap.SimpleEntry<>(
                () -> SpectriteConfig.getSpectriteEntityAttributes().getSpectriteMobArmorBonus(),
                () -> SpectriteConfig.getSpectriteEntityAttributes().getSpectriteMobArmorMultiplier()
        ));
        ENTITY_ATTRIBUTE_MODIFIERS.put(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new AbstractMap.SimpleEntry<>(
                () -> SpectriteConfig.getSpectriteEntityAttributes().getSpectriteMobArmorToughnessBonus(),
                () -> SpectriteConfig.getSpectriteEntityAttributes().getSpectriteMobArmorToughnessMultiplier()
        ));
        ENTITY_ATTRIBUTE_MODIFIERS.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, new AbstractMap.SimpleEntry<>(
                () -> SpectriteConfig.getSpectriteEntityAttributes().getSpectriteMobMovementSpeedBonus(),
                () -> SpectriteConfig.getSpectriteEntityAttributes().getSpectriteMobMovementSpeedMultiplier()
        ));
        ENTITY_ATTRIBUTE_MODIFIERS.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new AbstractMap.SimpleEntry<>(
                () -> SpectriteConfig.getSpectriteEntityAttributes().getSpectriteMobKnockbackResistanceBonus(),
                () -> SpectriteConfig.getSpectriteEntityAttributes().getSpectriteMobKnockbackResistanceMultiplier()
        ));
    }

    public static boolean isSpectriteEntity(Entity entity)
    {
        final SpectriteEntityComponent spectriteEntityComponent;
        spectriteEntityComponent = Components.SPECTRITE_ENTITY.maybeGet(entity).orElse(null);
        return spectriteEntityComponent != null && spectriteEntityComponent.isSpectrite();
    }

    public static void setSpectriteEntity(Entity entity, boolean spectriteEntity)
    {
        Components.SPECTRITE_ENTITY.maybeGet(entity).ifPresent(
                spectriteEntityComponent -> spectriteEntityComponent.setSpectrite(spectriteEntity));
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
}
