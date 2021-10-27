package com.flashfyre.spectrite.util;

import com.flashfyre.spectrite.item.Items;
import com.flashfyre.spectrite.item.SpectriteChargeableItem;
import com.flashfyre.spectrite.item.SpectriteDamageableItem;
import com.flashfyre.spectrite.item.SpectriteWeaponItem;
import com.flashfyre.spectrite.mixin.PlayerInventoryAccessor;
import com.flashfyre.spectrite.soundEvent.SoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class SpectriteItemUtils
{
    public static final int SPECTRITE_CHARGEABLE_USE_TICKS_THRESHOLD = 4;

    public static TypedActionResult<ItemStack> useSpectriteChargeableItem(PlayerEntity user, Hand hand,
                                                                          boolean bypassOffHandCheck)
    {
        return useSpectriteChargeableItem(user, user.getStackInHand(hand), bypassOffHandCheck);
    }

    public static TypedActionResult<ItemStack> useSpectriteChargeableItem(PlayerEntity user, ItemStack itemStack,
                                                                          boolean bypassOffHandCheck)
    {
        final SpectriteChargeableItem spectriteChargeableItem = (SpectriteChargeableItem) itemStack.getItem();
        if (!spectriteChargeableItem.isDepleted())
        {
            if (!bypassOffHandCheck && user.getMainHandStack() == itemStack)
            {
                final ItemStack offHandStack = user.getOffHandStack();
                if (!(offHandStack.getItem() instanceof SpectriteChargeableItem)
                        && offHandStack.getMaxUseTime() >= SPECTRITE_CHARGEABLE_USE_TICKS_THRESHOLD)
                    return TypedActionResult.pass(itemStack);
            }
            final boolean charged = !spectriteChargeableItem.isCharged(itemStack);
            spectriteChargeableItem.setCharged(itemStack, charged);
            final SoundEvent soundEvent = charged
                    ? SoundEvents.CHARGE
                    : SoundEvents.UNCHARGE;
            user.playSound(soundEvent, 0.8F, 1.0F);

            return TypedActionResult.consume(itemStack);
        }

        return TypedActionResult.pass(itemStack);
    }

    public static boolean spectriteWeaponPostHit(boolean result, ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        final PlayerEntity playerEntity = attacker instanceof PlayerEntity
                ? (PlayerEntity) attacker
                : null;
        final SpectriteWeaponItem spectriteWeaponItem = (SpectriteWeaponItem) stack.getItem();
        if (result && (playerEntity == null
                || playerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), 0f) == 0f)
                && spectriteWeaponItem.isCharged(stack) && !spectriteWeaponItem.isDepleted())
        {
            if (playerEntity == null || !(target instanceof PlayerEntity targetPlayer)
                    || playerEntity.shouldDamagePlayer(targetPlayer))
            {
                final int power = spectriteWeaponItem.getSpectriteDamageLevel();
                stack.damage((int) Math.pow(power, 3f), attacker, (e) -> e.sendToolBreakStatus(playerEntity.getActiveHand()));

                if (!attacker.world.isClient)
                {
                    target.hurtTime = 0;

                    SpectriteUtils.newSpectriteExplosion(attacker.world, attacker, target, null,
                            target.getX(), attacker.getBoundingBox().minY + attacker.getHeight() / 2f, target.getZ(),
                            power, false, Explosion.DestructionType.NONE);

                    if (playerEntity != null)
                        SpectriteUtils.tryActivateSpectriteChargeableItemCooldown(playerEntity, power, stack);
                }

                spectriteWeaponItem.setCharged(stack, false);

                return true;
            }
        }

        return result;
    }

    public static void stopUsingSpectriteChargeableItem(LivingEntity user, ItemStack itemStack, int remainingUseTicks)
    {
        final int maxUseTime = itemStack.getMaxUseTime();
        final int useTicks = maxUseTime - remainingUseTicks;
        if (useTicks <= SpectriteItemUtils.SPECTRITE_CHARGEABLE_USE_TICKS_THRESHOLD && user instanceof PlayerEntity playerEntity)
            useSpectriteChargeableItem(playerEntity, itemStack, true);
    }

    public static void spectriteDamageableItemInventoryTick(ItemStack stack, World world, Entity entity, int slot)
    {
        if (((SpectriteDamageableItem) stack.getItem()).isDepleted())
        {
            final int newDamage = stack.getDamage() - 1;
            if (newDamage > 0)
            {
                if (stack.getDamage() > 0 && world.getTime() % 13 == 0)
                    stack.setDamage(newDamage);
            } else
            {

                if (entity instanceof PlayerEntity playerEntity)
                {
                    for (DefaultedList<ItemStack> inventory : ((PlayerInventoryAccessor) playerEntity.getInventory()).getCombinedInventory())
                    {
                        if (inventory.size() > slot && inventory.get(slot) == stack)
                        {
                            final Item depletedItem = stack.getItem();
                            final Item originalItem = Items.DEPLETED_DAMAGEABLE_ITEMS_MAP.entrySet().stream()
                                    .filter(e -> e.getValue() == depletedItem).map(e -> e.getKey()).findFirst().orElse(null);

                            if (originalItem != null)
                            {
                                final ItemStack restoredStack = new ItemStack(originalItem);
                                if (stack.hasTag())
                                    restoredStack.setTag(stack.getTag());
                                restoredStack.setDamage(1);
                                inventory.set(slot, restoredStack);
                            }
                        }
                    }
                }
            }
        }
    }
}
