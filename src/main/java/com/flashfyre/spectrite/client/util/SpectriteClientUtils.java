package com.flashfyre.spectrite.client.util;

import com.flashfyre.spectrite.client.particle.ChromaBlastEmitterParticle;
import com.flashfyre.spectrite.entity.player.SuperchromaticCooldownPlayerEntity;
import com.flashfyre.spectrite.etc.ChromaBlast;
import com.flashfyre.spectrite.particle.Particles;
import com.flashfyre.spectrite.util.SpectriteUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.world.explosion.Explosion;

@Environment(EnvType.CLIENT)
public class SpectriteClientUtils
{
    public static void handleClientChromaBlastExplosion(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender)
    {
        final ExplosionS2CPacket packet = new ExplosionS2CPacket(buf);
        client.execute(() ->
        {
            final Explosion explosion = new ChromaBlast(client.world, null, null,
                    packet.getX(), packet.getY(), packet.getZ(), packet.getRadius(), packet.getAffectedBlocks());
            explosion.affectWorld(true);
            client.player.setVelocity(client.player.getVelocity().add(packet.getPlayerVelocityX(), packet.getPlayerVelocityY(), packet.getPlayerVelocityZ()));
        });
    }

    public static void spawnSpectriteExplosionEmitterParticle(ClientWorld world, double posX, double posY, double posZ, double power)
    {
        MinecraftClient.getInstance().particleManager.addParticle(new ChromaBlastEmitterParticle(world, posX, posY, posZ, power));
    }

    public static void spawnSpectriteExplosionParticle(double posX, double posY, double posZ, double xSpeed, double ySpeed, double zSpeed)
    {
        final Particle particle = MinecraftClient.getInstance().particleManager.addParticle(Particles.CHROMA_BLAST, posX, posY, posZ, xSpeed, ySpeed, zSpeed);
        final float offsetLevel = 18F * (Double.valueOf(Math.abs(posX + posZ) + posY).floatValue() % 20F);
        final float[] c = SpectriteUtils.getCurrentHueRGBColor(offsetLevel);
        particle.setColor(c[0], c[1], c[2]);
    }

    public static void handleClientSuperchromaticCooldown(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender)
    {
        final CooldownUpdateS2CPacket packet = new CooldownUpdateS2CPacket(buf);
        client.execute(() ->
        {
            if (packet.getCooldown() == 0)
                ((SuperchromaticCooldownPlayerEntity) client.player).getSuperchromaticItemCooldownManager().remove();
            else
                ((SuperchromaticCooldownPlayerEntity) client.player).getSuperchromaticItemCooldownManager().set(packet.getCooldown());
        });
    }
}
