package com.flashfyre.spectrite.etc;

import com.flashfyre.spectrite.Spectrite;
import com.flashfyre.spectrite.mixin.ExplosionAccessor;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SpectriteExplosion extends Explosion
{
    private final boolean createFire;
    private final Explosion.DestructionType destructionType;
    private final Random random;
    private final World world;
    private final double x;
    private final double y;
    private final double z;
    @Nullable
    private final Entity entity;
    @Nullable
    private final Entity targetEntity;
    private final float power;
    private final List<BlockPos> affectedBlocks;

    public SpectriteExplosion(World world, @Nullable Entity entity, @Nullable Entity targetEntity, double x, double y, double z,
                              float power, List<BlockPos> affectedBlocks)
    {
        super(world, entity, x, y, z, power, false, DestructionType.DESTROY, affectedBlocks);
        this.random = ((ExplosionAccessor) this).getRandom();
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.entity = entity;
        this.targetEntity = targetEntity;
        this.power = power;
        this.createFire = false;
        this.destructionType = DestructionType.DESTROY;
        this.affectedBlocks = ((ExplosionAccessor) this).getAffectedBlocks();
    }

    public SpectriteExplosion(World world, @Nullable Entity entity, @Nullable Entity targetEntity,
                              @Nullable DamageSource damageSource, @Nullable ExplosionBehavior behavior,
                              double x, double y, double z, float power, boolean createFire,
                              Explosion.DestructionType destructionType)
    {
        super(world, entity, damageSource, behavior, x, y, z, power, createFire, destructionType);
        this.random = ((ExplosionAccessor) this).getRandom();
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.entity = entity;
        this.targetEntity = targetEntity;
        this.power = power;
        this.createFire = createFire;
        this.destructionType = destructionType;
        this.affectedBlocks = ((ExplosionAccessor) this).getAffectedBlocks();
    }

    @Override
    public void affectWorld(boolean particles)
    {
        if (this.world.isClient)
        {
            if (power >= 1)
            {
                if (power >= 6)
                {
                    world.playSound(this.x, this.y, this.z, com.flashfyre.spectrite.soundEvent.SoundEvents.EXPLOSION, SoundCategory.NEUTRAL, 0.75F + Math.max(power, 0F),
                            0.5F + (world.random.nextFloat()) * 0.4F, false);
                    world.playSound(this.x, this.y, this.z, com.flashfyre.spectrite.soundEvent.SoundEvents.FATALITY, SoundCategory.NEUTRAL, 1.0F + Math.max(power, 0F),
                            0.75F, false);
                } else if (power >= 5)
                {
                    world.playSound(this.x, this.y, this.z, com.flashfyre.spectrite.soundEvent.SoundEvents.EXPLOSION, SoundCategory.NEUTRAL, 0.75F + Math.max(power, 0F),
                            0.75F + (world.random.nextFloat()) * 0.4F, false);
                    world.playSound(this.x, this.y, this.z, com.flashfyre.spectrite.soundEvent.SoundEvents.FATALITY, SoundCategory.NEUTRAL, 1.0F + Math.max(power, 0F),
                            1.0F, false);
                } else if (power >= 4)
                    world.playSound(this.x, this.y, this.z, com.flashfyre.spectrite.soundEvent.SoundEvents.EXPLOSION, SoundCategory.NEUTRAL, 0.75F + Math.max(power, 0F),
                            1.0F + (world.random.nextFloat()) * 0.4F, false);
                else if (power >= 3)
                    world.playSound(this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.NEUTRAL, 0.75F + Math.max(power, 0F),
                            1.0F + (world.random.nextFloat()) * 0.4F, false);
                else if (power >= 2)
                    world.playSound(this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.NEUTRAL, 0.75F + Math.max(power, 0F),
                            1.6F + (world.random.nextFloat()) * 0.4F, false);
                else
                    world.playSound(this.x, this.y, this.z, SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, SoundCategory.NEUTRAL, 1.0F + Math.max(power, 0F),
                            1.0F + (world.random.nextFloat()) * 0.4F, false);
            }
        }

        boolean bl = this.destructionType != Explosion.DestructionType.NONE;
        if (particles && world.isClient)
        {
            Spectrite.INSTANCE.spawnSpectriteExplosionParticle(this.x, this.y, this.z, 0, 0, 0);
            if (power > 1)
                Spectrite.INSTANCE.spawnSpectriteExplosionEmitterParticle(this.world, this.x, this.y, this.z, power);
        }

        if (bl)
        {
            final ObjectArrayList<Pair<ItemStack, BlockPos>> objectArrayList = new ObjectArrayList();
            Collections.shuffle(this.affectedBlocks, this.world.random);
            final Iterator var4 = this.affectedBlocks.iterator();

            while (var4.hasNext())
            {
                final BlockPos blockPos = (BlockPos) var4.next();
                final BlockState blockState = this.world.getBlockState(blockPos);
                final Block block = blockState.getBlock();
                if (!blockState.isAir())
                {
                    final BlockPos blockPos2 = blockPos.toImmutable();
                    this.world.getProfiler().push("explosion_blocks");
                    if (block.shouldDropItemsOnExplosion(this) && this.world instanceof ServerWorld)
                    {
                        BlockEntity blockEntity = blockState.hasBlockEntity() ? this.world.getBlockEntity(blockPos) : null;
                        LootContext.Builder builder = (new LootContext.Builder((ServerWorld) this.world)).random(this.world.random).parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(blockPos)).parameter(LootContextParameters.TOOL, ItemStack.EMPTY).optionalParameter(LootContextParameters.BLOCK_ENTITY, blockEntity).optionalParameter(LootContextParameters.THIS_ENTITY, this.entity);
                        if (this.destructionType == Explosion.DestructionType.DESTROY)
                            builder.parameter(LootContextParameters.EXPLOSION_RADIUS, this.power);

                        blockState.getDroppedStacks(builder).forEach((stack) ->
                        {
                            ExplosionAccessor.invokeTryMergeStack(objectArrayList, stack, blockPos2);
                        });
                    }

                    this.world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                    block.onDestroyedByExplosion(this.world, blockPos, this);
                    this.world.getProfiler().pop();
                }
            }

            final ObjectListIterator var12 = objectArrayList.iterator();

            while (var12.hasNext())
            {
                final Pair<ItemStack, BlockPos> pair = (Pair) var12.next();
                Block.dropStack(this.world, pair.getSecond(), pair.getFirst());
            }
        }

        if (this.createFire)
        {
            final Iterator var11 = this.affectedBlocks.iterator();

            while (var11.hasNext())
            {
                final BlockPos blockPos3 = (BlockPos) var11.next();
                if (this.random.nextInt(3) == 0 && this.world.getBlockState(blockPos3).isAir()
                        && this.world.getBlockState(blockPos3.down()).isOpaqueFullCube(this.world, blockPos3.down()))
                    this.world.setBlockState(blockPos3, AbstractFireBlock.getState(this.world, blockPos3));
            }
        }
    }

    public Entity getTargetEntity()
    {
        return targetEntity;
    }
}
