package com.flashfyre.spectrite.etc;

import com.flashfyre.spectrite.Spectrite;
import com.flashfyre.spectrite.entity.effect.StatusEffects;
import com.flashfyre.spectrite.mixin.ExplosionAccessor;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ChromaBlast extends Explosion
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

    public ChromaBlast(World world, @Nullable Entity entity, @Nullable Entity targetEntity, double x, double y, double z,
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

    public ChromaBlast(World world, @Nullable Entity entity, @Nullable Entity targetEntity,
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
    public void collectBlocksAndDamageEntities()
    {
        this.world.emitGameEvent(this.entity, GameEvent.EXPLODE, new BlockPos(this.x, this.y, this.z));
        final Set<BlockPos> set = Sets.newHashSet();

        int k;
        int l;
        for (int j = 0; j < 16; ++j)
        {
            for (k = 0; k < 16; ++k)
            {
                for (l = 0; l < 16; ++l)
                {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15)
                    {
                        double d = ((float) j / 15.0F * 2.0F - 1.0F);
                        double e = ((float) k / 15.0F * 2.0F - 1.0F);
                        double f = ((float) l / 15.0F * 2.0F - 1.0F);
                        final double g = Math.sqrt(d * d + e * e + f * f);
                        d /= g;
                        e /= g;
                        f /= g;
                        float h = this.power * (0.7F + this.world.random.nextFloat() * 0.6F);
                        double m = this.x;
                        double n = this.y;
                        double o = this.z;

                        for (; h > 0.0F; h -= 0.22500001F)
                        {
                            BlockPos blockPos = new BlockPos(m, n, o);
                            BlockState blockState = this.world.getBlockState(blockPos);
                            FluidState fluidState = this.world.getFluidState(blockPos);
                            if (!this.world.isInBuildLimit(blockPos))
                                break;

                            final Optional<Float> optional = ((ExplosionAccessor) this).getBehavior().getBlastResistance(this, this.world, blockPos, blockState, fluidState);
                            if (optional.isPresent())
                                h -= (optional.get() + 0.3F) * 0.3F;

                            if (h > 0.0F && ((ExplosionAccessor) this).getBehavior().canDestroyBlock(this, this.world, blockPos, blockState, h))
                                set.add(blockPos);

                            m += d * 0.30000001192092896D;
                            n += e * 0.30000001192092896D;
                            o += f * 0.30000001192092896D;
                        }
                    }
                }
            }
        }

        this.affectedBlocks.addAll(set);
        final float q = this.power * 2.0F;
        k = MathHelper.floor(this.x - (double) q - 1.0D);
        l = MathHelper.floor(this.x + (double) q + 1.0D);
        final int t = MathHelper.floor(this.y - (double) q - 1.0D);
        final int u = MathHelper.floor(this.y + (double) q + 1.0D);
        final int v = MathHelper.floor(this.z - (double) q - 1.0D);
        final int w = MathHelper.floor(this.z + (double) q + 1.0D);
        final List<Entity> list = this.world.getOtherEntities(this.entity, new Box(k, t, v, l, u, w));
        final Vec3d vec3d = new Vec3d(this.x, this.y, this.z);

        modifyEntitiesList(list);

        for (int x = 0; x < list.size(); ++x)
        {
            final Entity entity = list.get(x);
            if (!entity.isImmuneToExplosion())
            {
                double y = entity == targetEntity ? 0f : Math.sqrt(entity.squaredDistanceTo(vec3d)) / (double) q;
                if (y <= 1.0D)
                {
                    double z = entity.getX() - this.x;
                    double aa = (entity instanceof TntEntity ? entity.getY() : entity.getEyeY()) - this.y;
                    double ab = entity.getZ() - this.z;
                    final double ac = Math.sqrt(z * z + aa * aa + ab * ab);
                    if (ac != 0.0D)
                    {
                        z /= ac;
                        aa /= ac;
                        ab /= ac;
                        final double ad = entity == targetEntity ? 1f : getExposure(vec3d, entity);
                        final double ae = (1.0D - y) * ad;
                        final double baseDamage = entity instanceof LivingEntity livingEntity
                                && livingEntity.hasStatusEffect(StatusEffects.SUPERCHROMATIC)
                                ? Math.max(this.power - (livingEntity.getStatusEffect(StatusEffects.SUPERCHROMATIC).getAmplifier() + 1.0D), 0f) * 2.0D
                                : q;
                        entity.damage(this.getDamageSource(), (((float) ((int) ((ae * ae + ae) / 2.0D * 7.0D * baseDamage))) / 4f) + 1.0f);
                        double af = ae;
                        if (entity instanceof LivingEntity)
                            af = ProtectionEnchantment.transformExplosionKnockback((LivingEntity) entity, ae);

                        entity.setVelocity(entity.getVelocity().add(z * af, aa * af, ab * af));
                        if (entity instanceof PlayerEntity)
                        {
                            final PlayerEntity playerEntity = (PlayerEntity) entity;
                            if (!playerEntity.isSpectator() && (!playerEntity.isCreative() || !playerEntity.getAbilities().flying))
                                ((ExplosionAccessor) this).getAffectedPlayers().put(playerEntity, new Vec3d(z * ae, aa * ae, ab * ae));
                        }
                    }
                }
            }
        }
    }

    private void modifyEntitiesList(List<Entity> list)
    {
        Entity entity = this.entity;
        if (entity instanceof ProjectileEntity projectileEntity && projectileEntity.getOwner() != null)
        {
            entity = projectileEntity.getOwner();
            list.remove(entity);
        }

        final Entity sourceEntity = entity;
        final Entity targetEntity = getTargetEntity();

        if (sourceEntity != null && targetEntity != null)
            list.removeIf(e -> canDamageEntity(sourceEntity, targetEntity, e));
    }

    private boolean canDamageEntity(Entity sourceEntity, Entity targetEntity, Entity damagedEntity)
    {
        if (targetEntity == damagedEntity)
            return true;

        final ChromaBlastTargetType sourceEntityTargetType = ChromaBlastTargetType.getEntityTargetType(sourceEntity);
        final ChromaBlastTargetType targetEntityTargetType = ChromaBlastTargetType.getEntityTargetType(targetEntity);
        if (targetEntityTargetType == sourceEntityTargetType)
            return true;

        final ChromaBlastTargetType damagedEntityTargetType = ChromaBlastTargetType.getEntityTargetType(damagedEntity);
        if (damagedEntityTargetType == ChromaBlastTargetType.OTHER_LIVING
                || (sourceEntityTargetType != ChromaBlastTargetType.PLAYER && damagedEntityTargetType == ChromaBlastTargetType.OTHER))
            return true;

        switch (sourceEntityTargetType)
        {
            case PLAYER:
                if (targetEntityTargetType == ChromaBlastTargetType.PLAYER)
                    return sourceEntity.isTeammate(targetEntity) || !sourceEntity.isTeammate(damagedEntity);
                return damagedEntityTargetType != ChromaBlastTargetType.PLAYER;
            case SUPERCHROMATIC_MOB:
                return targetEntityTargetType == damagedEntityTargetType;
            default:
                return true;
        }
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
            Spectrite.INSTANCE.spawnChromaBlastParticle(this.x, this.y, this.z, 0, 0, 0);
            if (power > 1)
                Spectrite.INSTANCE.spawnChromaBlastEmitterParticle(this.world, this.x, this.y, this.z, power);
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
