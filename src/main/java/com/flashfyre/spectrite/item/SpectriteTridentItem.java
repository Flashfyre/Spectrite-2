package com.flashfyre.spectrite.item;

import com.flashfyre.spectrite.util.SpectriteItemUtils;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class SpectriteTridentItem extends TridentItem implements SpectriteWeaponItem
{
    private boolean depleted;

    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public SpectriteTridentItem(boolean depleted, Settings settings)
    {
        super(settings);
        this.depleted = depleted;
        final ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        for (Map.Entry<EntityAttribute, EntityAttributeModifier> e : super.getAttributeModifiers(EquipmentSlot.MAINHAND).entries())
        {
            if (e.getKey() == EntityAttributes.GENERIC_ATTACK_DAMAGE)
            {
                final EntityAttributeModifier originalAttackAttributeModifier = e.getValue();
                final EntityAttributeModifier attackAttributeModifier = new EntityAttributeModifier(
                        originalAttackAttributeModifier.getId(), originalAttackAttributeModifier.getName(),
                        originalAttackAttributeModifier.getValue() + (depleted ? -2d : 1d),
                        originalAttackAttributeModifier.getOperation());
                builder.put(e.getKey(), attackAttributeModifier);
            } else
                builder.put(e.getKey(), e.getValue());
        }
        attributeModifiers = builder.build();
    }

    @Override
    public boolean isDepleted()
    {
        return depleted;
    }

    @Override
    public int getSpectriteDamageLevel()
    {
        return 4;
    }

    @Override
    public float getStackDamageMultiplier()
    {
        return 1f;
    }

    @Override
    public float getCooldownMultiplier()
    {
        return 0.75f;
    }

    @Override
    public boolean isFireproof()
    {
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
    {
        SpectriteItemUtils.appendSpectriteDamageableItemTooltip(stack, world, tooltip, context);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot)
    {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        final boolean ret = super.postHit(stack, target, attacker);
        return SpectriteItemUtils.spectriteWeaponPostHit(ret, stack, target, attacker);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
    {
        final int maxUseTime = stack.getMaxUseTime();
        final int useTicks = maxUseTime - remainingUseTicks;
        if (useTicks <= SpectriteItemUtils.SPECTRITE_CHARGEABLE_USE_TICKS_THRESHOLD && user instanceof PlayerEntity playerEntity)
            SpectriteItemUtils.useSpectriteChargeableItem(playerEntity, stack, true);

        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
    {
        SpectriteItemUtils.spectriteDamageableItemInventoryTick(stack, world, entity, slot);
    }
}
