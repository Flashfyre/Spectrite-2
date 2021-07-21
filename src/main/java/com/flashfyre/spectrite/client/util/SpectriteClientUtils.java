package com.flashfyre.spectrite.client.util;

import com.flashfyre.spectrite.client.particle.Particles;
import com.flashfyre.spectrite.client.particle.SpectriteExplosionEmitterParticle;
import com.flashfyre.spectrite.etc.SpectriteExplosion;
import com.flashfyre.spectrite.util.SpectriteUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.world.explosion.Explosion;

@Environment(EnvType.CLIENT)
public class SpectriteClientUtils
{
    public static void explodeOnClient(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender)
    {
        ExplosionS2CPacket packet = new ExplosionS2CPacket(buf); // Read from byte buf
        client.execute(() ->
        {
            final Explosion explosion = new SpectriteExplosion(client.world, null, null,
                    packet.getX(), packet.getY(), packet.getZ(), packet.getRadius(), packet.getAffectedBlocks());
            explosion.affectWorld(true);
            client.player.setVelocity(client.player.getVelocity().add(packet.getPlayerVelocityX(), packet.getPlayerVelocityY(), packet.getPlayerVelocityZ()));
        });
    }

    public static void spawnSpectriteExplosionEmitterParticle(ClientWorld world, double posX, double posY, double posZ, double power)
    {
        MinecraftClient.getInstance().particleManager.addParticle(new SpectriteExplosionEmitterParticle(world, posX, posY, posZ, power));
    }

    public static void spawnSpectriteExplosionParticle(double posX, double posY, double posZ, double xSpeed, double ySpeed, double zSpeed)
    {
        final Particle particle = MinecraftClient.getInstance().particleManager.addParticle(Particles.SPECTRITE_EXPLOSION, posX, posY, posZ, xSpeed, ySpeed, zSpeed);
        final float offsetLevel = 18F * (Double.valueOf(Math.abs(posX + posZ) + posY).floatValue() % 20F);
        final float[] c = SpectriteUtils.getCurrentSpectriteRGBColor(offsetLevel);
        particle.setColor(c[0], c[1], c[2]);
    }
}
