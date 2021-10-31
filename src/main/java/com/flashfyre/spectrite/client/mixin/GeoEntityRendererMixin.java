package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.client.SpectriteClient;
import com.flashfyre.spectrite.client.util.SpectriteGeoEntityRenderUtils;
import com.flashfyre.spectrite.entity.SpectriteCompatibleMobEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

@Environment(EnvType.CLIENT)
@Mixin(GeoEntityRenderer.class)
public class GeoEntityRendererMixin
{
    @Shadow
    @Final
    private AnimatedGeoModel modelProvider;

    @ModifyArgs(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lsoftware/bernie/geckolib3/renderers/geo/GeoEntityRenderer;render(Lsoftware/bernie/geckolib3/geo/render/built/GeoModel;Ljava/lang/Object;FLnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void injectGetRenderLayerSubstituteHueRenderLayer(Args args)
    {
        final LivingEntity e = args.get(1);
        if (e instanceof MobEntity && ((SpectriteCompatibleMobEntity) e).isSuperchromatic())
            args.set(3, SpectriteClient.CLIENT_INSTANCE.getHueLayer(args.get(3)));
    }


    @Inject(method = "getTextureLocation(Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/util/Identifier;", at = @At("RETURN"), cancellable = true)
    private void injectGetTextureLocationSubstituteSpectriteTexture(LivingEntity entity, CallbackInfoReturnable<Identifier> cir)
    {
        final GeoModel currentModel = ((AnimatedGeoModelAccessor) this.modelProvider).getCurrentModel();
        if (currentModel != null)
        {
            final Identifier spectriteTexture = SpectriteGeoEntityRenderUtils.getOrGenerateSpectriteEntityTexture(currentModel, cir.getReturnValue(), entity.getType());
            if (entity instanceof MobEntity && ((SpectriteCompatibleMobEntity) entity).isSuperchromatic())
                cir.setReturnValue(spectriteTexture);
        }
    }
}
