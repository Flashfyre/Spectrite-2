package com.flashfyre.spectrite.mixin;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Random;

@Mixin(Explosion.class)
public interface ExplosionAccessor
{
    @Accessor("random")
    Random getRandom();

    @Accessor("affectedBlocks")
    List<BlockPos> getAffectedBlocks();

    @Invoker("tryMergeStack")
    static void invokeTryMergeStack(ObjectArrayList<Pair<ItemStack, BlockPos>> stacks, ItemStack stack, BlockPos pos)
    {
        throw new AssertionError();
    }
}
