package com.flashfyre.spectrite.client.particle;

import com.flashfyre.spectrite.client.util.SpectriteClientUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.world.ClientWorld;

@Environment(EnvType.CLIENT)
public class ChromaBlastEmitterParticle extends NoRenderParticle
{
    private int age_;
    private final int maxAge;
    private final double power;

    public ChromaBlastEmitterParticle(ClientWorld clientWorld, double d, double e, double f, double power)
    {
        super(clientWorld, d, e, f, 0.0D, 0.0D, 0.0D);
        this.maxAge = 8 + ((int) power << 1);
        this.power = power;
    }

    public void tick()
    {
        for (int i = 0; i < ((int) power << 1) - 3; ++i)
        {
            final double d = this.x + (this.random.nextDouble() - this.random.nextDouble()) * power * 2.0D;
            final double e = this.y + (this.random.nextDouble() - this.random.nextDouble()) * power * 2.0D;
            final double f = this.z + (this.random.nextDouble() - this.random.nextDouble()) * power * 2.0D;

            SpectriteClientUtils.spawnSpectriteExplosionParticle(d, e, f,
                    (float) this.age_ / (float) this.maxAge, 0.0D, 0.0D);
        }

        ++this.age_;
        if (this.age_ == this.maxAge)
            this.markDead();

    }
}
