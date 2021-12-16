package com.flashfyre.spectrite.particle;


import com.flashfyre.spectrite.Spectrite;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.registry.Registry;

public class Particles
{
    public static DefaultParticleType SUPERCHROMATIC_GLOW;

    public static DefaultParticleType CHROMA_BLAST;

    public static void initParticles()
    {
        SUPERCHROMATIC_GLOW = registerParticleType("superchromatic_glow", true);
        CHROMA_BLAST = registerParticleType("chroma_blast", true);
    }

    private static <T extends ParticleEffect> DefaultParticleType registerParticleType(String name, boolean alwaysShow)

    {
        final T ret = (T) Registry.register(Registry.PARTICLE_TYPE, Spectrite.getId(name), new DefaultParticleType(alwaysShow));
        return (DefaultParticleType) ret;
    }
}
