package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.client.SpectriteClient;
import com.flashfyre.spectrite.client.util.SpectriteEntityRenderUtils;
import com.flashfyre.spectrite.entity.SpectriteCompatibleMobEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin
{
    @Shadow
    protected EntityModel<? extends LivingEntity> model;

    @ModifyVariable(method = "getRenderLayer", at = @At("STORE"))
    private Identifier injectGetRenderLayerSubstituteSpectriteTexture(Identifier identifier, LivingEntity e, boolean showBody, boolean translucent, boolean showOutline)
    {
        final Identifier spectriteTexture = SpectriteEntityRenderUtils.getOrGenerateSpectriteEntityTexture(model, identifier, e.getType());
        if (e instanceof MobEntity && ((SpectriteCompatibleMobEntity) e).isSpectriteEntity())
            return spectriteTexture;

        return identifier;
    }

    @Inject(method = "getRenderLayer", at = @At("RETURN"), cancellable = true)
    private void injectGetRenderLayerSubstituteHueRenderLayer(LivingEntity e, boolean showBody, boolean translucent, boolean showOutline, CallbackInfoReturnable<RenderLayer> cir)
    {
        if (e instanceof MobEntity && ((SpectriteCompatibleMobEntity) e).isSpectriteEntity())
            cir.setReturnValue(SpectriteClient.CLIENT_INSTANCE.getHueLayer(cir.getReturnValue()));
    }
}
