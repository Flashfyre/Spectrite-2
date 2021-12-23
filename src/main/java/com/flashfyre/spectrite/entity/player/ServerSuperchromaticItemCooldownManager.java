package com.flashfyre.spectrite.entity.player;

import com.flashfyre.spectrite.Spectrite;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerSuperchromaticItemCooldownManager extends SuperchromaticItemCooldownManager
{
    private final ServerPlayerEntity player;

    public ServerSuperchromaticItemCooldownManager(ServerPlayerEntity player)
    {
        this.player = player;
    }

    @Override
    protected void onCooldownUpdate(Item item, int duration)
    {
        super.onCooldownUpdate(item, duration);
        final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        new CooldownUpdateS2CPacket(item, duration).write(buf);
        ServerPlayNetworking.send(player, Spectrite.getId("superchromatic_cooldown_update"), buf);
    }

    @Override
    protected void onCooldownUpdate(Item item)
    {
        super.onCooldownUpdate(item);
        final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        new CooldownUpdateS2CPacket(item, 0).write(buf);
        ServerPlayNetworking.send(player, Spectrite.getId("superchromatic_cooldown_update"), buf);
    }
}
