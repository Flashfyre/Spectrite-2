package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.client.sound.SpectriteEntityTrackingDeathSoundInstance;
import com.flashfyre.spectrite.client.sound.SpectriteEntityTrackingSoundInstance;
import com.flashfyre.spectrite.component.Components;
import com.flashfyre.spectrite.component.SpectriteEntityComponent;
import com.flashfyre.spectrite.entity.SpectriteCompatibleEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientWorld.class)
public class SpectriteClientWorldMixin
{
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "playSoundFromEntity", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/sound/SoundManager;play(Lnet/minecraft/client/sound/SoundInstance;)V"),
            cancellable = true)
    private void spectrite$injectPlaySoundFromEntity(PlayerEntity player, Entity entity, SoundEvent sound,
                                                     SoundCategory category, float volume, float pitch, CallbackInfo ci)
    {
        if (entity instanceof SpectriteCompatibleEntity spectriteCompatibleEntity)
        {
            final SpectriteEntityComponent spectriteEntityComponent = Components.SPECTRITE_ENTITY.maybeGet(spectriteCompatibleEntity).orElse(null);
            if (spectriteEntityComponent != null && spectriteEntityComponent.isSpectrite())
            {
                final EntityTrackingSoundInstance entitySound = !(entity instanceof MobEntity mobEntity) || sound != mobEntity.getDeathSound()
                        ? new SpectriteEntityTrackingSoundInstance(sound, category, volume, pitch, entity)
                        : new SpectriteEntityTrackingDeathSoundInstance(sound, category, volume, pitch, entity);
                this.client.getSoundManager().play(entitySound);
                ci.cancel();
            }
        }
    }
}
