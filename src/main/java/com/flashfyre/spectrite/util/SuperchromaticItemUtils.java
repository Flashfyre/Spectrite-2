package com.flashfyre.spectrite.util;

import com.flashfyre.spectrite.SpectriteConfig;
import com.flashfyre.spectrite.component.item.ItemComponents;
import com.flashfyre.spectrite.component.item.SuperchromaticItemComponent;
import com.flashfyre.spectrite.entity.player.SuperchromaticCooldownPlayerEntity;
import com.flashfyre.spectrite.entity.player.SuperchromaticItemCooldownManager;
import com.flashfyre.spectrite.item.SpectriteChargeableItem;
import com.flashfyre.spectrite.item.SpectriteWeaponItem;
import com.flashfyre.spectrite.soundEvent.SoundEvents;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SuperchromaticItemUtils
{
    public static final Map<EntityAttribute, Map.Entry<Supplier<Double>, Boolean>> SUPERCHROMATIC_ATTRIBUTE_MODIFIER_MULTIPLIERS = new HashMap<>();

    public static final int SUPERCHROMATIC_CHARGEABLE_USE_TICKS_THRESHOLD = 4;

    public static final HashMap<Item, HashMap<EquipmentSlot, Multimap<EntityAttribute, EntityAttributeModifier>>> SUPERCHROMATIC_SLOT_ATTRIBUTE_MODIFIER_CACHE = new HashMap<>();

    static
    {
        SUPERCHROMATIC_ATTRIBUTE_MODIFIER_MULTIPLIERS.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new AbstractMap.SimpleEntry<>(() -> 2.0D, false));
        SUPERCHROMATIC_ATTRIBUTE_MODIFIER_MULTIPLIERS.put(EntityAttributes.GENERIC_ARMOR, new AbstractMap.SimpleEntry<>(() -> 1.5D, true));
        SUPERCHROMATIC_ATTRIBUTE_MODIFIER_MULTIPLIERS.put(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new AbstractMap.SimpleEntry<>(() -> 2.0D, true));
        SUPERCHROMATIC_ATTRIBUTE_MODIFIER_MULTIPLIERS.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new AbstractMap.SimpleEntry<>(() -> 2.0D, false));
    }

    public static boolean isSuperchromaticCompatible(ItemStack stack)
    {
        return ItemComponents.SUPERCHROMATIC_ITEM.maybeGet(stack).orElse(null) != null;
    }

    public static boolean isSuperchromatic(ItemStack stack)
    {
        final SuperchromaticItemComponent superchromaticItemComponent;
        superchromaticItemComponent = ItemComponents.SUPERCHROMATIC_ITEM.maybeGet(stack).orElse(null);
        return superchromaticItemComponent != null
                && superchromaticItemComponent.getSuperchromaticPoints() >= SuperchromaticItemUtils.getRequiredSuperchromaticPoints(stack);
    }

    public static boolean isSuperchromaticChargeable(ItemStack stack)
    {
        final SuperchromaticItemComponent superchromaticItemComponent;
        superchromaticItemComponent = ItemComponents.SUPERCHROMATIC_ITEM.maybeGet(stack).orElse(null);
        return superchromaticItemComponent != null && superchromaticItemComponent.isChargeable();
    }

    public static boolean isSuperchromaticCharged(ItemStack stack)
    {
        final SuperchromaticItemComponent superchromaticItemComponent;
        superchromaticItemComponent = ItemComponents.SUPERCHROMATIC_ITEM.maybeGet(stack).orElse(null);
        return superchromaticItemComponent != null && superchromaticItemComponent.isCharged();
    }

    public static void setSuperchromaticCharged(ItemStack stack, boolean charged)
    {
        ItemComponents.SUPERCHROMATIC_ITEM.maybeGet(stack).ifPresent(
                superchromaticItemComponent -> superchromaticItemComponent.setCharged(charged));
    }

    public static int getSuperchromaticPoints(ItemStack stack)
    {
        final SuperchromaticItemComponent superchromaticItemComponent;
        superchromaticItemComponent = ItemComponents.SUPERCHROMATIC_ITEM.maybeGet(stack).orElse(null);
        return superchromaticItemComponent != null ? superchromaticItemComponent.getSuperchromaticPoints() : 0;
    }

    public static void setSuperchromaticPoints(ItemStack stack, int superchromaticPoints)
    {
        ItemComponents.SUPERCHROMATIC_ITEM.maybeGet(stack).ifPresent(
                superchromaticItemComponent -> superchromaticItemComponent.setSuperchromaticPoints(superchromaticPoints));
    }

    public static int getRequiredSuperchromaticPoints(ItemStack stack)
    {
        final Item item = stack.getItem();
        return MathHelper.ceil(item.getMaxDamage() / (item instanceof ArmorItem ? 120f : 490f));
    }

    public static boolean isSuperchromaticMeleeWeaponItem(ItemStack stack)
    {
        final Item item = stack.getItem();
        return item instanceof ToolItem && !(item instanceof HoeItem) && isSuperchromatic(stack);
    }

    public static float getSuperchromaticToolItemChargedEfficiencyMultiplier(Item item)
    {
        return 1.5f;
    }

    public static float getSuperchromaticWeaponItemDamageMultiplier(Item item)
    {
        if (item instanceof ToolItem)
        {
            if (item instanceof PickaxeItem)
                return 12f;
            if (item instanceof ShovelItem)
                return 7f;
            if (item instanceof SwordItem)
                return 1f;
        } else if (item instanceof BowItem)
            return 1.5f;
        return 2f;
    }

    public static float getSuperchromaticChargeableItemCooldownMultiplier(Item item)
    {
        if (item instanceof ToolItem)
        {
            if (item instanceof PickaxeItem)
                return 0.75f;
            if (item instanceof ShovelItem)
                return 1.25f;
        }
        return 1f;
    }

    public static TypedActionResult<ItemStack> useSuperchromaticOrSpectriteChargeableItem(PlayerEntity user, Hand hand,
                                                                                          boolean bypassOffHandCheck)
    {
        return useSuperchromaticOrSpectriteChargeableItem(user, user.getStackInHand(hand), bypassOffHandCheck);
    }

    public static TypedActionResult<ItemStack> useSuperchromaticOrSpectriteChargeableItem(PlayerEntity user, ItemStack itemStack,
                                                                                          boolean bypassOffHandCheck)
    {
        if (((SuperchromaticCooldownPlayerEntity) user).getSuperchromaticItemCooldownManager().isCoolingDown())
            return TypedActionResult.pass(itemStack);

        final Item item = itemStack.getItem();

        if (!bypassOffHandCheck && user.getMainHandStack() == itemStack)
        {
            final ItemStack offHandStack = user.getOffHandStack();
            if (!(offHandStack.getItem() instanceof SpectriteChargeableItem)
                    && !SuperchromaticItemUtils.isSuperchromaticChargeable(offHandStack)
                    && offHandStack.getMaxUseTime() > SUPERCHROMATIC_CHARGEABLE_USE_TICKS_THRESHOLD)
                return TypedActionResult.pass(itemStack);
        }

        final boolean charged;
        if (item instanceof SpectriteChargeableItem spectriteChargeableItem)
        {
            charged = !spectriteChargeableItem.isCharged(itemStack);
            spectriteChargeableItem.setCharged(itemStack, charged);
        } else
        {
            charged = !SuperchromaticItemUtils.isSuperchromaticCharged(itemStack);
            SuperchromaticItemUtils.setSuperchromaticCharged(itemStack, charged);
        }

        if (user.world.isClient)
        {
            final SoundEvent soundEvent = charged
                    ? SoundEvents.CHARGE
                    : SoundEvents.UNCHARGE;
            user.playSound(soundEvent, 0.25F, 1.0F);
        }

        return TypedActionResult.consume(itemStack);
    }

    public static boolean stopUsingSuperchromaticOrSpectriteChargeableItem(LivingEntity user, ItemStack itemStack, int remainingUseTicks)
    {
        final int maxUseTime = itemStack.getMaxUseTime();
        final int useTicks = maxUseTime - remainingUseTicks;

        if (useTicks <= SuperchromaticItemUtils.SUPERCHROMATIC_CHARGEABLE_USE_TICKS_THRESHOLD && user instanceof PlayerEntity playerEntity)
        {
            useSuperchromaticOrSpectriteChargeableItem(playerEntity, itemStack, true);
            return true;
        }

        return false;
    }

    public static void tryActivateSuperchromaticOrSpectriteChargeableItemCooldown(PlayerEntity playerEntity, int power, ItemStack itemStack)
    {
        if (playerEntity != null && !playerEntity.isCreative())
        {
            final SuperchromaticItemCooldownManager superchromaticItemCooldownManager = ((SuperchromaticCooldownPlayerEntity) playerEntity).getSuperchromaticItemCooldownManager();
            final Item item = itemStack.getItem();
            final float baseCooldown;
            final float cooldownMultiplier;
            if (item instanceof SpectriteWeaponItem spectriteWeaponItem)
            {
                baseCooldown = SpectriteConfig.getSpectriteToolCooldown();
                cooldownMultiplier = spectriteWeaponItem.getCooldownMultiplier();
            } else
            {
                baseCooldown = SpectriteConfig.getSuperchromaticToolCooldown();
                cooldownMultiplier = getSuperchromaticChargeableItemCooldownMultiplier(item);
            }
            final float cooldown = baseCooldown * (2.5f * (float) Math.pow(2, power)) * cooldownMultiplier;
            superchromaticItemCooldownManager.set((int) Math.max(cooldown, Math.round(superchromaticItemCooldownManager.getCooldownProgress(0f))));
        }
    }

    public static int getSuperchromaticItemChromaBlastLevel(Item item)
    {
        if (item instanceof ToolItem || item instanceof TridentItem)
        {
            Map.Entry<EntityAttribute, EntityAttributeModifier> itemAttackAttributeEntry =
                    item.getAttributeModifiers(EquipmentSlot.MAINHAND).entries()
                            .stream().filter(a -> a.getKey() == EntityAttributes.GENERIC_ATTACK_DAMAGE)
                            .findFirst().orElse(null);
            if (itemAttackAttributeEntry != null)
            {
                final double attack = itemAttackAttributeEntry.getValue().getValue();
                if (item instanceof ToolItem toolItem)
                {
                    final double baseAttack = attack - toolItem.getMaterial().getAttackDamage();
                    if (baseAttack < 0.5D)
                        return 0;
                    if (baseAttack < 1.0D)
                        return 1;
                    if (baseAttack < 2.0D)
                        return 2;
                    if (baseAttack < 4.0D)
                        return 3;
                    return 4;
                }
                if (attack < 1.0D)
                    return 0;
                if (attack < 3.0D)
                    return 1;
                if (attack < 6.0D)
                    return 2;
                if (attack < 10.0D)
                    return 3;
                return 4;
            }
        } else if (item instanceof BowItem)
            return 1;
        return 0;
    }

    public static int getSuperchromaticItemPassiveChromaBlastLevel(Item item)
    {
        if (item instanceof BowItem)
            return item.getMaxDamage() < 490 ? 0 : item.getMaxDamage() < 2100 ? 1 : 2;
        return 0;
    }
}
