package com.flashfyre.spectrite.etc;

import com.flashfyre.spectrite.entity.SpectriteCompatibleMobEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public enum ChromaBlastTargetType
{
    PLAYER(0),
    OTHER_TEAM_PLAYER(1),
    SPECTRITE_MOB(2),
    OTHER(3);

    private int typeIndex;

    ChromaBlastTargetType(int typeIndex)
    {
        this.typeIndex = typeIndex;
    }

    public static ChromaBlastTargetType getEntityTargetType(Entity entity)
    {
        if (entity instanceof PlayerEntity)
            return PLAYER;
        if (entity instanceof SpectriteCompatibleMobEntity spectriteCompatibleMobEntity
                && spectriteCompatibleMobEntity.isSuperchromatic())
            return SPECTRITE_MOB;
        return OTHER;
    }

    public static ChromaBlastTargetType getCollateralEntityTargetType(Entity entity, Entity collateralEntity)
    {
        if (collateralEntity instanceof PlayerEntity)
            return collateralEntity.isTeammate(entity)
                    ? PLAYER
                    : OTHER_TEAM_PLAYER;
        return getEntityTargetType(collateralEntity);
    }
}
