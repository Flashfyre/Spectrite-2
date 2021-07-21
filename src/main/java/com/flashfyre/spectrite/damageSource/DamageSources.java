package com.flashfyre.spectrite.damageSource;

import net.minecraft.entity.damage.DamageSource;

public class DamageSources
{
    public static DamageSource SPECTRITE_DAMAGE;

    public static void initDamageSources()
    {
        SPECTRITE_DAMAGE = new SpectriteDamageSource("spectrite");
    }
}
