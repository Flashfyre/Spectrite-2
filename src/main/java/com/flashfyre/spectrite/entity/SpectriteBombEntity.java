package com.flashfyre.spectrite.entity;

import com.flashfyre.spectrite.item.Items;
import com.flashfyre.spectrite.particle.Particles;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class SpectriteBombEntity extends ThrownItemEntity
{
    public SpectriteBombEntity(EntityType<? extends SpectriteBombEntity> entityType, World world)
    {
        super(entityType, world);
    }

    public SpectriteBombEntity(World world, LivingEntity owner)
    {
        super(EntityTypes.SPECTRITE_BOMB, owner, world);
    }

    public SpectriteBombEntity(World world, double x, double y, double z)
    {
        super(EntityTypes.SPECTRITE_BOMB, x, y, z, world);
    }

    @Override
    protected Item getDefaultItem()
    {
        return Items.SPECTRITE_BOMB;
    }

    @Override
    public void tick()
    {
        super.tick();
        if (world.getTime() % 3 == 0)
            world.addParticle(Particles.SUPERCHROMATIC_GLOW, getParticleX(0.6D), getRandomBodyY(), getParticleZ(0.6D), 0.0D, 0.0D, 0.0D);
    }

    @Override
    public void handleStatus(byte status)
    {
        if (status == 3)
        {
            for (int i = 0; i < 8; ++i)
                this.world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, this.getStack()), this.getX(), this.getY(), this.getZ(), ((double) this.random.nextFloat() - 0.5) * 0.08D, ((double) this.random.nextFloat() - 0.5) * 0.08D, ((double) this.random.nextFloat() - 0.5) * 0.08D);
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult)
    {
        super.onEntityHit(entityHitResult);
        entityHitResult.getEntity().damage(DamageSource.thrownProjectile(this, this.getOwner()), 2.0f);
    }

    @Override
    protected void onCollision(HitResult hitResult)
    {
        super.onCollision(hitResult);
        if (!this.world.isClient)
        {
            this.world.sendEntityStatus(this, (byte) 3);
            this.discard();
        }
    }
}
