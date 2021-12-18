package com.flashfyre.spectrite.item;

import com.flashfyre.spectrite.entity.SpectriteBombEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SpectriteBombItem extends SimpleSpectriteItem
{
    public SpectriteBombItem(Settings settings)
    {
        super(settings);
    }

    @Override
    public boolean isFireproof()
    {
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        final ItemStack itemStack = user.getStackInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW,
                SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
        if (!world.isClient)
        {
            final SpectriteBombEntity spectriteBombEntity = new SpectriteBombEntity(world, user);
            spectriteBombEntity.setItem(itemStack);
            spectriteBombEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1.5f, 1.0f);
            world.spawnEntity(spectriteBombEntity);
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode)
            itemStack.decrement(1);
        return TypedActionResult.success(itemStack, world.isClient());
    }
}
