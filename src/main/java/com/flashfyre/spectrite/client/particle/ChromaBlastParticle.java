package com.flashfyre.spectrite.client.particle;

import com.flashfyre.spectrite.client.SpectriteClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class ChromaBlastParticle extends ExplosionLargeParticle
{
    public ChromaBlastParticle(ClientWorld clientWorld, double d, double e, double f, double g, SpriteProvider spriteProvider)
    {
        super(clientWorld, d, e, f, g, spriteProvider);
    }

    @Override
    public ParticleTextureSheet getType()
    {
        return SpectriteClient.PARTICLE_SHEET_SPECTRITE;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider)
        {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i)
        {
            return new ChromaBlastParticle(clientWorld, d, e, f, g, this.spriteProvider);
        }
    }
}
