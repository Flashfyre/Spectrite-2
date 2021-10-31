package com.flashfyre.spectrite.client.resourcePack;

import com.flashfyre.spectrite.Spectrite;
import com.flashfyre.spectrite.client.util.SpectriteEntityRenderUtils;
import com.flashfyre.spectrite.client.util.SpectriteGeoEntityRenderUtils;
import com.flashfyre.spectrite.client.util.SpectriteGeoEntityTextureUtils;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.geo.render.built.GeoBone;

import java.util.List;
import java.util.Map;

public class SpectriteResourcePackGeoEntityExtension implements SpectriteResourcePackExtension
{
    @Override
    public void findResources(SpectriteResourcePack spectriteResourcePack, ResourceManager resourceManager)
    {
        for (Map.Entry<Identifier, Map<String, Map<Identifier, Identifier>>> entry : SpectriteEntityRenderUtils.ENTITY_SPECTRITE_TEXTURE_CACHE.entrySet())
        {
            final Identifier entityId = entry.getKey();
            final Map<String, List<GeoBone>> entityGeoBoneCache = SpectriteGeoEntityRenderUtils.ENTITY_GEO_BONE_CACHE.getOrDefault(entityId, null);
            if (entityGeoBoneCache != null)
            {
                for (Map.Entry<String, Map<Identifier, Identifier>> entityEntry : entry.getValue().entrySet())
                {
                    final String modelClassName = entityEntry.getKey();
                    if (entityGeoBoneCache.containsKey(modelClassName))
                    {
                        for (Map.Entry<Identifier, Identifier> entityModelEntry : entityEntry.getValue().entrySet())
                        {
                            final NativeImage spectriteEntityTexture =
                                    SpectriteGeoEntityTextureUtils.getEntityTexture(
                                            resourceManager, entityModelEntry.getKey(), entityGeoBoneCache.get(modelClassName));
                            spectriteResourcePack.putImage(
                                    "assets/" + Spectrite.MODID + "/" + entityModelEntry.getValue().getPath(),
                                    spectriteEntityTexture);
                        }
                    }
                }
            }
        }
    }
}
