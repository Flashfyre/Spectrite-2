package com.flashfyre.spectrite.client.render;

import com.flashfyre.spectrite.Spectrite;
import com.flashfyre.spectrite.client.SpectriteClient;
import com.flashfyre.spectrite.client.util.SpectriteEntityRenderUtils;
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
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Collections;

@Environment(EnvType.CLIENT)
public class SpectriteShieldItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer, SimpleSynchronousResourceReloadListener
{
    private final Identifier id;
    private final Identifier shieldEntityTexture;
    private ShieldEntityModel shieldEntityModel;

    public SpectriteShieldItemRenderer()
    {
        this.id = new Identifier(Spectrite.MODID, "spectrite_shield_renderer");
        this.shieldEntityTexture = new Identifier("textures/entity/shield_base_nopattern.png");
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
        this.shieldEntityModel = new ShieldEntityModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EntityModelLayers.SHIELD));
    }

    @Override
    public void render(ItemStack stack, ModelTransformation.Mode renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();
        matrices.scale(1.0F, -1.0F, -1.0F);
        SpectriteEntityRenderUtils.CURRENT_RENDERING_ENTITY_ID = new Identifier("shield");
        renderModel(stack, matrices, vertexConsumers, SpectriteEntityRenderUtils.getOrGenerateSpectriteEntityTexture(shieldEntityModel,
                shieldEntityTexture, null, false), light, overlay);
        matrices.pop();
        SpectriteEntityRenderUtils.CURRENT_RENDERING_ENTITY_ID = null;
    }

    private void renderModel(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Identifier texture, int light, int overlay)
    {
        final int damage = SpectriteUtils.getItemStackStDamage(stack);
        final VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers,
                SpectriteClient.CLIENT_INSTANCE.getHueLayer(shieldEntityModel.getLayer(texture), damage, false), true, stack.hasGlint());
        shieldEntityModel.getHandle().render(matrices, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        shieldEntityModel.getPlate().render(matrices, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}