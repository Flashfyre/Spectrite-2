package com.flashfyre.spectrite.item;

import com.flashfyre.spectrite.entity.SpectriteCompatibleMobEntity;
import com.flashfyre.spectrite.util.SpectriteEntityUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class SuperchromaticElixirItem extends SimpleSpectriteItem implements PassiveChargedSpectriteItem
{
    public SuperchromaticElixirItem(Settings settings)
    {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand)
    {
        if (entity instanceof MobEntity mobEntity && mobEntity instanceof SpectriteCompatibleMobEntity spectriteCompatibleMobEntity
                && !(spectriteCompatibleMobEntity).isSuperchromatic())
        {
            SpectriteEntityUtils.setSuperchromatic(entity, true);
            if (!user.world.isClient && entity.isAlive())
            {
                SpectriteEntityUtils.initSuperchromaticMobAttributes(mobEntity);
                mobEntity.setPersistent();
                stack.decrement(1);
            }
            user.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_WITCH_DRINK,
                    SoundCategory.NEUTRAL, 1.0f, 0.8f + user.getRandom().nextFloat() * 0.4f);
            return ActionResult.success(user.world.isClient);
        }
        return ActionResult.PASS;
    }
}
