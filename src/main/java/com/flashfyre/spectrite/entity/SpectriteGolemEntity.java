package com.flashfyre.spectrite.entity;

import com.flashfyre.spectrite.SpectriteConfig;
import com.flashfyre.spectrite.util.SuperchromaticEntityUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class SpectriteGolemEntity extends IronGolemEntity implements SuperchromaticEntity
{
    private static final TrackedData<Boolean> DEPLETED = DataTracker.registerData(SpectriteGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public SpectriteGolemEntity(EntityType<? extends IronGolemEntity> entityType, World world)
    {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker()
    {
        super.initDataTracker();
        this.dataTracker.startTracking(DEPLETED, false);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Depleted", this.isDepleted());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);
        this.setDepleted(nbt.getBoolean("Depleted"));
    }

    @Override
    protected void mobTick()
    {
        super.mobTick();
        if (isDepleted() && this.age % 20 == 0)
        {
            this.heal(1.0f);
            if (getHealth() == getMaxHealth())
            {
                setDepleted(false);
                SuperchromaticEntityUtils.enableSuperchromaticMobAttributes(this);
            }
        }
    }

    @Override
    public boolean isDead()
    {
        final boolean ret = super.isDead();
        if (ret && !SpectriteConfig.getSpectriteToolsBreakPermanently() && !isDepleted())
        {
            setHealth(10.0f);
            setDepleted(true);
            SuperchromaticEntityUtils.disableSuperchromaticMobAttributes(this);
            return false;
        }

        return ret;
    }

    public boolean isDepleted()
    {
        return this.dataTracker.get(DEPLETED);
    }

    public void setDepleted(boolean depleted)
    {
        this.dataTracker.set(DEPLETED, depleted);
    }
}
