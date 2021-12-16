package com.flashfyre.spectrite.client.particle;

import com.flashfyre.spectrite.particle.Particles;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

@Environment(EnvType.CLIENT)
public class ParticleFactories
{
    public static void initParticleFactories()
    {
        registerParticleFactory(Particles.SUPERCHROMATIC_GLOW, SuperchromaticGlowParticle.Factory::new);
        registerParticleFactory(Particles.CHROMA_BLAST, ChromaBlastParticle.Factory::new);
    }

    private static <T extends ParticleEffect> void registerParticleFactory(ParticleType<T> particleType,
                                                                           ParticleFactoryRegistry.PendingParticleFactory<T> factory)
    {
        ParticleFactoryRegistry.getInstance().register(particleType, factory);
    }
}
