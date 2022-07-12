package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.client.util.SpectriteEntityRenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(ModelPart.class)
public class ModelPartMixin
{
    @Shadow
    @Final
    private List<ModelPart.Cuboid> cuboids;

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
            at = @At(value = "HEAD"))
    private void spectrite$injectGenerateSpectriteEntityTexture(MatrixStack matrices, VertexConsumer vertices,
                                                                int light, int overlay, float red, float green, float blue,
                                                                float alpha, CallbackInfo ci)
    {
        if (!SpectriteEntityRenderUtils.CURRENT_RENDERING_ENTITY_SPECTRITE_COMPATIBLE)
            return;
        final Identifier entityId = SpectriteEntityRenderUtils.CURRENT_RENDERING_ENTITY_ID;
        final String modelClassName = SpectriteEntityRenderUtils.CURRENT_RENDERING_ENTITY_MODEL_CLASS_NAME;
        if (entityId != null && SpectriteEntityRenderUtils.ENTITY_SUPERCHROMATIC_TEXTURE_CACHE.containsKey(entityId)
                && SpectriteEntityRenderUtils.ENTITY_SUPERCHROMATIC_TEXTURE_CACHE.get(entityId).containsKey(modelClassName))
        {
            final Map<Map.Entry<Identifier, Boolean>, Identifier> entityModelTextureCache =
                    SpectriteEntityRenderUtils.ENTITY_SUPERCHROMATIC_TEXTURE_CACHE.get(entityId).get(modelClassName);
            if (entityModelTextureCache.isEmpty() && (!this.cuboids.isEmpty() || !((ModelPartAccessor) this).getChildren().isEmpty()))
            {
                final List<ModelPart> modelPartCache;
                final Map<String, List<ModelPart>> entityModelPartCache;
                final ModelPart modelPart = ((ModelPart) (Object) this);
                if (!SpectriteEntityRenderUtils.ENTITY_MODEL_PART_CACHE.containsKey(entityId))
                    SpectriteEntityRenderUtils.ENTITY_MODEL_PART_CACHE.put(entityId, new HashMap<>());
                entityModelPartCache = SpectriteEntityRenderUtils.ENTITY_MODEL_PART_CACHE.get(entityId);
                if (entityModelPartCache.containsKey(modelClassName))
                {
                    modelPartCache = entityModelPartCache.get(modelClassName);
                    if (SpectriteEntityRenderUtils.containsModelPart(modelPartCache, modelPart))
                        return;
                } else
                {
                    modelPartCache = new ArrayList<>();
                    entityModelPartCache.put(modelClassName, modelPartCache);
                }
                modelPartCache.add(modelPart);
            }
        }
    }
}
