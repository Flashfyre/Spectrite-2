package com.flashfyre.spectrite.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.floatprovider.ClampedNormalFloatProvider;
import net.minecraft.world.World;

public class SuperchromaticChorusFruitItem extends SimpleSpectriteItem implements SpectriteItem
{
    public SuperchromaticChorusFruitItem(Settings settings)
    {
        super(settings);
    }

    @Override
    public boolean isFireproof()
    {
        return true;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
    {
        ItemStack itemStack = super.finishUsing(stack, world, user);
        if (!world.isClient)
        {
            final double d = user.getX();
            final double e = user.getY();
            final double f = user.getZ();
            final float xMultiplier = MathHelper.abs(ClampedNormalFloatProvider.get(user.getRandom(), 0.0f, 3.0f, -12.0f, 12.0f));
            final float yMultiplier = MathHelper.abs(ClampedNormalFloatProvider.get(user.getRandom(), 0.0f, 3.0f, -12.0f, 12.0f));
            final float zMultiplier = MathHelper.abs(ClampedNormalFloatProvider.get(user.getRandom(), 0.0f, 3.0f, -12.0f, 12.0f));
            final float avgMultiplier = (xMultiplier + yMultiplier + zMultiplier) / 3f;
            final float soundPitch = 1f + 0.585f * (avgMultiplier < 1.0f ? (1f - avgMultiplier) : (avgMultiplier - 1.0f) / -11.0f);

            final int xRange = MathHelper.ceil(16f * xMultiplier);
            final int yRange = MathHelper.ceil(16f * yMultiplier);
            final int zRange = MathHelper.ceil(16f * zMultiplier);
            for (int i = 0; i < 16; ++i)
            {
                final double g = user.getX() + (user.getRandom().nextDouble() - 0.5d) * xRange;
                final double h = MathHelper.clamp(user.getY() + (user.getRandom().nextInt(yRange) - (yRange / 2.0d)),
                        world.getBottomY(), (world.getBottomY() + ((ServerWorld) world).getLogicalHeight() - 1));
                final double j = user.getZ() + (user.getRandom().nextDouble() - 0.5) * zRange;
                if (user.hasVehicle())
                    user.stopRiding();
                if (!user.teleport(g, h, j, true))
                    continue;
                final SoundEvent soundEvent = user instanceof FoxEntity ? SoundEvents.ENTITY_FOX_TELEPORT : SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
                world.playSound(null, d, e, f, soundEvent, SoundCategory.PLAYERS, 1.0f, soundPitch);
                if (xMultiplier >= 2f || yMultiplier >= 2f || zMultiplier >= 2f)
                    world.playSound(null, g, h, j, soundEvent, SoundCategory.PLAYERS, 1.0f, soundPitch);
                user.playSound(soundEvent, 1.0f, soundPitch);
                break;
            }
            if (user instanceof PlayerEntity playerEntity)
                playerEntity.getItemCooldownManager().set(this, playerEntity.isCreative() ? 20 : 200);
        }
        return itemStack;
    }
}
