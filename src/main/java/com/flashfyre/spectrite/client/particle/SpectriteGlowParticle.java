package com.flashfyre.spectrite.client.particle;

import com.flashfyre.spectrite.util.SpectriteUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class SpectriteGlowParticle extends GlowParticle
{
    static final Random RANDOM = new Random();

    public SpectriteGlowParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i,
                                 SpriteProvider spriteProvider)
    {
        super(clientWorld, d, e, f, g, h, i, spriteProvider);
    }

    @Override
    public ParticleTextureSheet getType()
    {
        return Particles.PARTICLE_SHEET_SPECTRITE;
    }

    public static class Factory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider)
        {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld,
                                       double d, double e, double f, double g, double h, double i)
        {
            final SpectriteGlowParticle glowParticle = new SpectriteGlowParticle(clientWorld, d, e, f,
                    0.5D - SpectriteGlowParticle.RANDOM.nextDouble(), h,
                    0.5D - SpectriteGlowParticle.RANDOM.nextDouble(), this.spriteProvider);

            final float offsetLevel = 18F * (float) (Double.valueOf(Math.abs(d + f) + e) % 20D);
            final float[] c = SpectriteUtils.getCurrentSpectriteRGBColor(offsetLevel);
            glowParticle.setColor(c[0], c[1], c[2]);

            glowParticle.velocityY *= 0.20000000298023224D;
            if (g == 0.0D && i == 0.0D)
            {
                glowParticle.velocityX *= 0.10000000149011612D;
                glowParticle.velocityZ *= 0.10000000149011612D;
            }

            glowParticle.setMaxAge((int) (8.0D / (clientWorld.random.nextDouble() * 0.8D + 0.2D)));
            return glowParticle;
        }
    }
}
