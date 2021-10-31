package com.flashfyre.spectrite.util;

import com.flashfyre.spectrite.SpectriteConfig;
import com.flashfyre.spectrite.component.Components;
import com.flashfyre.spectrite.component.SpectriteWeaponEntityAttributesComponent;
import com.flashfyre.spectrite.component.SuperchromaticEntityComponent;
import com.flashfyre.spectrite.entity.SpectriteCompatibleMobEntity;
import com.flashfyre.spectrite.entity.effect.StatusEffects;
import com.flashfyre.spectrite.item.SpectriteMeleeWeaponItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

public class SpectriteEntityUtils
{
    public static final Map<EntityAttribute, Map.Entry<Supplier<Double>, Supplier<Double>>> ENTITY_ATTRIBUTE_MODIFIERS = new HashMap<>();
    public static final Map<BlockPos, Map.Entry<Integer, Integer>> BEACON_LOCATIONS = new HashMap<>();

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

    public static boolean trySetMobSuperchromatic(MobEntity mobEntity)
    {
        boolean isSuperchromatic = false;
        final float bonusRate = (float) getMaxBeaconBaseSpectriteBlocks(mobEntity.world, mobEntity.getPos()) * SpectriteConfig.getSuperchromaticMobSpawnRateBeaconBlockBonus();
        final float superchromaticMobSpawnRate = Math.min(SpectriteConfig.getSuperchromaticMobSpawnRate() + bonusRate, 100f);

        if (superchromaticMobSpawnRate == 100f)
            isSuperchromatic = true;
        else if (superchromaticMobSpawnRate > 0f)
        {
            final long leastSignificantBits = Math.abs(mobEntity.getUuid().getLeastSignificantBits());
            isSuperchromatic = (int) (leastSignificantBits % (long) (100l / superchromaticMobSpawnRate)) == 0;
        }

        if (isSuperchromatic)
            setSuperchromatic(mobEntity, true);

        return isSuperchromatic;
    }

