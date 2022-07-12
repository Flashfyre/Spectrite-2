package com.flashfyre.spectrite.client.util;

import com.flashfyre.spectrite.Spectrite;
import com.flashfyre.spectrite.client.SpectriteClient;
import com.flashfyre.spectrite.client.resourcePack.SpectriteResourcePack;
import com.flashfyre.spectrite.client.resourcePack.SpectriteResourcePackGeoEntityExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpectriteGeoEntityRenderUtils
{

    public static Map<Identifier, Map<String, List<GeoBone>>> ENTITY_GEO_BONE_CACHE = new HashMap<>();

    public static Identifier getOrGenerateSpectriteEntityTexture(GeoModel model, Identifier texture, EntityType entityType)
    {
        return getOrGenerateSpectriteEntityTexture(model, texture, entityType, null);
    }

    public static Identifier getOrGenerateSpectriteEntityTexture(GeoModel model, Identifier texture, EntityType entityType,
                                                                 String spectriteTexturePath)
    {
        return getOrGenerateSpectriteEntityTexture(model, texture, entityType, spectriteTexturePath, false);
    }

    private static Identifier getOrGenerateSpectriteEntityTexture(GeoModel model, Identifier texture, EntityType entityType,
                                                                  String spectriteTexturePath, boolean cacheSize)
    {
        final Identifier entityId = entityType != null ? EntityType.getId(entityType) : SpectriteEntityRenderUtils.CURRENT_RENDERING_ENTITY_ID;
        final String modelClassName = model != null ? model.getClass().getName() : entityId.getPath();
        SpectriteEntityRenderUtils.CURRENT_RENDERING_ENTITY_MODEL_CLASS_NAME = modelClassName;
        if (!SpectriteEntityRenderUtils.ENTITY_SUPERCHROMATIC_TEXTURE_CACHE.containsKey(entityId))
            SpectriteEntityRenderUtils.ENTITY_SUPERCHROMATIC_TEXTURE_CACHE.put(entityId, new HashMap<>());
        final Map<String, Map<Map.Entry<Identifier, Boolean>, Identifier>> entityTextureCache = SpectriteEntityRenderUtils.ENTITY_SUPERCHROMATIC_TEXTURE_CACHE.get(entityId);
        if (!entityTextureCache.containsKey(modelClassName))
            entityTextureCache.put(modelClassName, new HashMap<>());
        final Map<Map.Entry<Identifier, Boolean>, Identifier> entityModelTextureCache = entityTextureCache.get(modelClassName);
        final Map.Entry<Identifier, Boolean> textureEntry = new AbstractMap.SimpleEntry<>(texture, false);
        if (!entityModelTextureCache.containsKey(textureEntry))
        {
            final SpectriteResourcePack resourcePack = SpectriteClient.CLIENT_INSTANCE.resourcePack;
            if (!resourcePack.hasGeoEntityExtension())
                resourcePack.setGeoEntityExtension(new SpectriteResourcePackGeoEntityExtension());
            final NativeImage spectriteEntityTexture;
            if (ENTITY_GEO_BONE_CACHE.containsKey(entityId))
            {
                final Map<String, List<GeoBone>> entityGeoBoneCache = ENTITY_GEO_BONE_CACHE.get(entityId);
                spectriteEntityTexture = entityGeoBoneCache.containsKey(modelClassName)
                        ? SpectriteGeoEntityTextureUtils.getEntityTexture(
                        MinecraftClient.getInstance().getResourceManager(),
                        texture,
                        entityGeoBoneCache.get(modelClassName))
                        : null;
            } else
                spectriteEntityTexture = null;
            if (spectriteEntityTexture != null)
            {
                final Identifier spectriteTextureLocation = spectriteTexturePath == null
                        ? SpectriteTextureUtils.getSpectriteEntityTextureLocation(texture, entityId.getPath())
                        : new Identifier(Spectrite.MODID, spectriteTexturePath);
                resourcePack.putImage(
                        "assets/" + Spectrite.MODID + "/" + spectriteTextureLocation.getPath(),
                        spectriteEntityTexture);
                entityModelTextureCache.put(textureEntry, spectriteTextureLocation);
                if (cacheSize)
                    SpectriteEntityRenderUtils.ENTITY_SPECTRITE_TEXTURE_SIZE_CACHE.put(spectriteTextureLocation,
                            new AbstractMap.SimpleEntry<>(spectriteEntityTexture.getWidth(), spectriteEntityTexture.getHeight()));
                return spectriteTextureLocation;
            }
            return texture;
        }

        return entityModelTextureCache.get(textureEntry);
    }

    public static boolean containsGeoBone(List<GeoBone> geoBonesList, GeoBone geoBone)
    {
        for (GeoBone gb : geoBonesList)
        {
            if (gb == geoBone)
                return true;

            if (!gb.childBones.isEmpty())
                return containsGeoBone(gb.childBones, geoBone);
        }

        return false;
    }
}
