package com.flashfyre.spectrite.entity;

import net.minecraft.entity.boss.ServerBossBar;

public interface SpectriteCompatibleMobEntity extends SpectriteCompatibleEntity
{
    ServerBossBar getSuperchromaticBossBar();

    void setSuperchromaticBossBar(ServerBossBar superchromaticBossBar);
}
