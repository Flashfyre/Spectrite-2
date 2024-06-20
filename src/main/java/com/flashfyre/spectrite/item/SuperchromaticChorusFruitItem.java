package com.flashfyre.spectrite.item;

import com.flashfyre.spectrite.world.Dimensions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class SuperchromaticChorusFruitItem extends SimpleSpectriteItem implements SpectriteItem
{
    public SuperchromaticChorusFruitItem(Settings settings)
    {
        super(settings, true);
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
            user.moveToWorld(world.getRegistryKey() != Dimensions.SUPERCHROMATIC
                    ? ((ServerWorld) world).getServer().getWorld(Dimensions.SUPERCHROMATIC)
                    : ((ServerWorld) world).getServer().getWorld(World.OVERWORLD));
        }
        return itemStack;
    }
}
