package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.client.particle.Particles;
import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ParticleManager.class)
public class ParticleManagerMixin
{
    @Shadow
    @Final
    @Mutable
    private static List<ParticleTextureSheet> PARTICLE_TEXTURE_SHEETS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void spectrite$inject(CallbackInfo callbackInfo)
    {
        PARTICLE_TEXTURE_SHEETS = ImmutableList.<ParticleTextureSheet>builder().addAll(PARTICLE_TEXTURE_SHEETS)
                .add(Particles.PARTICLE_SHEET_SPECTRITE).build();
    }
}
