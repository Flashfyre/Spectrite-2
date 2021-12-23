package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.client.util.SpectriteEntityRenderUtils;
import com.flashfyre.spectrite.client.util.SpectriteGeoEntityRenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(IGeoRenderer.class)
public interface IGeoRendererMixin
{
    @Inject(method = "renderRecursively",
            at = @At(value = "HEAD"))
    private void spectrite$injectGenerateSpectriteEntityTexture(GeoBone bone, MatrixStack stack, VertexConsumer bufferIn, int packedLightIn,
                                                                int packedOverlayIn, float red, float green, float blue, float alpha, CallbackInfo ci)
    {
        final Identifier entityId = SpectriteEntityRenderUtils.CURRENT_RENDERING_SPECTRITE_COMPATIBLE_ENTITY_ID;
        final String modelClassName = SpectriteEntityRenderUtils.CURRENT_RENDERING_SPECTRITE_COMPATIBLE_ENTITY_MODEL_CLASS_NAME;
        if (entityId != null && SpectriteEntityRenderUtils.ENTITY_SUPERCHROMATIC_TEXTURE_CACHE.containsKey(entityId)
                && SpectriteEntityRenderUtils.ENTITY_SUPERCHROMATIC_TEXTURE_CACHE.get(entityId).containsKey(modelClassName))
        {
            final Map<Map.Entry<Identifier, Boolean>, Identifier> entityModelTextureCache = SpectriteEntityRenderUtils.ENTITY_SUPERCHROMATIC_TEXTURE_CACHE.get(entityId).get(modelClassName);
            if (entityModelTextureCache.isEmpty() && (!bone.childCubes.isEmpty() || !bone.childCubes.isEmpty()))
            {
                final List<GeoBone> geoBoneCache;
                final Map<String, List<GeoBone>> entityGeoBoneCache;
                if (!SpectriteGeoEntityRenderUtils.ENTITY_GEO_BONE_CACHE.containsKey(entityId))
                    SpectriteGeoEntityRenderUtils.ENTITY_GEO_BONE_CACHE.put(entityId, new HashMap<>());
                entityGeoBoneCache = SpectriteGeoEntityRenderUtils.ENTITY_GEO_BONE_CACHE.get(entityId);
                if (entityGeoBoneCache.containsKey(modelClassName))
                {
                    geoBoneCache = entityGeoBoneCache.get(modelClassName);
                    if (SpectriteGeoEntityRenderUtils.containsGeoBone(geoBoneCache, bone))
                        return;
                } else
                {
                    geoBoneCache = new ArrayList<>();
                    entityGeoBoneCache.put(modelClassName, geoBoneCache);
                }
                geoBoneCache.add(bone);
            }
        }
    }
}
