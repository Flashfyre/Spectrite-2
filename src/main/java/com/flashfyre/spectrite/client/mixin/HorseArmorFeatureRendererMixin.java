package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.client.SpectriteClient;
import com.flashfyre.spectrite.client.util.SpectriteEntityRenderUtils;
import com.flashfyre.spectrite.item.SpectriteHorseArmorItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HorseArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Environment(EnvType.CLIENT)
@Mixin(HorseArmorFeatureRenderer.class)
public class HorseArmorFeatureRendererMixin
{
    @Shadow
    @Final
    private HorseEntityModel<HorseEntity> model;

    @ModifyArgs(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/RenderLayer;getEntityCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private void spectrite$modifyArgRenderEntityCutoutNoNull(
            Args args, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, HorseEntity horseEntity,
            float f, float g, float h, float j, float k, float l)
    {
        final ItemStack itemStack = horseEntity.getArmorType();
        if (itemStack.getItem() instanceof SpectriteHorseArmorItem)
        {
            final Identifier spectriteTexture = SpectriteEntityRenderUtils.getOrGenerateSuperchromaticEntityArmorTexture(model, args.get(0));
            args.set(0, spectriteTexture);
        }
    }

    @ModifyArgs(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"))
    private void spectrite$modifyArgRenderGetBuffer(
            Args args, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, HorseEntity horseEntity,
            float f, float g, float h, float j, float k, float l)
    {
        final ItemStack itemStack = horseEntity.getArmorType();
        if (itemStack.getItem() instanceof HorseArmorItem)
        {
            HorseArmorItem horseArmorItem = (HorseArmorItem) itemStack.getItem();
            if (horseArmorItem instanceof SpectriteHorseArmorItem)
                args.set(0, SpectriteClient.CLIENT_INSTANCE.getHueLayer(args.get(0)));
        }
    }
}
