package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.client.SpectriteClient;
import com.flashfyre.spectrite.client.util.SpectriteEntityRenderUtils;
import com.flashfyre.spectrite.item.SpectriteArmorItem;
import com.flashfyre.spectrite.util.SpectriteUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Environment(EnvType.CLIENT)
@Mixin(ArmorFeatureRenderer.class)
public class ArmorFeatureRendererMixin
{
    private static final ThreadLocal<Integer> SPECTRITE_ARMOR_DAMAGE = new ThreadLocal<>();

    @Inject(method = "renderArmor",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;renderArmorParts(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/ArmorItem;ZLnet/minecraft/client/render/entity/model/BipedEntityModel;ZFFFLjava/lang/String;)V"))
    private void injectRenderArmorCaptureSpectriteArmorDamage(
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, LivingEntity entity, EquipmentSlot armorSlot,
            int light, BipedEntityModel model, CallbackInfo ci)
    {
        final ItemStack itemStack = entity.getEquippedStack(armorSlot);
        if (itemStack.getItem() instanceof SpectriteArmorItem)
            SPECTRITE_ARMOR_DAMAGE.set(SpectriteUtils.getItemStackStDamage(itemStack));
    }

    @ModifyArgs(method = "renderArmorParts",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private void spectrite$modifyArgRenderGetArmorCutoutNoNull(
            Args args, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorItem item,
            boolean usesSecondLayer, BipedEntityModel model, boolean legs, float red, float green, float blue,
            @Nullable String overlay)
    {
        if (item instanceof SpectriteArmorItem)
        {
            final Identifier spectriteTexture = SpectriteEntityRenderUtils.getOrGenerateSuperchromaticEntityArmorTexture(model, args.get(0));
            args.set(0, spectriteTexture);
        }
    }

    @ModifyArgs(method = "renderArmorParts",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/ItemRenderer;getArmorGlintConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;ZZ)Lnet/minecraft/client/render/VertexConsumer;"))
    private void spectrite$modifyArgRenderGetArmorGlintConsumer(
            Args args, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorItem item,
            boolean usesSecondLayer, BipedEntityModel model, boolean legs, float red, float green, float blue,
            @Nullable String overlay)
    {
        if (item instanceof SpectriteArmorItem)
            args.set(1, SpectriteClient.CLIENT_INSTANCE.getHueLayer(args.get(1), SPECTRITE_ARMOR_DAMAGE.get(), false));
    }
}
