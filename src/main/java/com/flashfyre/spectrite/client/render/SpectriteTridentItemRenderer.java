package com.flashfyre.spectrite.client.render;

import com.flashfyre.spectrite.Spectrite;
import com.flashfyre.spectrite.client.SpectriteClient;
import com.flashfyre.spectrite.client.util.SpectriteEntityRenderUtils;
import com.flashfyre.spectrite.item.SpectriteTridentItem;
import com.flashfyre.spectrite.util.SpectriteUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;

@Environment(EnvType.CLIENT)
public class SpectriteTridentItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer, SimpleSynchronousResourceReloadListener
{
    private final Identifier id;
    private final Identifier tridentEntityTexture;
    private TridentEntityModel tridentEntityModel;

    public SpectriteTridentItemRenderer()
    {
        this.id = new Identifier(Spectrite.MODID, "spectrite_trident_renderer");
        this.tridentEntityTexture = new Identifier("textures/entity/trident.png");
    }

    @Override
    public Identifier getFabricId()
    {
        return this.id;
    }

    @Override
    public Collection<Identifier> getFabricDependencies()
    {
        return Collections.singletonList(ResourceReloadListenerKeys.MODELS);
    }

    @Override
    public void reload(ResourceManager manager)
    {
        this.tridentEntityModel = new TridentEntityModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EntityModelLayers.TRIDENT));
    }

    @Override
    public void render(ItemStack stack, ModelTransformation.Mode renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();
        matrices.scale(1.0F, -1.0F, -1.0F);
        SpectriteEntityRenderUtils.CURRENT_RENDERING_SPECTRITE_COMPATIBLE_ENTITY_ID = EntityType.getId(EntityType.TRIDENT);
        final Identifier spectriteTridentEntityTexture = SpectriteEntityRenderUtils.getOrGenerateSpectriteChargeableEntityTexture(
                tridentEntityModel, tridentEntityTexture, EntityType.TRIDENT);
        SpectriteClient.CHARGEABLE_SPECTRITE_ENTITY_TEXTURE_SIZE = SpectriteEntityRenderUtils.ENTITY_SPECTRITE_TEXTURE_SIZE_CACHE.getOrDefault(
                spectriteTridentEntityTexture, new AbstractMap.SimpleEntry<>(1, 1));
        renderModel(stack, matrices, vertexConsumers, spectriteTridentEntityTexture, light, overlay);
        matrices.pop();
    }

    private void renderModel(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Identifier texture, int light, int overlay)
    {
        final int damage = SpectriteUtils.getItemStackStDamage(stack);
        final VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers,
                SpectriteClient.CLIENT_INSTANCE.getHeldItemEntityHueLayer(tridentEntityModel.getLayer(texture),
                        damage, ((SpectriteTridentItem) stack.getItem()).isCharged(stack)), false, stack.hasGlint());
        tridentEntityModel.render(matrices, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}