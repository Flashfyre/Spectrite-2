package com.flashfyre.spectrite.etc;

import com.flashfyre.spectrite.entity.SpectriteCompatibleMobEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public enum SpectriteDamageTargetType
{
    PLAYER(0),
    OTHER_TEAM_PLAYER(1),
    SPECTRITE_MOB(2),
    OTHER(3);

    private int typeIndex;

    SpectriteDamageTargetType(int typeIndex)
    {
        this.typeIndex = typeIndex;
    }

    public static SpectriteDamageTargetType getEntityTargetType(Entity entity)
    {
        if (entity instanceof PlayerEntity)
            return PLAYER;
        if (entity instanceof SpectriteCompatibleMobEntity spectriteCompatibleMobEntity
                && spectriteCompatibleMobEntity.isSpectriteEntity())
            return SPECTRITE_MOB;
        return OTHER;
    }

    public static SpectriteDamageTargetType getCollateralEntityTargetType(Entity entity, Entity collateralEntity)
    {
        if (collateralEntity instanceof PlayerEntity)
            return collateralEntity.isTeammate(entity)
                    ? PLAYER
                    : OTHER_TEAM_PLAYER;
        return getEntityTargetType(collateralEntity);
    }
}
