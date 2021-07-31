package com.flashfyre.spectrite.client.util;

import com.flashfyre.spectrite.Spectrite;
import com.flashfyre.spectrite.client.SpectriteClient;
import com.flashfyre.spectrite.client.etc.SpectriteTextureOverlayData;
import com.flashfyre.spectrite.client.mixin.ModelPartAccessor;
import com.flashfyre.spectrite.client.resourcePack.SpectriteResourcePack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpectriteEntityRenderUtils
{
    public static Identifier CURRENT_RENDERING_SPECTRITE_COMPATIBLE_ENTITY_ID;
    public static String CURRENT_RENDERING_SPECTRITE_COMPATIBLE_ENTITY_MODEL_CLASS_NAME;
    public static Map<Identifier, Map<String, Map<Identifier, Identifier>>> ENTITY_SPECTRITE_TEXTURE_CACHE = new HashMap<>();
    public static Map<Identifier, Map<String, List<ModelPart>>> ENTITY_MODEL_PART_CACHE = new HashMap<>();
    public static Map<Identifier, Map.Entry<Integer, Integer>> ENTITY_SPECTRITE_TEXTURE_SIZE_CACHE = new HashMap<>();
    public static Map<Identifier, SpectriteTextureOverlayData> SPECTRITE_TEXTURE_OVERLAY_CACHE = new HashMap<>();
    public static SpectriteTextureOverlayData SPECTRITE_ARROW_TEXTURE_OVERLAY_DATA = new SpectriteTextureOverlayData()
            .add(0.5f, 0.0f, 0.0f, 0.15625f, 0f, 1f, false)
            .add(0.0f, 0.15625f, 0.1875f, 0.3125f, 1f, 1f, true);

    public static Identifier getOrGenerateSpectriteEntityTexture(Model model, Identifier texture, EntityType entityType)
    {
        return getOrGenerateSpectriteEntityTexture(model, texture, entityType, null);
    }

    public static Identifier getOrGenerateSpectriteEntityTexture(Model model, Identifier texture, EntityType entityType,
                                                                 String spectriteTexturePath)
    {
        return getOrGenerateSpectriteEntityTexture(model, null, texture, entityType, spectriteTexturePath, false, false);
    }

    public static Identifier getOrGenerateSpectriteChargeableEntityTexture(Model model, Identifier texture, EntityType entityType)
    {
        return getOrGenerateSpectriteEntityTexture(model, null, texture, entityType, null, false, true);
    }

    public static Identifier getOrGenerateSpectriteEntityArmorTexture(Model model, Identifier texture)
    {
        return getOrGenerateSpectriteEntityTexture(model, null, new Identifier(texture.getNamespace(),
                texture.getPath().replace("spectrite", "diamond")), null, null, true, false);
    }

    public static Identifier getOrGenerateSpectriteEntityTexture(SpectriteTextureOverlayData overlayData,
                                                                 Identifier texture, EntityType entityType, boolean chargeable)
    {
        return getOrGenerateSpectriteEntityTexture(null, overlayData, texture, entityType, null, false, chargeable);
    }

    private static Identifier getOrGenerateSpectriteEntityTexture(Model model, SpectriteTextureOverlayData overlayData,
                                                                  Identifier texture, EntityType entityType,
                                                                  String spectriteTexturePath,
                                                                  boolean isArmor, boolean cacheSize)
    {
        final Identifier entityId = entityType != null ? EntityType.getId(entityType) : CURRENT_RENDERING_SPECTRITE_COMPATIBLE_ENTITY_ID;
        final String modelClassName = model != null ? model.getClass().getName() : entityId.getPath();
        CURRENT_RENDERING_SPECTRITE_COMPATIBLE_ENTITY_MODEL_CLASS_NAME = modelClassName;
        if (!ENTITY_SPECTRITE_TEXTURE_CACHE.containsKey(entityId))
            ENTITY_SPECTRITE_TEXTURE_CACHE.put(entityId, new HashMap<>());
        final Map<String, Map<Identifier, Identifier>> entityTextureCache = ENTITY_SPECTRITE_TEXTURE_CACHE.get(entityId);
        if (!entityTextureCache.containsKey(modelClassName))
            entityTextureCache.put(modelClassName, new HashMap<>());
        final Map<Identifier, Identifier> entityModelTextureCache = entityTextureCache.get(modelClassName);
        if (!entityModelTextureCache.containsKey(texture))
        {
            final SpectriteResourcePack resourcePack = SpectriteClient.CLIENT_INSTANCE.resourcePack;
            final NativeImage spectriteEntityTexture;
            if (overlayData != null)
            {
                spectriteEntityTexture = SpectriteTextureUtils.getEntityTexture(
                        MinecraftClient.getInstance().getResourceManager(),
                        texture, overlayData);
                SPECTRITE_TEXTURE_OVERLAY_CACHE.put(entityId, overlayData);
            } else if (ENTITY_MODEL_PART_CACHE.containsKey(entityId))
            {
                final Map<String, List<ModelPart>> entityModelPartCache = ENTITY_MODEL_PART_CACHE.get(entityId);
                spectriteEntityTexture = entityModelPartCache.containsKey(modelClassName)
                        ? SpectriteTextureUtils.getEntityTexture(
                        MinecraftClient.getInstance().getResourceManager(),
                        entityId.toString(),
                        modelClassName,
                        texture,
                        entityModelPartCache.get(modelClassName))
                        : null;
            } else
                spectriteEntityTexture = null;
            if (spectriteEntityTexture != null)
            {
                final Identifier spectriteTextureLocation = spectriteTexturePath == null
                        ? isArmor
                        ? new Identifier(Spectrite.MODID, texture.getPath())
                        : SpectriteTextureUtils.getSpectriteEntityTextureLocation(texture, entityId.getPath())
                        : new Identifier(Spectrite.MODID, spectriteTexturePath);
                resourcePack.putImage(
                        "assets/" + Spectrite.MODID + "/" + spectriteTextureLocation.getPath(),
                        spectriteEntityTexture);
                entityModelTextureCache.put(texture, spectriteTextureLocation);
                if (cacheSize)
                    ENTITY_SPECTRITE_TEXTURE_SIZE_CACHE.put(spectriteTextureLocation,
                            new AbstractMap.SimpleEntry<>(spectriteEntityTexture.getWidth(), spectriteEntityTexture.getHeight()));
                return spectriteTextureLocation;
            }
            return texture;
        }

        return entityModelTextureCache.get(texture);
    }

    public static boolean containsModelPart(List<ModelPart> modelPartsList, ModelPart modelPart)
    {
        for (ModelPart mp : modelPartsList)
        {
            if (mp == modelPart)
                return true;

            final List<ModelPart> childModelPartsList = ((ModelPartAccessor) (Object) mp)
                    .getChildren().values().stream().collect(Collectors.toList());
            if (!childModelPartsList.isEmpty())
                return containsModelPart(childModelPartsList, modelPart);
        }

        return false;
    }
}
