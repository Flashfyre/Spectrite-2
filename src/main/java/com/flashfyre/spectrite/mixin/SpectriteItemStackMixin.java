package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.item.SpectriteChargeableItem;
import com.flashfyre.spectrite.item.SpectriteToolItem;
import com.flashfyre.spectrite.util.SuperchromaticItemUtils;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Mixin(ItemStack.class)
public class SpectriteItemStackMixin
{
    @Inject(method = "getMiningSpeedMultiplier", at = @At(value = "RETURN"), cancellable = true)
    private void injectGetMiningSpeedMultiplierAddSuperchromaticChargedMultiplier(BlockState state, CallbackInfoReturnable<Float> cir)
    {
        final ItemStack stack = (ItemStack) (Object) this;
        final Item item = stack.getItem();
        if ((item instanceof ToolItem && ((item instanceof SpectriteChargeableItem)
                || (SuperchromaticItemUtils.isSuperchromatic(stack) && SuperchromaticItemUtils.isSuperchromaticChargeable(stack))))
                && cir.getReturnValue() > 1.0f)
        {
            final boolean isSuperchromaticCharged = item instanceof SpectriteToolItem
                    ? ((SpectriteToolItem) item).isCharged(stack)
                    : SuperchromaticItemUtils.isSuperchromaticCharged(stack);
            if (isSuperchromaticCharged)
            {
                final float chargedEfficiencyMultiplier = item instanceof SpectriteToolItem
                        ? ((SpectriteToolItem) item).getChargedEfficiencyMultiplier()
                        : SuperchromaticItemUtils.getSuperchromaticToolItemChargedEfficiencyMultiplier(item);
                cir.setReturnValue(cir.getReturnValue() * chargedEfficiencyMultiplier);
            } else if (!(item instanceof SpectriteToolItem))
            {
                final float passiveEfficiencyMultiplier = SuperchromaticItemUtils.getSuperchromaticToolItemPassiveEfficiencyMultiplier(item);
                cir.setReturnValue(cir.getReturnValue() * passiveEfficiencyMultiplier);
            }
        }
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void injectUseSuperchromaticOrSpectriteChargeableItem(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir)
    {
        final ItemStack stack = (ItemStack) (Object) this;
        final Item item = stack.getItem();
        if (item instanceof ToolItem && ((item instanceof SpectriteChargeableItem && !((SpectriteChargeableItem) item).isDepleted())
                || (SuperchromaticItemUtils.isSuperchromatic(stack) && SuperchromaticItemUtils.isSuperchromaticChargeable(stack))))
        {
            final TypedActionResult<ItemStack> ret = SuperchromaticItemUtils.useSuperchromaticOrSpectriteChargeableItem(user, hand, false);
            if (ret.getResult() != ActionResult.PASS)
                cir.setReturnValue(ret);
        }
    }

    @Inject(method = "onStoppedUsing", at = @At("HEAD"), cancellable = true)
    private void injectOnStoppedUsingChargeSuperchromaticOrSpectriteChargeableItem(World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci)
    {
        if (user instanceof PlayerEntity playerEntity)
        {
            final ItemStack activeHandStack = playerEntity.getStackInHand(playerEntity.getActiveHand());
            final Item activeHandItem = activeHandStack.getItem();
            if ((activeHandItem instanceof SpectriteChargeableItem && !((SpectriteChargeableItem) activeHandItem).isDepleted())
                    || (SuperchromaticItemUtils.isSuperchromatic(activeHandStack) && SuperchromaticItemUtils.isSuperchromaticChargeable(activeHandStack)))
            {
                final boolean result = SuperchromaticItemUtils.stopUsingSuperchromaticOrSpectriteChargeableItem(user, activeHandStack, remainingUseTicks);
                if (result && activeHandItem instanceof BowItem)
                    ci.cancel();
            } else
            {
                final ItemStack otherHandStack = playerEntity.getActiveHand() == Hand.MAIN_HAND
                        ? playerEntity.getOffHandStack()
                        : playerEntity.getMainHandStack();
                final Item otherHandItem = otherHandStack.getItem();
                if ((otherHandItem instanceof SpectriteChargeableItem && !((SpectriteChargeableItem) otherHandItem).isDepleted())
                        || (SuperchromaticItemUtils.isSuperchromatic(otherHandStack) && SuperchromaticItemUtils.isSuperchromaticChargeable(otherHandStack)))
                    SuperchromaticItemUtils.stopUsingSuperchromaticOrSpectriteChargeableItem(user, otherHandStack, remainingUseTicks);
            }
        }
    }

    @Inject(method = "getAttributeModifiers", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/Item;getAttributeModifiers(Lnet/minecraft/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;"),
            cancellable = true)
    private void injectGetAttributeModifiersSubstituteSuperchromaticModifiers(EquipmentSlot slot,
                                                                              CallbackInfoReturnable<Multimap<EntityAttribute, EntityAttributeModifier>> cir)
    {
        final ItemStack itemStack = (ItemStack) (Object) this;
        if (SuperchromaticItemUtils.isSuperchromatic(itemStack))
        {
            final Item item = itemStack.getItem();
            if (!SuperchromaticItemUtils.SUPERCHROMATIC_SLOT_ATTRIBUTE_MODIFIER_CACHE.containsKey(item))
                SuperchromaticItemUtils.SUPERCHROMATIC_SLOT_ATTRIBUTE_MODIFIER_CACHE.put(item, new HashMap<>());
            final Multimap<EntityAttribute, EntityAttributeModifier> slotModifiers = item.getAttributeModifiers(slot);
            if (!SuperchromaticItemUtils.SUPERCHROMATIC_SLOT_ATTRIBUTE_MODIFIER_CACHE.get(item).containsKey(slot))
            {
                if (slotModifiers.isEmpty())
                    return;
                final ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> newSlotModifiersBuilder = new ImmutableMultimap.Builder<>();
                for (Map.Entry<EntityAttribute, EntityAttributeModifier> e : slotModifiers.entries())
                {
                    final EntityAttribute attribute = e.getKey();
                    EntityAttributeModifier modifier = e.getValue();
                    if (SuperchromaticItemUtils.SUPERCHROMATIC_ATTRIBUTE_MODIFIER_MULTIPLIERS.containsKey(attribute))
                    {
                        double baseValue = 0f;
                        if (attribute == EntityAttributes.GENERIC_ATTACK_DAMAGE && item instanceof ToolItem toolItem)
                            baseValue = modifier.getValue() - toolItem.getMaterial().getAttackDamage();
                        final Map.Entry<Supplier<Double>, Boolean> multiplierEntry = SuperchromaticItemUtils.SUPERCHROMATIC_ATTRIBUTE_MODIFIER_MULTIPLIERS.get(attribute);
                        final double newValue = baseValue + (modifier.getValue() - baseValue) * multiplierEntry.getKey().get();
                        modifier = new EntityAttributeModifier(modifier.getId(), modifier.getName(),
                                multiplierEntry.getValue() ? MathHelper.ceil(newValue) : newValue, modifier.getOperation());
                    }
                    newSlotModifiersBuilder.put(attribute, modifier);
                }
                SuperchromaticItemUtils.SUPERCHROMATIC_SLOT_ATTRIBUTE_MODIFIER_CACHE.get(item).put(slot, newSlotModifiersBuilder.build());
            }
            cir.setReturnValue(SuperchromaticItemUtils.SUPERCHROMATIC_SLOT_ATTRIBUTE_MODIFIER_CACHE.get(item).get(slot));
        }
    }
}
