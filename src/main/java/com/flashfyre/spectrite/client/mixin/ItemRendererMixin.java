package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.client.SpectriteClient;
import com.flashfyre.spectrite.entity.player.SuperchromaticCooldownPlayerEntity;
import com.flashfyre.spectrite.item.*;
import com.flashfyre.spectrite.util.SpectriteUtils;
import com.flashfyre.spectrite.util.SuperchromaticItemUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin
{
    private static final ThreadLocal<SpectriteTridentItem> RENDERING_SPECTRITE_TRIDENT = new ThreadLocal<>();

    static
    {
        RENDERING_SPECTRITE_TRIDENT.set(null);
    }

    @Shadow
    @Final
    private ItemModels models;

    @Shadow
    protected abstract void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha);

    @ModifyVariable(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(value = "STORE", target = "Lnet/minecraft/client/render/RenderLayers;getItemLayer(Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/client/render/RenderLayer;"),
            ordinal = 0)
    private RenderLayer modifyVariableRenderItemRenderLayersGetItemLayerChargedSpectriteItem(
            RenderLayer layer, ItemStack stack)
    {
        final Item item = stack.getItem();
        if (item instanceof SpectriteItem)
        {
            final int damage = item instanceof SpectriteDamageableItem
                    ? SpectriteUtils.getItemStackStDamage(stack)
                    : 0;
            final boolean charged = (item instanceof SpectriteChargeableItem spectriteChargeableItem
                    && spectriteChargeableItem.isCharged(stack)) || item instanceof PassiveChargedSpectriteItem;
            return SpectriteClient.CLIENT_INSTANCE.getSpectriteItemLayer(layer, damage, charged);
        } else if (SuperchromaticItemUtils.isSuperchromatic(stack))
        {
            final boolean charged = SuperchromaticItemUtils.isSuperchromaticCharged(stack);
            return SpectriteClient.CLIENT_INSTANCE.getSuperchromizedItemLayer(layer, charged);
        }
        return layer;
    }

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", ordinal = 0))
    private void injectRenderItemCaptureRenderingSpectriteTrident(
            ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci)
    {
        if (stack.getItem() instanceof SpectriteTridentItem spectriteTridentItem)
            RENDERING_SPECTRITE_TRIDENT.set(spectriteTridentItem);
    }

    @ModifyVariable(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/item/Items;TRIDENT:Lnet/minecraft/item/Item;", ordinal = 0))
    private BakedModel modifyVariableRenderItemStackIsOfTrident(BakedModel model, ItemStack stack)
    {
        final SpectriteTridentItem spectriteTridentItem = RENDERING_SPECTRITE_TRIDENT.get();
        if (spectriteTridentItem != null)
        {
            final BakedModel spectriteTridentModel = this.models.getModelManager().getModel(new ModelIdentifier(Registry.ITEM.getId(spectriteTridentItem), "inventory"));
            return spectriteTridentModel.getOverrides().apply(spectriteTridentModel, stack, MinecraftClient.getInstance().world, null, 0);
        }

        return model;
    }

    @ModifyArg(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 2))
    private Item modifyArgRenderItemStackIsOfTrident(Item item)
    {
        final SpectriteTridentItem spectriteTridentItem = RENDERING_SPECTRITE_TRIDENT.get();
        if (spectriteTridentItem != null)
            return spectriteTridentItem;

        return item;
    }

    @Inject(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V", ordinal = 1))
    private void injectRenderItemMatrixStackPopStopCapturingRenderingSpectriteTrident(
            ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci)
    {
        if (RENDERING_SPECTRITE_TRIDENT.get() != null)
            RENDERING_SPECTRITE_TRIDENT.set(null);
    }

    @ModifyVariable(method = "getModel", at = @At(value = "STORE"), ordinal = 0)
    private BakedModel modifyVariableGetHeldItemModelSubstituteSpectriteTridentInventoryModel(BakedModel model, ItemStack stack)
    {
        if (stack.getItem() instanceof SpectriteTridentItem spectriteTridentItem)
            return this.models.getModelManager().getModel(new ModelIdentifier(Registry.ITEM.getId(spectriteTridentItem) + "_in_hand#inventory"));

        return model;
    }

    @Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            at = @At("TAIL"))
    private void injectRenderGuiItemOverlayAddSuperchromaticCooldown(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci)
    {
        if (stack.getItem() instanceof SpectriteChargeableItem || (SuperchromaticItemUtils.isSuperchromatic(stack) && SuperchromaticItemUtils.isSuperchromaticChargeable(stack)))
        {
            final PlayerEntity playerEntity = MinecraftClient.getInstance().player;
            float cooldown = playerEntity == null
                    ? 0.0f :
                    ((SuperchromaticCooldownPlayerEntity) playerEntity).getSuperchromaticItemCooldownManager().getCooldownProgress(MinecraftClient.getInstance().getTickDelta());
            if (cooldown > 0.0f)
            {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                final Tessellator tessellator = Tessellator.getInstance();
                final BufferBuilder bufferBuilder = tessellator.getBuffer();
                this.renderGuiQuad(bufferBuilder, x, y + MathHelper.floor(16.0f * (1.0f - cooldown)), 16,
                        MathHelper.ceil(16.0f * cooldown), 255, 255, 255, 127);
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }
        }
    }
}
