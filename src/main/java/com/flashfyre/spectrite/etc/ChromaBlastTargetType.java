package com.flashfyre.spectrite.etc;

import com.flashfyre.spectrite.entity.SpectriteCompatibleMobEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public enum ChromaBlastTargetType
{
    PLAYER(0),
    SUPERCHROMATIC_MOB(1),
    OTHER_LIVING(2),
    OTHER(3);

    private int typeIndex;

    ChromaBlastTargetType(int typeIndex)
    {
        this.typeIndex = typeIndex;
    }

    public static ChromaBlastTargetType getEntityTargetType(Entity entity)
    {
        if (entity != null)
        {
            if (entity instanceof PlayerEntity)
                return PLAYER;
            if (entity instanceof SpectriteCompatibleMobEntity spectriteCompatibleMobEntity
                    && spectriteCompatibleMobEntity.isSuperchromatic())
                return SUPERCHROMATIC_MOB;
            if (entity instanceof LivingEntity)
                return OTHER_LIVING;
        }
        return OTHER;
    }
}
