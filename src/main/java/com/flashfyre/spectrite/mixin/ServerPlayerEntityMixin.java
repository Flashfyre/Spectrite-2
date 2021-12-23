package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.entity.player.ServerSuperchromaticItemCooldownManager;
import com.flashfyre.spectrite.entity.player.SuperchromaticCooldownPlayerEntity;
import com.flashfyre.spectrite.entity.player.SuperchromaticItemCooldownManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements SuperchromaticCooldownPlayerEntity
{
    private ServerSuperchromaticItemCooldownManager serverSuperchromaticItemCooldownManager;

    @Override
    public SuperchromaticItemCooldownManager createSuperchromaticCooldownManager()
    {
        return (serverSuperchromaticItemCooldownManager = new ServerSuperchromaticItemCooldownManager((ServerPlayerEntity) (Object) this));
    }

    @Override
    public SuperchromaticItemCooldownManager getSuperchromaticItemCooldownManager()
    {
        return serverSuperchromaticItemCooldownManager;
    }
}
