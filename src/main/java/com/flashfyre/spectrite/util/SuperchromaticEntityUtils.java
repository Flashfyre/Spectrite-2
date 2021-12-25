package com.flashfyre.spectrite.util;

import com.flashfyre.spectrite.SpectriteConfig;
import com.flashfyre.spectrite.component.entity.EntityComponents;
import com.flashfyre.spectrite.component.entity.SpectriteWeaponEntityAttributesComponent;
import com.flashfyre.spectrite.component.entity.SuperchromaticEntityComponent;
import com.flashfyre.spectrite.entity.SpectriteCompatibleMobEntity;
import com.flashfyre.spectrite.entity.SpectriteGolemEntity;
import com.flashfyre.spectrite.entity.SuperchromaticEntity;
import com.flashfyre.spectrite.entity.effect.StatusEffects;
import com.flashfyre.spectrite.entity.player.SuperchromaticCooldownPlayerEntity;
import com.flashfyre.spectrite.item.SpectriteMeleeWeaponItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SuperchromaticEntityUtils
{
    public static final Map<EntityAttribute, Map.Entry<Supplier<Double>, Supplier<Double>>> ENTITY_ATTRIBUTE_MODIFIERS = new HashMap<>();
    public static final Map<BlockPos, Map.Entry<Integer, Integer>> BEACON_LOCATIONS = new HashMap<>();

    private static final String SUPERCHROMATIC_MOB_BONUS = "Superchromatic mob bonus";
    private static final String SUPERCHROMATIC_MOB_MULTIPLIER = "Superchromatic mob multiplier";

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
        if (mobEntity instanceof SuperchromaticEntity)
        {
            setSuperchromatic(mobEntity, true);
            return true;
        }

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
        superchromaticEntityComponent = EntityComponents.SUPERCHROMATIC_ENTITY.maybeGet(entity).orElse(null);
        return superchromaticEntityComponent != null && superchromaticEntityComponent.isSuperchromatic();
    }

    public static void setSuperchromatic(Entity entity, boolean superchromatic)
    {
        EntityComponents.SUPERCHROMATIC_ENTITY.maybeGet(entity).ifPresent(
                superchromaticEntityComponent -> superchromaticEntityComponent.setSuperchromatic(superchromatic));
        if (superchromatic && entity instanceof LivingEntity livingEntity)
            addPassiveSuperchromaticEffectIfNotPresent(livingEntity);
    }

    public static int getSpectriteDamage(Entity entity)
    {
        final SpectriteWeaponEntityAttributesComponent spectriteWeaponEntityAttributesComponent =
                EntityComponents.SPECTRITE_WEAPON_ENTITY_ATTRIBUTES.maybeGet(entity).orElse(null);
        return spectriteWeaponEntityAttributesComponent != null ? spectriteWeaponEntityAttributesComponent.getSpectriteDamage() : 0;
    }

    public static void setSpectriteDamage(Entity entity, int spectriteDamage)
    {
        EntityComponents.SPECTRITE_WEAPON_ENTITY_ATTRIBUTES.maybeGet(entity).ifPresent(
                spectriteWeaponEntityAttributesComponent -> spectriteWeaponEntityAttributesComponent.setSpectriteDamage(spectriteDamage));
    }

    public static boolean isSpectriteCharged(Entity entity)
    {
        final SpectriteWeaponEntityAttributesComponent spectriteWeaponEntityAttributesComponent =
                EntityComponents.SPECTRITE_WEAPON_ENTITY_ATTRIBUTES.maybeGet(entity).orElse(null);
        return spectriteWeaponEntityAttributesComponent != null && spectriteWeaponEntityAttributesComponent.isSpectriteCharged();
    }

    public static void setSpectriteCharged(Entity entity, boolean spectriteCharged)
    {
        EntityComponents.SPECTRITE_WEAPON_ENTITY_ATTRIBUTES.maybeGet(entity).ifPresent(
                spectriteWeaponEntityAttributesComponent -> spectriteWeaponEntityAttributesComponent.setSpectriteCharged(spectriteCharged));
    }

    public static int getBaseChromaBlastLevel(Entity entity)
    {
        final SpectriteWeaponEntityAttributesComponent spectriteWeaponEntityAttributesComponent =
                EntityComponents.SPECTRITE_WEAPON_ENTITY_ATTRIBUTES.maybeGet(entity).orElse(null);
        return spectriteWeaponEntityAttributesComponent != null ? spectriteWeaponEntityAttributesComponent.getBaseChromaBlastLevel() : 0;
    }

    public static void setBaseChromaBlastLevel(Entity entity, int baseChromaBlastLevel)
    {
        EntityComponents.SPECTRITE_WEAPON_ENTITY_ATTRIBUTES.maybeGet(entity).ifPresent(
                spectriteWeaponEntityAttributesComponent -> spectriteWeaponEntityAttributesComponent.setBaseChromaBlastLevel(baseChromaBlastLevel));
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
            if (livingEntity instanceof SpectriteGolemEntity spectriteGolemEntity && spectriteGolemEntity.isDepleted())
                return;
            final boolean isSuperchromatic = livingEntity.hasStatusEffect(StatusEffects.SUPERCHROMATIC);
            final int superchromaticLevel = isSuperchromatic
                    ? livingEntity.getStatusEffect(StatusEffects.SUPERCHROMATIC).getAmplifier() + 1
                    : 0;
            final int superchromaticMobPowerBonus = livingEntity instanceof SpectriteCompatibleMobEntity spectriteCompatibleMobEntity
                    && spectriteCompatibleMobEntity.isSuperchromatic() ? getSuperchromaticMobPowerBonus(((MobEntity) livingEntity)) : 0;
            final ItemStack stack = livingEntity.getMainHandStack();
            final Item item = stack.getItem();
            final SpectriteMeleeWeaponItem spectriteWeaponItem = !stack.isEmpty()
                    && item instanceof SpectriteMeleeWeaponItem
                    ? (SpectriteMeleeWeaponItem) stack.getItem()
                    : null;
            final PlayerEntity playerEntity = livingEntity instanceof PlayerEntity ? (PlayerEntity) livingEntity : null;

            if ((spectriteWeaponItem != null && spectriteWeaponItem.isCharged(stack))
                    || (SuperchromaticItemUtils.isSuperchromaticMeleeWeaponItem(stack) && SuperchromaticItemUtils.isSuperchromaticCharged(stack)))
            {
                final int power = spectriteWeaponItem != null ? spectriteWeaponItem.getChromaBlastLevel() : SuperchromaticItemUtils.getSuperchromaticItemChromaBlastLevel(item);
                if ((playerEntity == null
                        || ((SuperchromaticCooldownPlayerEntity) playerEntity).getSuperchromaticItemCooldownManager().getCooldownProgress(0f) == 0f))
                {
                    if (playerEntity == null || !(target instanceof PlayerEntity targetPlayer)
                            || playerEntity.shouldDamagePlayer(targetPlayer))
                    {
                        if (playerEntity != null)
                        {
                            final float stackDamageMultiplier = spectriteWeaponItem != null
                                    ? spectriteWeaponItem.getStackDamageMultiplier()
                                    : SuperchromaticItemUtils.getSuperchromaticWeaponItemDamageMultiplier(item);
                            stack.damage((int) (Math.pow(power, 3f) * stackDamageMultiplier), playerEntity,
                                    (e) -> e.sendToolBreakStatus(playerEntity.getActiveHand()));
                        }

                        if (!livingEntity.world.isClient)
                        {
                            newChromaBlast(livingEntity, livingTarget, power + superchromaticLevel + superchromaticMobPowerBonus);

                            if (playerEntity != null)
                                SuperchromaticItemUtils.tryActivateSuperchromaticOrSpectriteChargeableItemCooldown(playerEntity, stack);
                        }

                        if (spectriteWeaponItem != null)
                            spectriteWeaponItem.setCharged(stack, false);
                        else
                            SuperchromaticItemUtils.setSuperchromaticCharged(stack, false);
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

    private static void initSuperchromaticMobAttributes(MobEntity mobEntity, boolean ignoreHealth)
    {
        final float initialMaxHealth = mobEntity.getMaxHealth();
        final float healthRatio = mobEntity.getHealth() / initialMaxHealth;
        final AttributeContainer attributes = mobEntity.getAttributes();
        for (Map.Entry<EntityAttribute, Map.Entry<Supplier<Double>, Supplier<Double>>> e : SuperchromaticEntityUtils.ENTITY_ATTRIBUTE_MODIFIERS.entrySet())
        {
            final EntityAttribute attribute = e.getKey();
            if (attributes.hasAttribute(attribute) && (!ignoreHealth || attribute != EntityAttributes.GENERIC_MAX_HEALTH))
            {
                final Double bonus = e.getValue().getKey().get();
                final Double multiplier = e.getValue().getValue().get();
                final EntityAttributeInstance entityAttributeInstance = mobEntity.getAttributeInstance(attribute);
                if (bonus != null && bonus.doubleValue() > 0.0d)
                {
                    if (!entityAttributeInstance.getModifiers().stream().anyMatch(m -> SUPERCHROMATIC_MOB_BONUS.equals(m.getName())))
                        entityAttributeInstance.addPersistentModifier(
                                new EntityAttributeModifier(SUPERCHROMATIC_MOB_BONUS, bonus, EntityAttributeModifier.Operation.ADDITION));
                }
                if (multiplier != null && multiplier.doubleValue() != 1.0d)
                {
                    if (!entityAttributeInstance.getModifiers().stream().anyMatch(m -> SUPERCHROMATIC_MOB_MULTIPLIER.equals(m.getName())))
                        entityAttributeInstance.addPersistentModifier(
                                new EntityAttributeModifier(SUPERCHROMATIC_MOB_MULTIPLIER, multiplier - 1.0d, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
                }
            }
        }
        if (!ignoreHealth)
        {
            final float currentMaxHealth = mobEntity.getMaxHealth();
            if (currentMaxHealth > initialMaxHealth)
                mobEntity.setHealth(currentMaxHealth * healthRatio);
        }
    }

    public static void initSuperchromaticMobAttributes(MobEntity mobEntity)
    {
        initSuperchromaticMobAttributes(mobEntity, false);
    }

    public static void enableSuperchromaticMobAttributes(MobEntity mobEntity)
    {
        initSuperchromaticMobAttributes(mobEntity, true);
    }

    public static void disableSuperchromaticMobAttributes(MobEntity mobEntity)
    {
        final AttributeContainer attributes = mobEntity.getAttributes();
        for (Map.Entry<EntityAttribute, Map.Entry<Supplier<Double>, Supplier<Double>>> e : SuperchromaticEntityUtils.ENTITY_ATTRIBUTE_MODIFIERS.entrySet())
        {
            final EntityAttribute attribute = e.getKey();
            if (attributes.hasAttribute(attribute) && attribute != EntityAttributes.GENERIC_MAX_HEALTH)
            {
                final EntityAttributeInstance entityAttributeInstance = mobEntity.getAttributeInstance(attribute);
                final List<EntityAttributeModifier> spectriteMobModifiers = entityAttributeInstance.getModifiers()
                        .stream().filter(m -> SUPERCHROMATIC_MOB_BONUS.equals(m.getName()) || SUPERCHROMATIC_MOB_MULTIPLIER.equals(m.getName())).collect(Collectors.toList());
                for (EntityAttributeModifier modifier : spectriteMobModifiers)
                    entityAttributeInstance.removeModifier(modifier);
            }
        }
    }

    public static Map.Entry<Integer, Integer> getSuperchromaticEssenceRange(Identifier entityId)
    {
        int min = 0;
        int max = 1;

        try
        {
            final EntityType<?> entityType = Registry.ENTITY_TYPE.getOrEmpty(entityId).orElse(null);
            if (entityType != null)
            {
                final DefaultAttributeContainer entityDefaultAttributes = DefaultAttributeRegistry.get((EntityType<? extends LivingEntity>) entityType);
                final double maxHealth = entityDefaultAttributes.getValue(EntityAttributes.GENERIC_MAX_HEALTH);
                final double attack = entityDefaultAttributes.has(EntityAttributes.GENERIC_ATTACK_DAMAGE)
                        ? entityDefaultAttributes.getValue(EntityAttributes.GENERIC_ATTACK_DAMAGE)
                        : 0D;
                final double armor = entityDefaultAttributes.has(EntityAttributes.GENERIC_ARMOR)
                        ? entityDefaultAttributes.getValue(EntityAttributes.GENERIC_ARMOR)
                        : 0D;
                final boolean isHostile = entityType.getSpawnGroup() == SpawnGroup.MONSTER || attack > 0D;

                healthBasedRangeModifiers:
                {
                    if (maxHealth >= 10D)
                    {
                        min++;
                        max++;
                    } else
                        break healthBasedRangeModifiers;

                    if (maxHealth >= 20D)
                        max++;
                    else
                        break healthBasedRangeModifiers;

                    if (maxHealth >= 40D)
                    {
                        if (isHostile)
                        {
                            min++;
                            max++;
                        }
                    } else
                        break healthBasedRangeModifiers;

                    if (maxHealth >= 60D)
                    {
                        if (isHostile)
                            max++;
                    } else
                        break healthBasedRangeModifiers;

                    if (maxHealth >= 80D)
                    {
                        if (isHostile)
                            min++;
                        max++;
                    } else
                        break healthBasedRangeModifiers;

                    if (maxHealth >= 100D)
                    {
                        if (isHostile)
                            max++;
                    } else
                        break healthBasedRangeModifiers;

                    if (maxHealth >= 200D)
                    {
                        min++;
                        max++;
                    }
                }

                attackBaseRangeModifiers:
                {
                    if (attack >= 5D)
                    {
                        min++;
                        max++;
                    } else
                        break attackBaseRangeModifiers;

                    if (attack >= 7D)
                    {
                        min++;
                        max++;
                    } else
                        break attackBaseRangeModifiers;

                    if (attack >= 10D)
                    {
                        min++;
                        max++;
                    } else
                        break attackBaseRangeModifiers;

                    if (attack >= 12D)
                    {
                        min++;
                        max++;
                    }
                }

                if (isHostile)
                {
                    max++;
                    if (armor >= 4.0D)
                    {
                        min++;
                        max++;
                    }
                }
            } else
            {
                min = 1;
                max = 2;
            }
        } catch (Exception e)
        {
            min = 1;
            max = 2;
        }

        return new AbstractMap.SimpleEntry(min, max);
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
