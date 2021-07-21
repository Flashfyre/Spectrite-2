package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.client.SpectriteClient;
import com.flashfyre.spectrite.client.util.SpectriteEntityRenderUtils;
import com.flashfyre.spectrite.entity.SpectriteCompatibleEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Environment(EnvType.CLIENT)
@Mixin(FeatureRenderer.class)
public class FeatureRendererMixin
{
    @ModifyVariable(method = "renderModel", at = @At("HEAD"))
    private static <T extends LivingEntity> Identifier modifyVariableRenderModelSubstituteSpectriteTexture(
            Identifier ret, EntityModel<T> model, Identifier texture, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, T entity)
    {
        final Identifier spectriteTexture = SpectriteEntityRenderUtils.getOrGenerateSpectriteEntityTexture(model, texture, entity.getType());
        if (entity instanceof SpectriteCompatibleEntity spectriteCompatibleEntity && spectriteCompatibleEntity.isSpectriteEntity())
            return spectriteTexture;

        return ret;
    }

    @ModifyArgs(method = "renderModel",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"))
    private static <T extends LivingEntity> void modifyArgRenderGetBufferSubstituteHueLayer(
            Args args, EntityModel<T> model, Identifier texture, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, T entity, float red, float green, float blue)
    {
        if (entity instanceof SpectriteCompatibleEntity spectriteCompatibleEntity && spectriteCompatibleEntity.isSpectriteEntity())
            args.set(0, SpectriteClient.CLIENT_INSTANCE.getHueLayer(args.get(0)));
    }
}
