package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.client.SpectriteClient;
import com.flashfyre.spectrite.client.util.SpectriteEntityRenderUtils;
import com.flashfyre.spectrite.item.SpectriteArmorItem;
import com.flashfyre.spectrite.util.SpectriteUtils;
import com.flashfyre.spectrite.util.SuperchromaticItemUtils;
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
    private static final ThreadLocal<ItemStack> CURRENT_ARMOR_STACK = new ThreadLocal<>();

    private static final ThreadLocal<Integer> SPECTRITE_ARMOR_DAMAGE = new ThreadLocal<>();

    @Inject(method = "renderArmor",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;renderArmorParts(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/ArmorItem;ZLnet/minecraft/client/render/entity/model/BipedEntityModel;ZFFFLjava/lang/String;)V"))
    private void injectRenderArmorCaptureSpectriteVars(
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, LivingEntity entity, EquipmentSlot armorSlot,
            int light, BipedEntityModel model, CallbackInfo ci)
    {
        final ItemStack itemStack = entity.getEquippedStack(armorSlot);
        CURRENT_ARMOR_STACK.set(itemStack);
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
            final Identifier textureId = args.get(0);
            final Identifier spectriteTexture = SpectriteEntityRenderUtils.getOrGenerateSpectriteEntityArmorTexture(model,
                    new Identifier(textureId.getPath().replace("spectrite", "diamond")),
                    textureId.getPath());
            args.set(0, spectriteTexture);
        } else if (SuperchromaticItemUtils.isSuperchromatic(CURRENT_ARMOR_STACK.get()))
        {
            final Identifier spectriteTexture = SpectriteEntityRenderUtils.getOrGenerateSpectriteEntityArmorTexture(model, args.get(0), null);
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
        else if (SuperchromaticItemUtils.isSuperchromatic(CURRENT_ARMOR_STACK.get()))
            args.set(1, SpectriteClient.CLIENT_INSTANCE.getHueLayer(args.get(1), 0, false));
    }
}
