package com.flashfyre.spectrite.mixin;

import com.flashfyre.spectrite.entity.player.SuperchromaticCooldownPlayerEntity;
import com.flashfyre.spectrite.entity.player.SuperchromaticItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements SuperchromaticCooldownPlayerEntity
{
    private final SuperchromaticItemCooldownManager superchromaticItemCooldownManager = createSuperchromaticCooldownManager();

    @Override
    public SuperchromaticItemCooldownManager createSuperchromaticCooldownManager()
    {
        return new SuperchromaticItemCooldownManager();
    }

    @Override
    public SuperchromaticItemCooldownManager getSuperchromaticItemCooldownManager()
    {
        return superchromaticItemCooldownManager;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ItemCooldownManager;update()V"))
    private void injectTickUpdateSuperchromaticCooldownManager(CallbackInfo ci)
    {
        getSuperchromaticItemCooldownManager().update();
    }
}
