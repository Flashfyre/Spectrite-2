package com.flashfyre.spectrite.util;

import com.flashfyre.spectrite.Spectrite;
import com.flashfyre.spectrite.SpectriteConfig;
import com.flashfyre.spectrite.damageSource.DamageSources;
import com.flashfyre.spectrite.etc.SpectriteExplosion;
import com.flashfyre.spectrite.item.Items;
import com.flashfyre.spectrite.item.SpectriteDamageableItem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.MapColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

public final class SpectriteUtils
{
    public static final Formatting[] TEXT_COLORS = new Formatting[]{Formatting.RED, Formatting.GOLD, Formatting.YELLOW,
            Formatting.GREEN, Formatting.BLUE, Formatting.DARK_PURPLE, Formatting.LIGHT_PURPLE};
    private static final MapColor[] MAP_COLORS = new MapColor[]{MapColor.RED, MapColor.TERRACOTTA_ORANGE,
            MapColor.YELLOW, MapColor.LIME, MapColor.BLUE, MapColor.PURPLE, MapColor.PINK};

    public static int getCurrentSpectriteFrame(World worldIn)
    {
        if (worldIn == null)
            return Math.round((System.currentTimeMillis() >> 6) % 36);
        else
        {
            float time = MathHelper.ceil((((worldIn.getTime() >> 1) % 36)
                    * 0.2777F) * 1000F) / 10000F;
            return Math.round(time * 36);
        }
    }

    public static float[] getCurrentSpectriteRGBColor(float offsetLevel)
    {
        int hueFrame = Math.round((System.currentTimeMillis() >> 5) % 180);
        if (offsetLevel >= 0f)
            hueFrame = (hueFrame + ((int) (offsetLevel))) % 180;
        float r = hueFrame >= 120 && hueFrame < 150 ? (1f / 30) * (hueFrame - 120) : hueFrame < 30 || hueFrame >= 150 ? 1f : hueFrame < 60 ? (1f / 30) * (30 - (hueFrame - 30)) : 0f,
                g = hueFrame < 30 ? (1f / 30) * hueFrame : hueFrame < 90 ? 1f : hueFrame < 120 ? (1f / 30) * (30 - (hueFrame - 90)) : 0f,
                b = hueFrame >= 60 && hueFrame < 90 ? (1f / 30) * (hueFrame - 60) : hueFrame >= 90 && hueFrame < 150 ? 1f : hueFrame >= 150 ? (1f / 30) * (30 - (hueFrame - 150)) : 0f;

        if (offsetLevel < 0f)
        {
            r = 1.0f - r;
            g = 1.0f - g;
            b = 1.0f - b;
        }

        return new float[]{r, g, b};
    }

    public static int getCurrentSpectriteColor(int offsetLevel)
    {
        final int hueFrame = Math.round((System.currentTimeMillis() >> 5) % 180);
        int r = MathHelper.floor(hueFrame >= 120 && hueFrame < 150 ? (255f / 30) * (hueFrame - 120)
                : hueFrame < 30 || hueFrame >= 150 ? 255f : hueFrame < 60 ? (255f / 30) * (30 - (hueFrame - 30)) : 0f),
                g = MathHelper.floor(hueFrame < 30 ? (255f / 30) * hueFrame : hueFrame < 90 ? 255f
                        : hueFrame < 120 ? (255f / 30) * (30 - (hueFrame - 90)) : 0f),
                b = MathHelper.floor(hueFrame >= 60 && hueFrame < 90 ? (255f / 30) * (hueFrame - 60)
                        : hueFrame >= 90 && hueFrame < 150 ? 255f : hueFrame >= 150 ? (255f / 30) * (30 - (hueFrame - 150)) : 0f);

        final int tempR = r;

        switch (offsetLevel)
        {
            case 1:
                r = g;
                g = b;
                b = tempR;
                break;
            case 2:
                int tempG = g;
                r = b;
                g = tempR;
                b = tempG;
                break;
            default:
                break;
        }

        return (r << 16) + (g << 8) + b + (255 >> 24);
    }

    public static int getItemStackStDamage(ItemStack itemStack)
    {
        return ((SpectriteDamageableItem) itemStack.getItem()).isDepleted()
                ? 4 : Math.min((int) Math.floor((itemStack.getDamage() / (float) itemStack.getMaxDamage()) * 4f), 4);
    }

    public static MapColor getSpectriteMapColor(World worldIn, BlockPos pos)
    {
        final int posOffset = pos.getX() + pos.getY() + pos.getZ();

        return MAP_COLORS[(posOffset + (int) (worldIn.getTime() >> 2)) % 7];
    }

    public static void tryActivateSpectriteChargeableItemCooldown(PlayerEntity playerEntity, ItemStack spectriteChargeableItemStack)
    {
        if (playerEntity != null && !playerEntity.isCreative())
        {
            final ItemCooldownManager itemCooldownManager = playerEntity.getItemCooldownManager();
            final float cooldown = SpectriteConfig.getSpectriteToolCooldown() * 20f;
            for (Item spectriteChargeableItem : Items.SPECTRITE_CHARGEABLE_ITEMS)
            {
                final int currentCooldown = Math.round(itemCooldownManager.getCooldownProgress(spectriteChargeableItem, 0f));
                itemCooldownManager.set(spectriteChargeableItem, (int) Math.max(cooldown, currentCooldown));
            }
        }
    }

    public static SpectriteExplosion newSpectriteExplosion(World world, @Nullable Entity entity, @Nullable Entity targetEntity,
                                                           @Nullable ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire,
                                                           Explosion.DestructionType destructionType)
    {
        final SpectriteExplosion explosion = new SpectriteExplosion(world, entity, targetEntity, DamageSources.SPECTRITE_DAMAGE,
                behavior, x, y, z, power, createFire, destructionType);
        explosion.collectBlocksAndDamageEntities();
        explosion.affectWorld(true);

        if (!world.isClient)
        {
            if (destructionType == Explosion.DestructionType.NONE)
                explosion.clearAffectedBlocks();

            for (final PlayerEntity player : world.getPlayers())
            {
                if (player.squaredDistanceTo(x, y, z) < 4096.0)
                {
                    final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                    new ExplosionS2CPacket(x, y, z, power, explosion.getAffectedBlocks(), explosion.getAffectedPlayers().get(player)).write(buf);
                    ServerPlayNetworking.send((ServerPlayerEntity) player, Spectrite.getId("explosion"), buf);
                }
            }
        }

        return explosion;
    }
}
