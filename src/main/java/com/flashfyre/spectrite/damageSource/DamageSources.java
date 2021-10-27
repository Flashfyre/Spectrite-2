package com.flashfyre.spectrite.damageSource;

import net.minecraft.entity.damage.DamageSource;

public class DamageSources
{
    public static DamageSource CHROMA_BLAST;

    public static void initDamageSources()
    {
        CHROMA_BLAST = new ChromaBlastDamageSource("chroma_blast");
    }
}