    private static int getMaxBeaconBaseSpectriteBlocks(World world, Vec3d pos)
    {
        int maxBeaconBaseSpectriteBlocks = 0;

        final Iterator<Map.Entry<BlockPos, Map.Entry<Integer, Integer>>> beaconLocationsIterator = BEACON_LOCATIONS.entrySet().iterator();

        while (beaconLocationsIterator.hasNext())
        {
            final Map.Entry<BlockPos, Map.Entry<Integer, Integer>> entry = beaconLocationsIterator.next();
            final double d = (entry.getValue().getKey() * 10 + 10);
            final Box box = new Box(entry.getKey()).expand(d).stretch(0.0D, world.getHeight(), 0.0D);
            if (box.contains(pos))
                maxBeaconBaseSpectriteBlocks = entry.getValue().getValue();
        }

        return maxBeaconBaseSpectriteBlocks;
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
        if (superchromatic && entity instanceof LivingEntity livingEntity)
            addPassiveSuperchromaticEffectIfNotPresent(livingEntity);
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

    public static void tryAddChromaBlast(LivingEntity livingEntity, Entity target)
    {
        if (target instanceof LivingEntity livingTarget)
        {
            final boolean isSuperchromatic = livingEntity.hasStatusEffect(StatusEffects.SUPERCHROMATIC);
            final int superchromaticLevel = isSuperchromatic
                    ? livingEntity.getStatusEffect(StatusEffects.SUPERCHROMATIC).getAmplifier() + 1
                    : 0;
            final int superchromaticMobPowerBonus = livingEntity instanceof SpectriteCompatibleMobEntity spectriteCompatibleMobEntity
                    && spectriteCompatibleMobEntity.isSuperchromatic() ? getSuperchromaticMobPowerBonus(((MobEntity) livingEntity)) : 0;
            final ItemStack stack = livingEntity.getMainHandStack();
            final SpectriteMeleeWeaponItem spectriteWeaponItem = !stack.isEmpty()
                    && stack.getItem() instanceof SpectriteMeleeWeaponItem
                    ? (SpectriteMeleeWeaponItem) stack.getItem()
                    : null;
            final PlayerEntity playerEntity = livingEntity instanceof PlayerEntity ? (PlayerEntity) livingEntity : null;

            if (spectriteWeaponItem != null && spectriteWeaponItem.isCharged(stack) && !spectriteWeaponItem.isDepleted())
            {
                final int power = spectriteWeaponItem.getChromaBlastLevel();
                if ((playerEntity == null
                        || playerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), 0f) == 0f))
                {
                    if (playerEntity == null || !(target instanceof PlayerEntity targetPlayer)
                            || playerEntity.shouldDamagePlayer(targetPlayer))
                    {
                        if (playerEntity != null)
                            stack.damage((int) (Math.pow(power, 3f) * spectriteWeaponItem.getStackDamageMultiplier()), playerEntity,
                                    (e) -> e.sendToolBreakStatus(playerEntity.getActiveHand()));

                        if (!livingEntity.world.isClient)
                        {
                            newChromaBlast(livingEntity, livingTarget, power + superchromaticLevel + superchromaticMobPowerBonus);

                            if (playerEntity != null)
                                SpectriteUtils.tryActivateSpectriteChargeableItemCooldown(playerEntity, power, stack);
                        }

                        spectriteWeaponItem.setCharged(stack, false);
                        return;
                    }
                }
            }

            if (isSuperchromatic)
                newChromaBlast(livingEntity, livingTarget, superchromaticLevel + superchromaticMobPowerBonus);
        }
    }

    private static void newChromaBlast(LivingEntity livingEntity, LivingEntity livingTarget, int power)
    {
        livingTarget.timeUntilRegen = 0;
        livingTarget.hurtTime = 0;

        final Vec3d attackerToTarget = livingTarget.getPos().subtract(livingEntity.getPos());
        final Vec3d chromaBlastPos = livingTarget.getPos().subtract(attackerToTarget.normalize());

        SpectriteUtils.newChromaBlast(livingEntity.world, livingEntity, livingTarget, null,
                chromaBlastPos.getX(), livingEntity.getBoundingBox().minY + livingEntity.getHeight() / 2f, chromaBlastPos.getZ(),
                power, false, Explosion.DestructionType.NONE);
    }

    public static void initSuperchromaticMobAttributes(MobEntity mobEntity)
    {
        final float initialMaxHealth = mobEntity.getMaxHealth();
        final float healthRatio = mobEntity.getHealth() / initialMaxHealth;
        final AttributeContainer attributes = mobEntity.getAttributes();
        for (Map.Entry<EntityAttribute, Map.Entry<Supplier<Double>, Supplier<Double>>> e : SpectriteEntityUtils.ENTITY_ATTRIBUTE_MODIFIERS.entrySet())
        {
            final EntityAttribute attribute = e.getKey();
            if (attributes.hasAttribute(attribute))
            {
                final Double bonus = e.getValue().getKey().get();
                final Double multiplier = e.getValue().getValue().get();
                if (bonus != null && bonus.doubleValue() > 0.0d)
                    mobEntity.getAttributeInstance(attribute).addPersistentModifier(
                            new EntityAttributeModifier("Superchromatic mob bonus", bonus, EntityAttributeModifier.Operation.ADDITION));
                if (multiplier != null && multiplier.doubleValue() != 1.0d)
                    mobEntity.getAttributeInstance(attribute).addPersistentModifier(
                            new EntityAttributeModifier("Superchromatic mob multiplier", multiplier - 1.0d, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
            }
        }
        final float currentMaxHealth = mobEntity.getMaxHealth();
        if (currentMaxHealth > initialMaxHealth)
            mobEntity.setHealth(currentMaxHealth * healthRatio);
    }

    public static int getSuperchromaticMobPowerBonus(MobEntity mobEntity)
    {
        final float maxHealth = mobEntity.getMaxHealth();
        if (maxHealth < 140f)
            return 0;
        if (maxHealth < 220f)
            return 1;
        return 2;
    }
}
