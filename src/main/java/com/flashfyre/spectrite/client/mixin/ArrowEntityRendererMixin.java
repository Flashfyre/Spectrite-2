package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.client.SpectriteClient;
import com.flashfyre.spectrite.client.util.SpectriteEntityRenderUtils;
import com.flashfyre.spectrite.entity.SpectriteCompatibleEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.AbstractMap;

@Environment(EnvType.CLIENT)
@Mixin(ArrowEntityRenderer.class)
public class ArrowEntityRendererMixin
{
    @Inject(method = "getTexture", at = @At("RETURN"), cancellable = true)
    private void spectrite$injectGetTexture(ArrowEntity arrowEntity, CallbackInfoReturnable<Identifier> cir)
    {
        final Identifier spectriteTexture = SpectriteEntityRenderUtils.getOrGenerateSpectriteEntityTexture(
                SpectriteEntityRenderUtils.SUPERCHROMATIC_ARROW_TEXTURE_OVERLAY_DATA, cir.getReturnValue(),
                arrowEntity.getType(), true);

        if (((SpectriteCompatibleEntity) arrowEntity).isSuperchromatic())
        {
            SpectriteClient.CHARGEABLE_SPECTRITE_ENTITY_TEXTURE_SIZE = SpectriteEntityRenderUtils.ENTITY_SPECTRITE_TEXTURE_SIZE_CACHE.getOrDefault(
                    spectriteTexture, new AbstractMap.SimpleEntry<>(1, 1));
            cir.setReturnValue(spectriteTexture);
        }
    }
}
