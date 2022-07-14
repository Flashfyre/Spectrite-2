package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.util.SpectriteUtils;
import com.flashfyre.spectrite.world.Dimensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.customportalapi.client.CustomPortalParticle;
import net.kyrptonaught.customportalapi.util.PortalLink;
import net.minecraft.block.Block;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(CustomPortalParticle.Factory.class)
public class CustomPortalParticleFactoryMixin
{
    @Inject(method = "createParticle(Lnet/minecraft/particle/BlockStateParticleEffect;Lnet/minecraft/client/world/ClientWorld;DDDDDD)Lnet/minecraft/client/particle/Particle;",
            at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void injectCreateParticleSetSuperchromaticPortalParticleColor(BlockStateParticleEffect blockStateParticleEffect,
                                                                         ClientWorld clientWorld, double d, double e, double f,
                                                                         double g, double h, double i, CallbackInfoReturnable<Particle> cir,
                                                                         CustomPortalParticle portalParticle, Block block, PortalLink link)
    {
        if (link.dimID == Dimensions.SUPERCHROMATIC.getValue())
        {
            final float[] currentColor = SpectriteUtils.getCurrentHueRGBColor(0);
            portalParticle.setColor(currentColor[0], currentColor[1], currentColor[2]);
            cir.setReturnValue(portalParticle);
        }
    }
}
