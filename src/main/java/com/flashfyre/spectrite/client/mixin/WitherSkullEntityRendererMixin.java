package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.client.SpectriteClient;
import com.flashfyre.spectrite.client.util.SpectriteEntityRenderUtils;
import com.flashfyre.spectrite.entity.SpectriteCompatibleWeaponEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.WitherSkullEntityRenderer;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.AbstractMap;

@Environment(EnvType.CLIENT)
@Mixin(WitherSkullEntityRenderer.class)
public class WitherSkullEntityRendererMixin
{
    @Shadow
    @Final
    private SkullEntityModel model;

    @Inject(method = "getTexture", at = @At("RETURN"), cancellable = true)
    private void spectrite$injectGetTexture(WitherSkullEntity witherSkullEntity, CallbackInfoReturnable<Identifier> cir)
    {
        final Identifier spectriteTexture = SpectriteEntityRenderUtils.getOrGenerateSpectriteChargeableEntityTexture(
                this.model, cir.getReturnValue(), witherSkullEntity.getType());

        if (((SpectriteCompatibleWeaponEntity) witherSkullEntity).isSuperchromatic())
        {
            SpectriteClient.CHARGEABLE_SPECTRITE_ENTITY_TEXTURE_SIZE = SpectriteEntityRenderUtils.ENTITY_SPECTRITE_TEXTURE_SIZE_CACHE.getOrDefault(
                    spectriteTexture, new AbstractMap.SimpleEntry<>(1, 1));
            cir.setReturnValue(spectriteTexture);
        }
    }

    @ModifyArgs(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"))
    private void spectrite$modifyArgRenderVertexConsumerProviderGetBuffer(
            Args args, WitherSkullEntity witherSkullEntity, float f, float g, MatrixStack matrixStack,
            VertexConsumerProvider vertexConsumerProvider, int i)
    {
        final SpectriteCompatibleWeaponEntity spectriteCompatibleWeaponEntity = (SpectriteCompatibleWeaponEntity) witherSkullEntity;
        if (spectriteCompatibleWeaponEntity.isSuperchromatic())
            args.set(0, SpectriteClient.CLIENT_INSTANCE.getHueLayer(args.get(0), 0, spectriteCompatibleWeaponEntity.isSpectriteCharged()));
    }
}
