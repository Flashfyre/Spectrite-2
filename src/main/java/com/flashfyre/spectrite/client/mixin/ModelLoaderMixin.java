package com.flashfyre.spectrite.client.mixin;

import com.flashfyre.spectrite.Spectrite;
import com.flashfyre.spectrite.client.SpectriteClient;
import com.flashfyre.spectrite.client.resourcePack.SpectriteResourcePack;
import com.flashfyre.spectrite.client.util.SpectriteTextureUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.MultipartUnbakedModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.IntFunction;

@Environment(EnvType.CLIENT)
@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin
{
    @Shadow
    @Final
    private ResourceManager resourceManager;

    @Shadow
    @Final
    private Map<Identifier, Pair<SpriteAtlasTexture, SpriteAtlasTexture.Data>> spriteAtlasData;

    @Shadow
    protected abstract void addModel(ModelIdentifier modelId);

    private boolean spectrite$addModelRecursive;

    private static Identifier spectrite$blocksAtlasId = new Identifier("textures/atlas/blocks.png");

    private static String[] spectrite$existingBlockStateBlockDependencyModels;
    private static HashMap<String, String[]> spectrite$blockDependencyModelsMap = new HashMap<>();
    private static HashMap<String, String> spectrite$blockDependencyModelVariantMatchMap = new HashMap<>();
    private static HashMap<String, String[]> spectrite$itemDependencyModelsMap = new HashMap<>();


    static
    {
        spectrite$existingBlockStateBlockDependencyModels = new String[]{"chorus_plant", "chorus_flower"};
        spectrite$blockDependencyModelsMap.put("diamond_ore", new String[]{
                "spectrite_ore",
                "spectrite_ore_nether",
                "spectrite_ore_end"
        });
        spectrite$blockDependencyModelsMap.put("deepslate_diamond_ore", new String[]{
                "spectrite_ore_deepslate",
                "spectrite_ore_blackstone"
        });
        spectrite$blockDependencyModelsMap.put("diamond_block", new String[]{"spectrite_block"});
        spectrite$blockDependencyModelsMap.put("stone_bricks", new String[]{"superchromatic_stone_bricks"});
        spectrite$blockDependencyModelsMap.put("mossy_stone_bricks", new String[]{"mossy_superchromatic_stone_bricks"});
        spectrite$blockDependencyModelsMap.put("cracked_stone_bricks", new String[]{"cracked_superchromatic_stone_bricks"});
        spectrite$blockDependencyModelsMap.put("chiseled_stone_bricks", new String[]{"chiseled_superchromatic_stone_bricks"});
        spectrite$blockDependencyModelsMap.put("chorus_plant", new String[]{"superchromatic_chorus_plant"});
        spectrite$blockDependencyModelVariantMatchMap.put("chorus_plant", "down=false,east=false,north=false,south=false,up=false,west=false");
        spectrite$blockDependencyModelsMap.put("chorus_flower", new String[]{"superchromatic_chorus_flower"});
        spectrite$itemDependencyModelsMap.put("diamond", new String[]{"spectrite_gem"});
        spectrite$itemDependencyModelsMap.put("blaze_rod", new String[]{"spectrite_rod"});
        spectrite$itemDependencyModelsMap.put("bow", new String[]{"spectrite_bow", "depleted_spectrite_bow"});
        spectrite$itemDependencyModelsMap.put("diamond_sword", new String[]{"spectrite_sword", "depleted_spectrite_sword"});
        spectrite$itemDependencyModelsMap.put("diamond_shovel", new String[]{"spectrite_shovel", "depleted_spectrite_shovel"});
        spectrite$itemDependencyModelsMap.put("diamond_pickaxe", new String[]{"spectrite_pickaxe", "depleted_spectrite_pickaxe"});
        spectrite$itemDependencyModelsMap.put("diamond_axe", new String[]{"spectrite_axe", "depleted_spectrite_axe"});
        spectrite$itemDependencyModelsMap.put("diamond_hoe", new String[]{"spectrite_hoe", "depleted_spectrite_hoe"});
        spectrite$itemDependencyModelsMap.put("diamond_helmet", new String[]{"spectrite_helmet", "depleted_spectrite_helmet"});
        spectrite$itemDependencyModelsMap.put("diamond_chestplate", new String[]{"spectrite_chestplate", "depleted_spectrite_chestplate"});
        spectrite$itemDependencyModelsMap.put("diamond_leggings", new String[]{"spectrite_leggings", "depleted_spectrite_leggings"});
        spectrite$itemDependencyModelsMap.put("diamond_boots", new String[]{"spectrite_boots", "depleted_spectrite_boots"});
        spectrite$itemDependencyModelsMap.put("shield", new String[]{"spectrite_shield", "depleted_spectrite_shield"});
        spectrite$itemDependencyModelsMap.put("shield_blocking", new String[]{"spectrite_shield_blocking", "depleted_spectrite_shield_blocking"});
        spectrite$itemDependencyModelsMap.put("trident", new String[]{"spectrite_trident", "depleted_spectrite_trident"});
        spectrite$itemDependencyModelsMap.put("trident_in_hand", new String[]{"spectrite_trident_in_hand", "depleted_spectrite_trident_in_hand"});
        spectrite$itemDependencyModelsMap.put("trident_throwing", new String[]{"spectrite_trident_throwing", "depleted_spectrite_trident_throwing"});
        spectrite$itemDependencyModelsMap.put("fire_charge", new String[]{"spectrite_bomb"});
        spectrite$itemDependencyModelsMap.put("golden_apple", new String[]{"superchromatic_apple"});
        spectrite$itemDependencyModelsMap.put("golden_carrot", new String[]{"superchromatic_carrot"});
        spectrite$itemDependencyModelsMap.put("axolotl_bucket", new String[]{"superchromatic_axolotl_bucket"});
        spectrite$itemDependencyModelsMap.put("ender_pearl", new String[]{"superchromatic_ender_pearl"});
        spectrite$itemDependencyModelsMap.put("nether_star", new String[]{"superchromatic_nether_star"});
        spectrite$itemDependencyModelsMap.put("chorus_fruit", new String[]{"superchromatic_chorus_fruit"});
        spectrite$itemDependencyModelsMap.put("experience_bottle", new String[]{"superchromatic_elixir"});
        spectrite$itemDependencyModelsMap.put("diamond_horse_armor", new String[]{"spectrite_horse_armor"});
    }

    @Inject(method = "putModel", at = @At("HEAD"))
    private void spectrite$injectOnPutModel(Identifier id, UnbakedModel unbakedModel, CallbackInfo ci)
    {
        if (id instanceof ModelIdentifier modelId)
        {
            final String namespace = modelId.getNamespace();
            final String path = modelId.getPath();
            final String variant = modelId.getVariant();
            if ("minecraft".equals(namespace))
            {
                if (spectrite$blockDependencyModelsMap.containsKey(path))
                {
                    if (spectrite$blockDependencyModelVariantMatchMap.containsKey(path) && !variant.equals(spectrite$blockDependencyModelVariantMatchMap.get(path)))
                        return;
                    if (unbakedModel instanceof WeightedUnbakedModel weightedUnbakedModel)
                        generateSpectriteBlockModelsAndTextures(weightedUnbakedModel, path);
                    else if (unbakedModel instanceof MultipartUnbakedModel multipartUnbakedModel)
                        generateSpectriteBlockModelsAndTextures(multipartUnbakedModel.getModels().stream().findFirst().orElse(null), path);
                } else if (spectrite$itemDependencyModelsMap.containsKey(path) && unbakedModel instanceof JsonUnbakedModel jsonUnbakedModel)
                {
                    final String[] modPaths = spectrite$itemDependencyModelsMap.get(path);
                    final List<ModelOverride> modelOverrides = jsonUnbakedModel.getOverrides();
                    generateSpectriteItemModelsAndTextures(new Identifier(jsonUnbakedModel.id), path, modelId.getVariant(),
                            modPaths, !modelOverrides.isEmpty(), false);

                    try
                    {
                        for (ModelOverride modelOverride : modelOverrides)
                        {
                            final String overrideModelPath = modelOverride.getModelId().getPath();
                            final int modelNameIndex = overrideModelPath.lastIndexOf('/') + 1;
                            if (modelNameIndex > 0)
                            {
                                final String modelName = overrideModelPath.substring(modelNameIndex);
                                final String[] overrideModPaths = spectrite$itemDependencyModelsMap.containsKey(modelName)
                                        ? spectrite$itemDependencyModelsMap.get(modelName)
                                        : Arrays.stream(modPaths).map(p -> p + "_" + modelName).toArray(size -> new String[size]);
                                generateSpectriteItemModelsAndTextures(modelOverride.getModelId(), modelName, null,
                                        overrideModPaths, true, true);
                            }
                        }
                    } catch (Exception e)
                    {
                    }
                }
            }
        }
    }

    private void generateSpectriteBlockModelsAndTextures(WeightedUnbakedModel weightedUnbakedModel, String path)
    {
        final SpectriteResourcePack resourcePack = SpectriteClient.CLIENT_INSTANCE.resourcePack;

        try
        {
            final boolean useExistingBlockState = Arrays.stream(spectrite$existingBlockStateBlockDependencyModels).anyMatch(m -> m.equals(path));
            final Map.Entry<Identifier, Integer>[] baseModelEntries = weightedUnbakedModel.getVariants().stream()
                    .map(v -> new AbstractMap.SimpleEntry<>(
                            SpectriteTextureUtils.getBaseModelTextureLocation(resourceManager, v.getLocation()), v.getWeight()))
                    .toArray((IntFunction<Map.Entry<Identifier, Integer>[]>) Map.Entry[]::new);

            final String[] modPaths = spectrite$blockDependencyModelsMap.get(path);
            for (String modPath : modPaths)
            {
                final Map<String, NativeImage[]> texturesByName = SpectriteTextureUtils.getAllBlockTextures(resourceManager, modPath, baseModelEntries);

                if (texturesByName != null)
                {
                    final String[] variants = SpectriteTextureUtils.getBlockVariants(modPath);
                    if (!useExistingBlockState)
                    {
                        final JsonObject blockStateObj = SpectriteTextureUtils.getBlockStateObj(modPath, baseModelEntries);
                        resourcePack.putBlockState(modPath, blockStateObj.toString().getBytes());
                    }
                    for (String variant : variants)
                    {
                        for (int v = 0; v < (useExistingBlockState ? 1 : baseModelEntries.length); v++)
                        {
                            final JsonObject modelObj = SpectriteTextureUtils.getBlockModelObj(modPath, variant, v);
                            final String modelName = useExistingBlockState
                                    ? modPath + (!variant.isEmpty() ? "_" + variant : "")
                                    : SpectriteTextureUtils.getBlockModelName(modPath, variant, v);
                            resourcePack.putBlockModel(modelName, modelObj.toString().getBytes());
                        }
                    }

                    for (Map.Entry<String, NativeImage[]> entry : texturesByName.entrySet())
                    {
                        final String textureName = entry.getKey();
                        final NativeImage[] textures = entry.getValue();
                        for (int t = 0; t < textures.length; t++)
                        {
                            final String texturePath = modPath
                                    + (textureName.length() > 0 ? "_" + textureName : "") + (t > 0 ? t : "");
                            final NativeImage texture = textures[t];
                            final byte[] mcMetaBytes = SpectriteTextureUtils.getMcMetaBytes(resourceManager, null);
                            resourcePack.putImageDynamic(texturePath, "block", texture);
                            resourcePack.putMcMeta(texturePath, "block", mcMetaBytes);
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void generateSpectriteItemModelsAndTextures(Identifier modelId, String path, String variant,
                                                        String[] modPaths, boolean hasOverrides, boolean isOverride)
    {
        final SpectriteResourcePack resourcePack = SpectriteClient.CLIENT_INSTANCE.resourcePack;
        for (String modPath : modPaths)
        {
            final Identifier baseModelTextureLocation = SpectriteTextureUtils.getBaseModelTextureLocation(resourceManager,
                    modelId);
            final Map.Entry<String, Integer>[] propertyOverrides = SpectriteTextureUtils.getItemModelOverrides(modPath);

            boolean reusesTexture = modPath.startsWith("depleted_") || modPath.startsWith("bow_") || "shield".equals(path);
            boolean isSpecialModel = path.startsWith("bow_") || path.startsWith("shield") || path.startsWith("trident_");
            boolean overrideBuiltin = false;
            JsonObject baseModelObj = null;
            if (hasOverrides)
            {
                try (final InputStream bmis = resourceManager.getResource(new Identifier(modelId.getNamespace(),
                        "models/" + modelId.getPath() + ".json")).getInputStream())
                {
                    final String baseModelJsonString = new String(bmis.readAllBytes());
                    baseModelObj = new JsonParser().parse(baseModelJsonString).getAsJsonObject();
                    if (baseModelObj.has("parent") && !baseModelObj.get("parent").getAsString().startsWith("builtin/"))
                    {
                        overrideBuiltin = true;
                        if ("spectrite_shield".equals(modPath))
                            reusesTexture = false;
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            final JsonObject modelObj = SpectriteTextureUtils.getItemModelObj(modPath, propertyOverrides, baseModelObj);
            final String modelName = SpectriteTextureUtils.getItemModelName(modPath, null);
            resourcePack.putItemModel(modelName, modelObj.toString().getBytes());

            if (propertyOverrides.length > 0)
            {
                for (Map.Entry<String, Integer> propertyOverride : propertyOverrides)
                {
                    final JsonObject overrideModelObj = SpectriteTextureUtils.getItemPropertyOverrideModelObj(modPath, propertyOverride, baseModelObj);
                    final String overrideModelName = SpectriteTextureUtils.getItemModelName(modPath, propertyOverride);
                    resourcePack.putItemModel(overrideModelName, overrideModelObj.toString().getBytes());

                    byte[] mcMetaBytes = null;

                    if ((overrideBuiltin || !isSpecialModel) && !reusesTexture)
                    {
                        final String textureName = SpectriteTextureUtils.getItemTextureName(modPath, propertyOverride);
                        final NativeImage texture = SpectriteTextureUtils.getItemTexture(resourceManager, modPath,
                                propertyOverride, baseModelTextureLocation);
                        resourcePack.putImageDynamic(textureName, "item", texture);
                        if (texture.getHeight() != texture.getWidth())
                        {
                            if (mcMetaBytes == null)
                                mcMetaBytes = SpectriteTextureUtils.getMcMetaBytes(resourceManager, baseModelTextureLocation);
                            resourcePack.putMcMeta(textureName, "item", mcMetaBytes);
                        }
                    }
                }
            } else if ((overrideBuiltin || !isSpecialModel) && !reusesTexture)
            {
                final NativeImage texture = SpectriteTextureUtils.getItemTexture(resourceManager, modPath,
                        null, baseModelTextureLocation);
                resourcePack.putImageDynamic(modPath, "item", texture);
                if (texture.getHeight() != texture.getWidth())
                {
                    final byte[] mcMetaBytes = SpectriteTextureUtils.getMcMetaBytes(resourceManager, baseModelTextureLocation);
                    resourcePack.putMcMeta(modPath, "item", mcMetaBytes);
                }
            }

            if (isSpecialModel && !isOverride && !spectrite$addModelRecursive)
            {
                spectrite$addModelRecursive = true;
                final ModelIdentifier extraModelId = new ModelIdentifier(Spectrite.MODID + ":" + modPath, variant);
                addModel(extraModelId);
                spectrite$addModelRecursive = false;
            }
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void spectrite$injectInit(ResourceManager resourceManager, BlockColors blockColors, Profiler profiler, int i, CallbackInfo ci)
    {
        if (this.spriteAtlasData.containsKey(spectrite$blocksAtlasId))
        {
            final SpriteAtlasTextureDataAccessor blocksAtlasData = (SpriteAtlasTextureDataAccessor) this.spriteAtlasData.get(spectrite$blocksAtlasId).getSecond();
            SpectriteClient.BLOCKS_TEXTURE_SIZE = new AbstractMap.SimpleEntry<>(
                    blocksAtlasData.getWidth(), blocksAtlasData.getHeight());
        }
        SpectriteTextureUtils.clearCaches();
    }
}