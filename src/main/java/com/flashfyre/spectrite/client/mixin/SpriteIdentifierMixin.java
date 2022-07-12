package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.client.SpectriteClient;
import com.flashfyre.spectrite.client.util.SpectriteEntityRenderUtils;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.SpriteIdentifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(SpriteIdentifier.class)
public class SpriteIdentifierMixin
{
    @ModifyReturnValue(method = "getRenderLayer", at = @At("RETURN"))
    public RenderLayer getRenderLayerForSuperchromaticBlockEntity(RenderLayer renderLayer)
    {
        if (SpectriteEntityRenderUtils.CURRENT_RENDERING_BLOCK_ENTITY_SUPERCHROMATIC)
            return SpectriteClient.CLIENT_INSTANCE.getSuperchromaticEntityLayer(renderLayer);
        return renderLayer;
    }
}
