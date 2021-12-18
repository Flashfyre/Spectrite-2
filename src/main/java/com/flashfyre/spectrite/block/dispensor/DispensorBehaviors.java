package com.flashfyre.spectrite.block.dispensor;

import com.flashfyre.spectrite.entity.SpectriteBombEntity;
import com.flashfyre.spectrite.item.Items;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public class DispensorBehaviors
{
    public static void initDispenserBehaviors()
    {
        DispenserBlock.registerBehavior(Items.SPECTRITE_BOMB, new ProjectileDispenserBehavior()
        {
            @Override
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack)
            {
                return Util.make(new SpectriteBombEntity(world, position.getX(), position.getY(), position.getZ()), entity -> entity.setItem(stack));
            }
        });
    }
}
