package com.flashfyre.spectrite.mixin;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Mixin(Explosion.class)
public interface ExplosionAccessor
{
    @Accessor("random")
    Random getRandom();

    @Accessor("behavior")
    ExplosionBehavior getBehavior();

    @Accessor("affectedBlocks")
    List<BlockPos> getAffectedBlocks();

    @Accessor("affectedPlayers")
    Map<PlayerEntity, Vec3d> getAffectedPlayers();

    @Invoker("tryMergeStack")
    static void invokeTryMergeStack(ObjectArrayList<Pair<ItemStack, BlockPos>> stacks, ItemStack stack, BlockPos pos)
    {
        throw new AssertionError();
    }
}
