package com.flashfyre.spectrite.entity;

import net.minecraft.entity.boss.ServerBossBar;

public interface SpectriteCompatibleMobEntity extends SpectriteCompatibleEntity
{
    ServerBossBar getSpectriteBossBar();
}
