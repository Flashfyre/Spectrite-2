package com.flashfyre.spectrite.client.util;

import com.flashfyre.spectrite.Spectrite;
import com.flashfyre.spectrite.client.etc.SpectriteTextureOverlayData;
import com.flashfyre.spectrite.client.mixin.ModelPartAccessor;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public final class SpectriteTextureUtils
{
    private static String fallbackTexture = "textures/transparent.png";
    private static byte[] spectriteBlockMcMetaBytes;
    private static String blockModelPrefix = Spectrite.MODID + ":block/";
    private static String itemModelPrefix = Spectrite.MODID + ":item/";

    public static void clearCaches()
    {
        spectriteBlockMcMetaBytes = null;
    }

    public static NativeImage getNativeImage(ResourceManager resourceManager, Identifier path)
    {
        AtomicReference<NativeImage> ret = new AtomicReference<>();

        resourceManager.getResource(path).ifPresent(r ->
        {
            try
            {
                ret.set(NativeImage.read(r.getInputStream()));
            } catch (IOException e)
            {
                Spectrite.INSTANCE.warn("Failed to load texture \"" + path + "\": " + e.getMessage());
            }
        });

        if (ret.get() != null)
            return ret.get();

        return !fallbackTexture.equals(path.getPath())
                ? getNativeImage(resourceManager, new Identifier(Spectrite.MODID, "textures/transparent.png"))
                : null;
    }

    public static Identifier getBaseModelTextureLocation(ResourceManager resourceManager, Identifier modelLocation)
    {
        AtomicReference<Identifier> ret = new AtomicReference<>();

        resourceManager.getResource(new Identifier(modelLocation.getNamespace(), "models/" + modelLocation.getPath() + ".json")).ifPresent(r ->
        {
            JsonObject modelObj = null;
            try (InputStream mis = r.getInputStream())
            {
                String modelJsonString = new String(mis.readAllBytes());
                modelObj = new JsonParser().parse(modelJsonString).getAsJsonObject();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            if (modelObj != null && modelObj.has("textures"))
            {
                final JsonObject texturesObj = modelObj.getAsJsonObject("textures");
                final Map.Entry<String, JsonElement> textureEntry = texturesObj.entrySet().stream().findAny().orElse(null);
                final String location = textureEntry != null ? textureEntry.getValue().getAsString() : null;
                if (location != null)
                {
                    int separatorIndex = location.indexOf(':');
                    ret.set(separatorIndex == -1
                            ? new Identifier(location)
                            : new Identifier(location.substring(0, separatorIndex), location.substring(separatorIndex + 1)));
                }
            }
        });

        return ret.get();
    }

    public static byte[] getMcMetaBytes(ResourceManager resourceManager, Identifier textureLocation)
    {
        final Identifier mcMetaLocation;
        final AtomicReference<byte[]> ret = new AtomicReference<>(new byte[0]);
        if (textureLocation == null)
        {
            mcMetaLocation = new Identifier(Spectrite.MODID, "textures/block/spectrite_ore.png.mcmeta");
            if (spectriteBlockMcMetaBytes == null)
            {
                resourceManager.getResource(mcMetaLocation).ifPresent(r ->
                {
                    try (InputStream inputStream = r.getInputStream())
                    {
                        spectriteBlockMcMetaBytes = inputStream.readAllBytes();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                });
            }
            ret.set(spectriteBlockMcMetaBytes);
        } else
        {
            mcMetaLocation = new Identifier(textureLocation.getNamespace(), "textures/" + textureLocation.getPath() + ".png.mcmeta");
            resourceManager.getResource(mcMetaLocation).ifPresent(r ->
            {
                try (InputStream inputStream = r.getInputStream())
                {
                    ret.set(inputStream.readAllBytes());
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            });
        }
        return ret.get();
    }

    public static String[] getBlockVariants(String name)
    {
        switch (name)
        {
            case "spectrite_ore":
            case "spectrite_ore_deepslate":
            case "spectrite_ore_nether":
            case "spectrite_ore_blackstone":
            case "spectrite_ore_end":
            case "spectrite_block":
                return new String[]{"odd=false", "odd=true"};
            case "superchromatic_chorus_plant":
                final List<String> variants = new ArrayList<>();
                variants.add("");
                final String[] angleVariants = new String[]{"", "y90", "y180", "y270", "x90", "x270"};
                for (int o = 0; o < 2; o++)
                {
                    for (String av : angleVariants)
                    {
                        final String angleVariantSuffix = !av.isEmpty() ? "_" + av : "";
                        variants.add("side" + angleVariantSuffix + (o == 1 ? "_odd" : ""));
                        for (int ns = 0; ns < 4; ns++)
                            variants.add("noside" + (ns > 0 ? String.valueOf(ns) : "") + angleVariantSuffix + (o == 1 ? "_odd" : ""));
                    }
                }
                return variants.toArray(new String[0]);
            case "superchromatic_chorus_flower":
                return new String[]{"", "dead", "odd", "dead_odd", "template"};
        }

        return new String[]{""};
    }

    private static String[] getBlockTextureNames(String name)
    {
        switch (name)
        {
            case "spectrite_ore":
            case "spectrite_ore_deepslate":
            case "spectrite_ore_nether":
            case "spectrite_ore_blackstone":
            case "spectrite_ore_end":
            case "spectrite_block":
                return new String[]{"ew", "ns", "top", "bottom", "top_odd", "bottom_odd"};
            case "superchromatic_chorus_plant":
                return new String[]{"side", "top", "bottom", "side_odd", "top_odd", "bottom_odd"};
            case "superchromatic_chorus_flower":
                return new String[]{"side", "top", "side_odd", "top_odd", "side_dead", "top_dead", "side_dead_odd", "top_dead_odd"};
        }

        return new String[]{""};
    }

    public static String getBlockModelName(String name, String variant, int variantIndex)
    {
        String ret = name;

        if (variant.length() > 0)
        {
            final String[] components = variant.split(",");
            for (String c : components)
            {
                final int eqIndex = c.indexOf('=');
                if (eqIndex == -1)
                    continue;
                final String key = c.substring(0, eqIndex);
                String value = c.substring(eqIndex + 1);

                if ("false".equals(value))
                    continue;
                if ("true".equals(value))
                    value = key;
                ret += "_" + value;
            }
        }

        return ret + (variantIndex > 0 ? variantIndex : "");
    }

    public static Identifier getSpectriteEntityTextureLocation(Identifier baseTextureLocation, String entityId)
    {
        final String baseTexturePath = baseTextureLocation.getPath();
        return new Identifier(Spectrite.MODID, "textures/entity/" + baseTextureLocation.getNamespace() + "/"
                + entityId + "/" + baseTexturePath.substring(baseTexturePath.lastIndexOf('/') + 1));
    }

    public static JsonObject getBlockStateObj(String name, Map.Entry<Identifier, Integer>[] baseModelEntries)
    {
        final JsonObject blockStateObj = new JsonObject();
        final JsonObject variantsObj = new JsonObject();

        final String[] variants = getBlockVariants(name);

        for (String variant : variants)
        {
            final JsonArray variantsArray = new JsonArray();
            for (int v = 0; v < baseModelEntries.length; v++)
            {
                final int weight = baseModelEntries[v].getValue().intValue();
                final JsonObject variantObj = new JsonObject();
                variantObj.addProperty("model", blockModelPrefix + getBlockModelName(name, variant, v));
                if (weight != 1)
                    variantObj.addProperty("weight", weight);
                variantsArray.add(variantObj);
            }
            if (baseModelEntries.length == 1)
                variantsObj.add(variant, variantsArray.get(0).getAsJsonObject());
            else
                variantsObj.add(variant, variantsArray);
        }

        blockStateObj.add("variants", variantsObj);

        return blockStateObj;
    }

    public static JsonObject getBlockModelObj(String name, String variant, int variantIndex)
    {
        JsonObject modelObj = new JsonObject();

        switch (name)
        {
            case "spectrite_ore":
            case "spectrite_ore_deepslate":
            case "spectrite_ore_nether":
            case "spectrite_ore_blackstone":
            case "spectrite_ore_end":
            case "spectrite_block":
                populateSpectriteSimpleBlockModelObj(modelObj, name, variant, variantIndex);
                break;
            case "superchromatic_chorus_plant":
                populateSuperchromaticChorusPlantBlockModelObj(modelObj, name, variant);
                break;
            case "superchromatic_chorus_flower":
                populateSuperchromaticChorusFlowerBlockModelObj(modelObj, name, variant);
                break;
        }

        return modelObj;
    }

    private static void populateSpectriteSimpleBlockModelObj(JsonObject modelObj, String name, String variant, int variantIndex)
    {
        final boolean isOdd = "odd=true".equals(variant);
        modelObj.addProperty("parent", "minecraft:block/block");

        final JsonObject texturesObj = new JsonObject();

        String variantIndexSuffix = variantIndex > 0 ? String.valueOf(variantIndex) : "";
        texturesObj.addProperty("top", blockModelPrefix + name + "_top" + (isOdd ? "_odd" : "") + variantIndexSuffix);
        texturesObj.addProperty("bottom", blockModelPrefix + name + "_bottom" + (isOdd ? "_odd" : "") + variantIndexSuffix);
        texturesObj.addProperty("ew", blockModelPrefix + name + (isOdd ? "_ns" : "_ew") + variantIndexSuffix);
        texturesObj.addProperty("ns", blockModelPrefix + name + (isOdd ? "_ew" : "_ns") + variantIndexSuffix);
        texturesObj.addProperty("particle", blockModelPrefix + name + (isOdd ? "_ns" : "_ew") + variantIndexSuffix);

        modelObj.add("textures", texturesObj);

        final JsonArray elementsArray = new JsonArray();
        final JsonObject elementObj = new JsonObject();

        final JsonArray fromArray = new JsonArray();
        for (int f = 0; f < 3; f++)
            fromArray.add(0);

        elementObj.add("from", fromArray);

        final JsonArray toArray = new JsonArray();
        for (int t = 0; t < 3; t++)
            toArray.add(16);

        elementObj.add("to", toArray);

        final JsonObject facesObj = new JsonObject();
        final String[] faces = new String[]{"down", "up", "north", "south", "west", "east"};

        for (String face : faces)
        {
            final JsonObject faceObj = new JsonObject();

            final JsonArray uvArray = new JsonArray();
            for (int c = 0; c < 4; c++)
                uvArray.add(c < 2 ? 0 : 16);

            faceObj.add("uv", uvArray);

            final String texture;
            switch (face)
            {
                case "down":
                    texture = "bottom";
                    break;
                case "up":
                    texture = "top";
                    break;
                case "north":
                case "south":
                    texture = "ns";
                    break;
                default:
                    texture = "ew";
                    break;
            }

            faceObj.addProperty("texture", "#" + texture);
            faceObj.addProperty("cullface", face);

            facesObj.add(face, faceObj);
        }

        elementObj.add("faces", facesObj);

        elementsArray.add(elementObj);
        modelObj.add("elements", elementsArray);

    }

    private static void populateSuperchromaticChorusPlantBlockModelObj(JsonObject modelObj, String name, String variant)
    {
        final boolean isOdd = variant.endsWith("_odd");
        String chorusVariant = variant.replace("_odd", "");
        String angleVariant = "";
        final int underscoreIndex = chorusVariant.indexOf('_');
        if (underscoreIndex > -1)
        {
            angleVariant = chorusVariant.substring(underscoreIndex + 1);
            chorusVariant = chorusVariant.substring(0, underscoreIndex);
        }

        if ("".equals(chorusVariant))
            modelObj.addProperty("parent", "minecraft:block/block");

        modelObj.addProperty("ambientocclusion", false);

        final JsonObject texturesObj = new JsonObject();

        texturesObj.addProperty("side", blockModelPrefix + name + "_side" + (isOdd ? "_odd" : ""));
        texturesObj.addProperty("top", blockModelPrefix + name + "_top" + (isOdd ? "_odd" : ""));
        texturesObj.addProperty("bottom", blockModelPrefix + name + "_bottom" + (isOdd ? "_odd" : ""));
        texturesObj.addProperty("particle", blockModelPrefix + name + "_side" + (isOdd ? "_odd" : ""));

        modelObj.add("textures", texturesObj);

        final JsonArray elementsArray = new JsonArray();

        if ("".equals(chorusVariant))
        {
            final String[] faces = new String[]{"down", "up", "north", "south", "west", "east"};
            final int[] cullfaces = new int[]{1, 4, 2, 3, 5, 0, -1};
            final int[] ignoreFaces = new int[]{0, 5, 3, 2, 4, 1, -1};
            final int[][] from = new int[][]{
                    new int[]{2, 14, 2}, new int[]{0, 2, 2}, new int[]{2, 2, 0}, new int[]{2, 2, 14}, new int[]{14, 2, 2}, new int[]{2, 0, 2}, new int[]{2, 2, 2}
            };
            final int[][] to = new int[][]{
                    new int[]{14, 16, 14}, new int[]{2, 14, 14}, new int[]{14, 14, 2}, new int[]{14, 14, 16}, new int[]{16, 14, 14}, new int[]{14, 2, 14}, new int[]{14, 14, 14}
            };
            final int[][][] faceUvs = new int[][][]{
                    new int[][]{
                            new int[]{2, 2, 14, 14}, new int[]{2, 0, 14, 2}, new int[]{2, 0, 14, 2}, new int[]{2, 0, 14, 2}, new int[]{2, 0, 14, 2}
                    },
                    new int[][]{
                            new int[]{16, 14, 14, 2}, new int[]{0, 2, 2, 14}, new int[]{14, 2, 16, 14}, new int[]{0, 2, 2, 14}, new int[]{2, 2, 14, 14}
                    },
                    new int[][]{
                            new int[]{14, 2, 2, 0}, new int[]{2, 0, 14, 2}, new int[]{2, 2, 14, 14}, new int[]{0, 2, 2, 14}, new int[]{14, 2, 16, 14}
                    },
                    new int[][]{
                            new int[]{14, 16, 2, 14}, new int[]{2, 14, 14, 16}, new int[]{2, 2, 14, 14}, new int[]{14, 2, 16, 14}, new int[]{0, 2, 2, 14}
                    },
                    new int[][]{
                            new int[]{2, 14, 0, 2}, new int[]{14, 2, 16, 14}, new int[]{0, 2, 2, 14}, new int[]{14, 2, 16, 14}, new int[]{2, 2, 14, 14}
                    },
                    new int[][]{
                            new int[]{14, 14, 2, 2}, new int[]{2, 14, 14, 16}, new int[]{2, 14, 14, 16}, new int[]{2, 14, 14, 16}, new int[]{2, 14, 14, 16}
                    },
                    new int[][]{
                            new int[]{14, 14, 2, 2}, new int[]{2, 2, 14, 14}, new int[]{2, 2, 14, 14}, new int[]{2, 2, 14, 14}, new int[]{2, 2, 14, 14}, new int[]{2, 2, 14, 14}
                    }
            };
            for (int e = 0; e < 7; e++)
            {
                final JsonObject elementObj = new JsonObject();

                final JsonArray fromArray = new JsonArray();
                final JsonArray toArray = new JsonArray();

                for (int c = 0; c < 3; c++)
                {
                    fromArray.add(from[e][c]);
                    toArray.add(to[e][c]);
                }

                elementObj.add("from", fromArray);
                elementObj.add("to", toArray);

                final JsonObject facesObj = new JsonObject();

                final String cullface = cullfaces[e] > -1 ? faces[cullfaces[e]] : null;
                final String ignoreFace = ignoreFaces[e] > -1 ? faces[ignoreFaces[e]] : null;

                boolean ignoreFaceFlag = false;

                for (int f = 0; f < faces.length; f++)
                {
                    final String face = faces[f];

                    if (face.equals(ignoreFace))
                    {
                        ignoreFaceFlag = true;
                        continue;
                    }

                    final JsonObject faceObj = new JsonObject();

                    final JsonArray uvArray = new JsonArray();

                    for (int uv : faceUvs[e][f - (ignoreFaceFlag ? 1 : 0)])
                        uvArray.add(uv);

                    faceObj.add("uv", uvArray);

                    final String texture;

                    switch (face)
                    {
                        case "up":
                            texture = "#top";
                            break;
                        case "down":
                            texture = "#bottom";
                            break;
                        default:
                            texture = "#side";
                            break;
                    }

                    faceObj.addProperty("texture", texture);

                    if (cullface != null)
                        faceObj.addProperty("cullface", cullface);

                    facesObj.add(face, faceObj);
                }

                elementObj.add("faces", facesObj);

                elementsArray.add(elementObj);
            }
        } else
        {
            if (!"side".equals(chorusVariant))
            {
                final JsonObject noSideElementObj = new JsonObject();

                int[] from = new int[]{4, 4, 4};
                int[] to = new int[]{12, 12, 12};

                switch (angleVariant)
                {
                    case "y90":
                    case "y180":
                        from[0] = 16 - from[0];
                        from[2] = from[0];
                        to[0] = 16 - to[0];
                        to[2] = to[0];
                        break;
                    case "x270":
                        from[1] = 16 - from[1];
                        from[2] = from[1];
                        to[1] = 16 - to[1];
                        to[2] = to[1];
                        break;
                }

                int temp;

                for (int c = 0; c < 3; c++)
                {
                    if (from[c] > to[c])
                    {
                        temp = from[c];
                        from[c] = to[c];
                        to[c] = temp;
                    }
                }

                final JsonArray fromArray = new JsonArray();
                for (int f : from)
                    fromArray.add(f);

                noSideElementObj.add("from", fromArray);

                final JsonArray toArray = new JsonArray();
                for (int t : to)
                    toArray.add(t);

                noSideElementObj.add("to", toArray);

                final JsonObject facesObj = new JsonObject();

                final JsonObject faceObj = new JsonObject();

                final String face;

                switch (angleVariant)
                {
                    case "y90":
                        face = "east";
                        break;
                    case "y180":
                        face = "south";
                        break;
                    case "y270":
                        face = "west";
                        break;
                    case "x90":
                        face = "down";
                        break;
                    case "x270":
                        face = "up";
                        break;
                    default:
                        face = "north";
                        break;
                }

                final String texture;

                switch (face)
                {
                    case "up":
                        texture = "#top";
                        break;
                    case "down":
                        texture = "#bottom";
                        break;
                    default:
                        texture = "#side";
                        break;
                }

                faceObj.addProperty("texture", texture);

                facesObj.add(face, faceObj);

                noSideElementObj.add("faces", facesObj);

                elementsArray.add(noSideElementObj);
            }

            if (!"noside".equals(chorusVariant))
            {
                final JsonObject sideElementObj = new JsonObject();

                int[] from;
                int[] to;
                int temp;

                switch (chorusVariant)
                {
                    case "noside1":
                    case "noside3":
                        from = new int[]{4, 4, 3};
                        to = new int[]{12, 12, 4};
                        break;
                    case "noside2":
                        from = new int[]{5, 5, 2};
                        to = new int[]{11, 11, 4};
                        break;
                    default:
                        from = new int[]{4, 4, 0};
                        to = new int[]{12, 12, 4};
                        break;
                }

                switch (angleVariant)
                {
                    case "y90":
                        temp = from[0];
                        from[0] = 16 - from[2];
                        from[2] = 16 - temp;
                        temp = to[0];
                        to[0] = 16 - to[2];
                        to[2] = 16 - temp;
                        break;
                    case "y180":
                        from[0] = 16 - from[0];
                        from[2] = 16 - from[2];
                        to[0] = 16 - to[0];
                        to[2] = 16 - to[2];
                        break;
                    case "y270":
                        temp = from[0];
                        from[0] = from[2];
                        from[2] = temp;
                        temp = to[0];
                        to[0] = to[2];
                        to[2] = temp;
                        break;
                    case "x90":
                        temp = from[1];
                        from[1] = from[2];
                        from[2] = temp;
                        temp = to[1];
                        to[1] = to[2];
                        to[2] = temp;
                        break;
                    case "x270":
                        temp = from[1];
                        from[1] = 16 - from[2];
                        from[2] = 16 - temp;
                        temp = to[1];
                        to[1] = 16 - to[2];
                        to[2] = 16 - temp;
                        break;
                }

                for (int c = 0; c < 3; c++)
                {
                    if (from[c] > to[c])
                    {
                        temp = from[c];
                        from[c] = to[c];
                        to[c] = temp;
                    }
                }

                final JsonArray fromArray = new JsonArray();
                for (int f : from)
                    fromArray.add(f);

                sideElementObj.add("from", fromArray);

                final JsonArray toArray = new JsonArray();
                for (int t : to)
                    toArray.add(t);

                sideElementObj.add("to", toArray);

                final JsonObject facesObj = new JsonObject();
                final String[] faces = new String[]{"down", "up", "north", "south", "west", "east"};

                for (String face : faces)
                {
                    final JsonObject faceObj = new JsonObject();

                    final String texture;

                    switch (face)
                    {
                        case "up":
                            texture = "#top";
                            break;
                        case "down":
                            texture = "#bottom";
                            break;
                        default:
                            texture = "#side";
                            break;
                    }

                    faceObj.addProperty("texture", texture);
                    if ("side".equals(chorusVariant))
                    {
                        String cullface;
                        switch (angleVariant)
                        {
                            case "y90":
                                cullface = "east";
                                break;
                            case "y180":
                                cullface = "south";
                                break;
                            case "y270":
                                cullface = "west";
                                break;
                            case "x90":
                                cullface = "down";
                                break;
                            case "x270":
                                cullface = "up";
                                break;
                            default:
                                cullface = "north";
                                break;
                        }
                        if (face.equals(cullface))
                            faceObj.addProperty("cullface", face);
                    }

                    facesObj.add(face, faceObj);
                }

                sideElementObj.add("faces", facesObj);

                elementsArray.add(sideElementObj);
            }
        }

        modelObj.add("elements", elementsArray);
    }

    private static void populateSuperchromaticChorusFlowerBlockModelObj(JsonObject modelObj, String name, String variant)
    {
        final boolean isOdd = variant.endsWith("odd");
        final String chorusVariant = !isOdd ? variant : !"odd".equals(variant) ? variant.replace("_odd", "") : "";

        modelObj.addProperty("parent", "template".equals(chorusVariant) ? "minecraft:block/block" : "spectrite:block/superchromatic_chorus_flower_template");

        if ("template".equals(chorusVariant))
        {
            final JsonArray elementsArray = new JsonArray();

            final String[] faces = new String[]{"down", "up", "north", "south", "west", "east"};
            final int[] flowerFaces = new int[]{1, 4, 2, 3, 5, -1};
            final int[] ignoreFaces = new int[]{0, 5, 3, 2, 4, -1};
            final int[][] from = new int[][]{
                    new int[]{2, 14, 2}, new int[]{0, 2, 2}, new int[]{2, 2, 0}, new int[]{2, 2, 14}, new int[]{14, 2, 2}, new int[]{2, 0, 2}
            };
            final int[][] to = new int[][]{
                    new int[]{14, 16, 14}, new int[]{2, 14, 14}, new int[]{14, 14, 2}, new int[]{14, 14, 16}, new int[]{16, 14, 14}, new int[]{14, 14, 14}
            };
            final int[][][] faceUvs = new int[][][]{
                    new int[][]{
                            new int[]{2, 2, 14, 14}, new int[]{2, 0, 14, 2}, new int[]{2, 0, 14, 2}, new int[]{2, 0, 14, 2}, new int[]{2, 0, 14, 2}
                    },
                    new int[][]{
                            new int[]{16, 14, 14, 2}, new int[]{0, 2, 2, 14}, new int[]{14, 2, 16, 14}, new int[]{0, 2, 2, 14}, new int[]{2, 2, 14, 14}
                    },
                    new int[][]{
                            new int[]{14, 2, 2, 0}, new int[]{2, 0, 14, 2}, new int[]{2, 2, 14, 14}, new int[]{0, 2, 2, 14}, new int[]{14, 2, 16, 14}
                    },
                    new int[][]{
                            new int[]{14, 16, 2, 14}, new int[]{2, 14, 14, 16}, new int[]{2, 2, 14, 14}, new int[]{14, 2, 16, 14}, new int[]{0, 2, 2, 14}
                    },
                    new int[][]{
                            new int[]{2, 14, 0, 2}, new int[]{14, 2, 16, 14}, new int[]{0, 2, 2, 14}, new int[]{14, 2, 16, 14}, new int[]{2, 2, 14, 14}
                    },
                    new int[][]{
                            new int[]{2, 2, 14, 14}, new int[]{14, 14, 2, 2}, new int[]{2, 2, 14, 16}, new int[]{2, 2, 14, 16}, new int[]{2, 2, 14, 16}, new int[]{2, 2, 14, 16}
                    }
            };

            for (int e = 0; e < 6; e++)
            {
                final JsonObject elementObj = new JsonObject();

                final JsonArray fromArray = new JsonArray();
                final JsonArray toArray = new JsonArray();

                for (int c = 0; c < 3; c++)
                {
                    fromArray.add(from[e][c]);
                    toArray.add(to[e][c]);
                }

                elementObj.add("from", fromArray);
                elementObj.add("to", toArray);

                final JsonObject facesObj = new JsonObject();

                final String flowerFace = flowerFaces[e] > -1 ? faces[flowerFaces[e]] : null;
                final String ignoreFace = ignoreFaces[e] > -1 ? faces[ignoreFaces[e]] : null;

                boolean ignoreFaceFlag = false;

                for (int f = 0; f < faces.length; f++)
                {
                    final String face = faces[f];

                    if (face.equals(ignoreFace))
                    {
                        ignoreFaceFlag = true;
                        continue;
                    }

                    final JsonObject faceObj = new JsonObject();

                    final JsonArray uvArray = new JsonArray();

                    for (int uv : faceUvs[e][f - (ignoreFaceFlag ? 1 : 0)])
                        uvArray.add(uv);

                    faceObj.add("uv", uvArray);

                    String texture;

                    switch (face)
                    {
                        case "up":
                            texture = "#top";
                            break;
                        case "down":
                            texture = "#bottom";
                            break;
                        default:
                            texture = "#side";
                            break;
                    }

                    if (face.equals(flowerFace))
                        texture += "_flower";

                    faceObj.addProperty("texture", texture);

                    facesObj.add(face, faceObj);
                }

                elementObj.add("faces", facesObj);

                elementsArray.add(elementObj);
            }

            modelObj.add("elements", elementsArray);
        } else
        {
            final boolean isDead = variant.contains("dead");

            final JsonObject texturesObj = new JsonObject();

            texturesObj.addProperty("side", blockModelPrefix + "superchromatic_chorus_plant_side" + (isOdd ? "_odd" : ""));
            texturesObj.addProperty("top", blockModelPrefix + "superchromatic_chorus_plant_top" + (isOdd ? "_odd" : ""));
            texturesObj.addProperty("bottom", blockModelPrefix + "superchromatic_chorus_plant_bottom" + (isOdd ? "_odd" : ""));
            texturesObj.addProperty("side_flower", blockModelPrefix + name + "_side" + (isDead ? "_dead" : "") + (isOdd ? "_odd" : ""));
            texturesObj.addProperty("top_flower", blockModelPrefix + name + "_top" + (isDead ? "_dead" : "") + (isOdd ? "_odd" : ""));
            texturesObj.addProperty("particle", blockModelPrefix + name + "_side" + (isDead ? "_dead" : "") + (isOdd ? "_odd" : ""));

            modelObj.add("textures", texturesObj);
        }
    }

    public static HashMap<String, NativeImage[]> getAllBlockTextures(ResourceManager resourceManager, String
            name, Map.Entry<Identifier, Integer>[] baseModelEntries)
    {
        final HashMap<String, NativeImage[]> ret = new HashMap<>();

        final String[] textureNames = getBlockTextureNames(name);

        for (String textureName : textureNames)
            ret.put(textureName, getBlockTextures(resourceManager, name, textureName, baseModelEntries));

        return ret;
    }

    public static NativeImage[] getBlockTextures(
            ResourceManager resourceManager,
            String name, String textureName, Map.Entry<Identifier, Integer>[] baseModelEntries)
    {
        final ArrayList<NativeImage> textures = new ArrayList<>();

        switch (name)
        {
            case "spectrite_ore":
                populateSpectriteOreTextures(textures, resourceManager, "stone", textureName, baseModelEntries);
                break;
            case "spectrite_ore_deepslate":
                populateSpectriteOreTextures(textures, resourceManager, "deepslate", textureName, baseModelEntries);
                break;
            case "spectrite_ore_nether":
                populateSpectriteOreTextures(textures, resourceManager, "netherrack", textureName, baseModelEntries);
                break;
            case "spectrite_ore_blackstone":
                populateSpectriteOreTextures(textures, resourceManager, "blackstone", textureName, baseModelEntries);
                break;
            case "spectrite_ore_end":
                populateSpectriteOreTextures(textures, resourceManager, "end_stone", textureName, baseModelEntries);
                break;
            case "spectrite_block":
                populateSpectriteBlockTextures(textures, resourceManager, textureName, baseModelEntries);
                break;
            case "superchromatic_chorus_plant":
            case "superchromatic_chorus_flower":
                populateSuperchromaticChorusBlockTextures(textures, resourceManager, name, textureName);
                break;
        }

        return textures.size() > 0 ? textures.stream().toArray(size -> new NativeImage[size]) : null;
    }

    private static void populateSpectriteOreTextures(ArrayList<NativeImage> textures, ResourceManager resourceManager,
                                                     String baseBlockName, String textureName, Map.Entry<Identifier, Integer>[] baseModelEntries)
    {
        Arrays.stream(baseModelEntries).forEach(modelEntry ->
        {
            textures.add(getSpectriteOreTexture(resourceManager, baseBlockName, textureName, modelEntry.getKey(), false));
        });
    }

    private static void populateSpectriteBlockTextures(ArrayList<NativeImage> textures, ResourceManager
            resourceManager,
                                                       String textureName, Map.Entry<Identifier, Integer>[] baseModelEntries)
    {
        Arrays.stream(baseModelEntries).forEach(modelEntry ->
                textures.add(getSpectriteBlockTexture(resourceManager, textureName, modelEntry.getKey())));
    }

    private static void populateSuperchromaticChorusBlockTextures
            (ArrayList<NativeImage> textures, ResourceManager resourceManager, String blockName,
             String textureName)
    {
        final Identifier baseBlockLocation = !"superchromatic_chorus_flower".equals(blockName) || textureName.startsWith("bottom")
                ? new Identifier("textures/block/chorus_plant.png")
                : textureName.contains("_dead") ? new Identifier("textures/block/chorus_flower_dead.png")
                : new Identifier("textures/block/chorus_flower.png");
        textures.add(getSuperchromaticChorusBlockTexture(resourceManager, textureName, baseBlockLocation));
    }

    public static Map.Entry<String, Integer>[] getItemModelOverrides(String name)
    {
        /*switch (name)
        {
            case "spectrite_helmet":
            case "spectrite_chestplate":
            case "spectrite_leggings":
            case "spectrite_boots":
            case "spectrite_sword":
            case "spectrite_pickaxe":
            case "spectrite_shield":
            case "spectrite_shield_blocking":
            case "spectrite_trident_in_hand":
            case "spectrite_trident_throwing":
                return new AbstractMap.SimpleEntry[]{
                        new AbstractMap.SimpleEntry<>("stdamage", 0),
                        new AbstractMap.SimpleEntry<>("stdamage", 1),
                        new AbstractMap.SimpleEntry<>("stdamage", 2),
                        new AbstractMap.SimpleEntry<>("stdamage", 3),
                        new AbstractMap.SimpleEntry<>("stdamage", 4)
                };
        }*/

        return new Map.Entry[0];
    }

    public static String getItemModelName(String name, Map.Entry<String, Integer> propertyOverride)
    {
        if (propertyOverride == null)
            return name;

        return name + "_" + propertyOverride.getKey() + propertyOverride.getValue();
    }

    public static String getItemTextureName(String name, Map.Entry<String, Integer> propertyOverride)
    {
        if (propertyOverride == null || propertyOverride.getValue().intValue() == 0)
        {
            if (name.startsWith("depleted_"))
                return name.substring(9);
            return name;
        }

        return name + "_" + propertyOverride.getKey() + propertyOverride.getValue();
    }

    public static JsonObject getItemModelObj(String name, Map.Entry<String, Integer>[] propertyOverrides, JsonObject baseModelObj, boolean generated)
    {
        JsonObject modelObj = new JsonObject();
        String variant = null;

        final boolean builtin = baseModelObj != null && baseModelObj.has("parent")
                && baseModelObj.get("parent").getAsString().startsWith("builtin/");
        final int modelNameOffsetIndex = name.startsWith("depleted_") ? 9 : 0;

        switch (name)
        {
            case "spectrite_shield":
            case "depleted_spectrite_shield":
            case "spectrite_shield_blocking":
            case "depleted_spectrite_shield_blocking":
                variant = name.length() - modelNameOffsetIndex > 17 ? name.substring(17 + modelNameOffsetIndex) : null;
                name = name.substring(0, 16 + modelNameOffsetIndex);
                break;
            case "spectrite_trident_in_hand":
            case "depleted_spectrite_trident_in_hand":
            case "spectrite_trident_throwing":
            case "depleted_spectrite_trident_throwing":
                variant = name.substring(18 + modelNameOffsetIndex);
                name = name.substring(0, 17 + modelNameOffsetIndex);
                break;
        }

        if (builtin)
            populateSpectriteBuiltinItemModelObj(modelObj, name, variant, propertyOverrides, baseModelObj);
        else
            populateSimpleSpectriteItemModelObj(modelObj, name, variant, propertyOverrides, baseModelObj, generated);

        return modelObj;
    }

    private static void populateSimpleSpectriteItemModelObj(JsonObject modelObj, String name, String variant,
                                                            Map.Entry<String, Integer>[] propertyOverrides,
                                                            JsonObject baseModelObj, boolean generated)
    {
        if (baseModelObj == null)
        {
            modelObj.addProperty("parent", generated ? "item/generated" : "item/handheld");

            final JsonObject texturesObj = new JsonObject();
            texturesObj.addProperty("layer0", itemModelPrefix + getItemTextureName(name, null));

            modelObj.add("textures", texturesObj);
        } else
        {
            for (Map.Entry<String, JsonElement> e : baseModelObj.entrySet())
            {
                final String key = e.getKey();
                if ("textures".equals(key))
                {
                    final JsonObject texturesObj = new JsonObject();
                    texturesObj.addProperty("layer0", itemModelPrefix + getItemTextureName(name, null));
                    modelObj.add(key, texturesObj);
                } else if ("overrides".equals(key))
                {
                    if (propertyOverrides.length == 0)
                    {
                        final JsonArray baseOverridesArray = e.getValue().getAsJsonArray();
                        final JsonArray newOverridesArray = new JsonArray();
                        for (int o = 0; o < baseOverridesArray.size(); o++)
                        {
                            final JsonObject baseOverrideObj = baseOverridesArray.get(o).getAsJsonObject();
                            final JsonObject newOverrideObj = new JsonObject();
                            for (Map.Entry<String, JsonElement> oe : baseOverrideObj.entrySet())
                            {
                                final String overrideKey = oe.getKey();
                                if (!"model".equals(overrideKey))
                                    newOverrideObj.add(overrideKey, oe.getValue());
                                else
                                {
                                    final String baseModelPath = oe.getValue().getAsString();
                                    final int modelNameStartIndex = baseModelPath.lastIndexOf('/') + 1;
                                    newOverrideObj.addProperty(overrideKey,
                                            itemModelPrefix + name + "_" + baseModelPath.substring(modelNameStartIndex));
                                }
                            }
                            newOverridesArray.add(newOverrideObj);
                        }
                        modelObj.add(key, newOverridesArray);
                    }
                } else
                    modelObj.add(key, e.getValue());
            }
        }

        final JsonArray overridesArray = propertyOverrides.length > 0
                ? new JsonArray()
                : modelObj.getAsJsonArray("overrides");

        for (Map.Entry<String, Integer> propertyOverride : propertyOverrides)
        {
            final JsonObject overrideObj = new JsonObject();

            final JsonObject predicateObj = new JsonObject();

            predicateObj.addProperty(propertyOverride.getKey(), propertyOverride.getValue());

            overrideObj.add("predicate", predicateObj);

            overrideObj.addProperty("model", itemModelPrefix + getItemModelName(name, propertyOverride));

            overridesArray.add(overrideObj);
        }

        if ((variant != null && "in_hand".equals(variant)) || (variant == null && name.endsWith("shield")))
            overridesArray.add(getBuiltinItemOtherVariantOverride(name));

        if (propertyOverrides.length > 0 && overridesArray.size() > 0)
            modelObj.add("overrides", overridesArray);
    }

    private static void populateSpectriteBuiltinItemModelObj(JsonObject modelObj, String name, String variant,
                                                             Map.Entry<String, Integer>[] propertyOverrides,
                                                             JsonObject baseModelObj)
    {
        for (Map.Entry<String, JsonElement> e : baseModelObj.entrySet())
        {
            final String key = e.getKey();
            if ("textures".equals(key))
            {
                final JsonObject texturesObj = new JsonObject();
                texturesObj.addProperty("particle", itemModelPrefix + getItemTextureName(name, null));
                modelObj.add(key, texturesObj);
            } else if (!"overrides".equals(key))
                modelObj.add(key, e.getValue());
        }

        final JsonArray overridesArray = new JsonArray();

        for (Map.Entry<String, Integer> propertyOverride : propertyOverrides)
        {
            final JsonObject overrideObj = new JsonObject();

            final JsonObject predicateObj = new JsonObject();

            predicateObj.addProperty(propertyOverride.getKey(), propertyOverride.getValue());

            overrideObj.add("predicate", predicateObj);

            overrideObj.addProperty("model", itemModelPrefix
                    + getItemModelName(name + (variant != null ? "_" + variant : ""), propertyOverride));

            overridesArray.add(overrideObj);
        }

        if ((variant != null && "in_hand".equals(variant)) || (variant == null && name.endsWith("shield")))
            overridesArray.add(getBuiltinItemOtherVariantOverride(name));

        if (overridesArray.size() > 0)
            modelObj.add("overrides", overridesArray);
    }

    private static JsonObject getBuiltinItemOtherVariantOverride(String name)
    {
        final String otherVariant = name.endsWith("shield") ? "blocking" : "throwing";

        final JsonObject overrideObj = new JsonObject();

        final JsonObject predicateObj = new JsonObject();

        predicateObj.addProperty(otherVariant, 1);

        overrideObj.add("predicate", predicateObj);

        overrideObj.addProperty("model", itemModelPrefix + name + "_" + otherVariant);

        return overrideObj;
    }

    public static JsonObject getItemPropertyOverrideModelObj(String name, Map.Entry<String, Integer> propertyOverride, JsonObject baseModelObj)
    {
        final JsonObject modelObj = new JsonObject();
        String variant = null;

        final boolean builtin = baseModelObj != null && baseModelObj.has("parent")
                && baseModelObj.get("parent").getAsString().startsWith("builtin/");

        switch (name)
        {
            case "spectrite_shield":
            case "spectrite_shield_blocking":
                variant = name.length() > 17 ? name.substring(17) : null;
                name = name.substring(0, 16);
                break;
            case "spectrite_trident_in_hand":
            case "spectrite_trident_throwing":
                variant = name.substring(18);
                name = name.substring(0, 17);
                break;
        }

        if (builtin)
            populateSpectriteBuiltinItemPropertyOverrideModelObj(modelObj, name, variant, propertyOverride, baseModelObj);
        else
            populateSimpleSpectriteItemPropertyOverrideModelObj(modelObj, name, variant, propertyOverride, baseModelObj);

        return modelObj;
    }

    private static void populateSimpleSpectriteItemPropertyOverrideModelObj(JsonObject modelObj, String name, String variant,
                                                                            Map.Entry<String, Integer> propertyOverride,
                                                                            JsonObject baseModelObj)
    {
        if (baseModelObj == null)
        {
            modelObj.addProperty("parent", "item/handheld");

            JsonObject texturesObj = new JsonObject();
            texturesObj.addProperty("layer0", itemModelPrefix + getItemTextureName(name, propertyOverride));

            modelObj.add("textures", texturesObj);
        } else
        {
            for (Map.Entry<String, JsonElement> e : baseModelObj.entrySet())
            {
                final String key = e.getKey();
                if ("textures".equals(key))
                {
                    final JsonObject texturesObj = new JsonObject();
                    texturesObj.addProperty("layer0", itemModelPrefix + getItemTextureName(name, propertyOverride));
                    modelObj.add(key, texturesObj);
                } else if (!"overrides".equals(key))
                    modelObj.add(key, e.getValue());
            }
        }
    }

    private static void populateSpectriteBuiltinItemPropertyOverrideModelObj(JsonObject modelObj, String name, String variant,
                                                                             Map.Entry<String, Integer> propertyOverride,
                                                                             JsonObject baseModelObj)
    {
        for (Map.Entry<String, JsonElement> e : baseModelObj.entrySet())
        {
            final String key = e.getKey();
            if ("textures".equals(key))
            {
                final JsonObject texturesObj = new JsonObject();
                texturesObj.addProperty("particle", itemModelPrefix + getItemTextureName(name, propertyOverride));
                modelObj.add(key, texturesObj);
            } else if (!"overrides".equals(key))
                modelObj.add(key, e.getValue());
        }
    }

    public static NativeImage getItemTexture(
            ResourceManager resourceManager, String name, Map.Entry<String, Integer> propertyOverride, Identifier baseModelLocation)
    {
        final NativeImage texture;

        switch (name)
        {
            default:
                texture = getSimpleSpectriteItemTexture(resourceManager, propertyOverride, baseModelLocation);
                break;
        }

        return texture;
    }

    public static NativeImage getSpectriteOreTexture(ResourceManager resourceManager, String baseBlockName,
                                                     String textureName, Identifier diamondOreTextureLocation,
                                                     boolean forGuiBackground)
    {
        final NativeImage diamondOre = getNativeImage(resourceManager, new Identifier(diamondOreTextureLocation.getNamespace(),
                "textures/" + diamondOreTextureLocation.getPath() + ".png"));
        final NativeImage oreBaseBlock;
        final NativeImage baseBlock;
        final int frameCount = forGuiBackground ? 1 : 32;
        final float frameHue = 1f / (float) frameCount;
        final int size = Math.min(diamondOre.getHeight(), diamondOre.getWidth());
        final int height = size;
        final int width = size;
        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        switch (baseBlockName)
        {
            case "deepslate":
            case "blackstone":
                oreBaseBlock = getNativeImage(resourceManager, new Identifier("textures/block/deepslate.png"));
                baseBlock = "deepslate".equals(baseBlockName) ? oreBaseBlock : getNativeImage(resourceManager,
                        new Identifier("textures/block/" + baseBlockName + ".png"));
                break;
            default:
                oreBaseBlock = getNativeImage(resourceManager, new Identifier("textures/block/stone.png"));
                baseBlock = "stone".equals(baseBlockName) ? oreBaseBlock : getNativeImage(resourceManager,
                        new Identifier("textures/block/" + baseBlockName + ".png"));
                break;
        }

        final NativeImage ret = new NativeImage(width, height * frameCount, false);

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < height; x++)
            {
                final int c1 = diamondOre.getColor(x, y);
                final int a1 = (c1 >> 24) & 0xFF;

                final int c2 = oreBaseBlock.getColor(x, y);
                final int a2 = (c2 >> 24) & 0xFF;

                if (a1 > 0 && a2 > 0)
                {
                    float a = 0;

                    final int[] rgb1 = new int[]{(c1 >> 16) & 0xFF, (c1 >> 8) & 0xFF, (c1) & 0xFF};
                    final int[] rgb2 = new int[]{(c2 >> 16) & 0xFF, (c2 >> 8) & 0xFF, (c2) & 0xFF};
                    int[] rgb = new int[3];

                    for (int c = 0; c < 3; c++)
                    {
                        int v1 = rgb1[c];
                        int v2 = rgb2[c];
                        if (v1 != v2)
                            a = Math.max(a, v1 > v2 ? (v1 - v2) / (float) (0xFF - v2) : (v2 - v1) / (float) v2);
                    }

                    if (a > 0f)
                    {
                        final float ai = 1f / a;

                        for (int c = 0; c < 3; c++)
                            rgb[c] = Math.round((rgb1[c] - rgb2[c]) * ai + rgb2[c]);
                    }

                    float[] rgbf = new float[]{rgb[0] / 255f, rgb[1] / 255f, rgb[2] / 255f};

                    final float gsf = (rgbf[0] + rgbf[1] + rgbf[2]) / 3f;

                    for (int c = 0; c < 3; c++)
                        rgbf[c] = gsf;

                    float overlayHue;
                    switch (textureName)
                    {
                        case "top":
                        case "bottom":
                        case "top_odd":
                        case "bottom_odd":
                            boolean isTop = textureName.startsWith("top");
                            boolean isOdd = textureName.endsWith("_odd");
                            float centerRatY = (halfHeight - Math.abs(y - halfHeight)) / (float) halfHeight;
                            float centerRatX = (halfWidth - Math.abs(x - halfWidth)) / (float) halfWidth;
                            float centerRat = (centerRatX + centerRatY) / 2f;
                            overlayHue = ((float) Math.atan2(x - halfWidth, y - halfHeight)
                                    + (((float) Math.PI) * (isTop ? 0.25f : -0.25f) * centerRat)
                                    + (float) Math.PI) / (float) Math.PI * (isTop ? -1f : 1f)
                                    + (isTop != isOdd ? 0.5f : 0f);
                            break;
                        case "ns":
                            overlayHue = (1f / 6f) + ((width - x) + (y + 1)) / (float) (height + width);
                            break;
                        default:
                            overlayHue = (2f / 3f) + ((width - x) + (y + 1)) / (float) (height + width);
                            break;
                    }

                    final int[] rgbb;
                    if (baseBlock == oreBaseBlock)
                        rgbb = rgb2;
                    else if (a < 1f)
                    {
                        final int cb = baseBlock.getColor(x, y);
                        rgbb = new int[]{(cb >> 16) & 0xFF, (cb >> 8) & 0xFF, (cb) & 0xFF};
                    } else
                        rgbb = null;

                    for (int f = 0; f < frameCount; f++)
                    {
                        final int co = MathHelper.hsvToRgb(clampHue(overlayHue), 1f, 1f);
                        final float[] overlayRgbf = new float[]{((co >> 16) & 0xFF) / 255f, ((co >> 8) & 0xFF) / 255f, ((co) & 0xFF) / 255f};

                        float[] frgbf = new float[]{rgbf[0], rgbf[1], rgbf[2]};

                        frgbf = spectriteBlend(frgbf, overlayRgbf);

                        for (int c = 0; c < 3; c++)
                        {
                            rgb[c] = Math.round(frgbf[c] * 255f);
                            if (a < 1f)
                                rgb[c] = a > 0f ? Math.round(rgb[c] * a + rgbb[c] * (1f - a)) : rgbb[c];
                        }

                        final int ca = (a1 << 24) & 0xFF000000;
                        final int cr = (rgb[0] << 16) & 0x00FF0000;
                        final int cg = (rgb[1] << 8) & 0x0000FF00;
                        final int cb = rgb[2] & 0x000000FF;
                        final int c = ca | cr | cg | cb;

                        ret.setColor(x, y + (height * f), c);

                        overlayHue -= frameHue;
                    }
                } else
                    for (int f = 0; f < frameCount; f++)
                        ret.setColor(x, y + (height * f), a1 > 0 ? c2 : c1);
            }
        }

        return ret;
    }

    public static NativeImage getSpectriteBlockTexture(ResourceManager resourceManager,
                                                       String textureName, Identifier diamondBlockTextureLocation)
    {
        final NativeImage diamondBlock = getNativeImage(resourceManager, new Identifier(diamondBlockTextureLocation.getNamespace(),
                "textures/" + diamondBlockTextureLocation.getPath() + ".png"));
        final int frameCount = 32;
        final float frameHue = 1f / 32f;
        final int size = Math.min(diamondBlock.getHeight(), diamondBlock.getWidth());
        final int height = size;
        final int width = size;
        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        final NativeImage ret = new NativeImage(width, height * frameCount, false);

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                final int cs = diamondBlock.getColor(x, y);
                final int as = (cs >> 24) & 0xFF;

                if (as > 0)
                {
                    final int[] rgbs = new int[]{(cs >> 16) & 0xFF, (cs >> 8) & 0xFF, (cs) & 0xFF};
                    int[] rgb = new int[3];

                    float[] rgbf = new float[]{rgbs[0] / 255f, rgbs[1] / 255f, rgbs[2] / 255f};

                    final float gsf = (rgbf[0] + rgbf[1] + rgbf[2]) / 3f;

                    for (int c = 0; c < 3; c++)
                        rgbf[c] = gsf;

                    float overlayHue;
                    switch (textureName)
                    {
                        case "top":
                        case "bottom":
                        case "top_odd":
                        case "bottom_odd":
                            boolean isTop = textureName.startsWith("top");
                            boolean isOdd = textureName.endsWith("_odd");
                            float centerRatY = (halfHeight - Math.abs(y - halfHeight)) / (float) halfHeight;
                            float centerRatX = (halfWidth - Math.abs(x - halfWidth)) / (float) halfWidth;
                            float centerRat = (centerRatX + centerRatY) / 2f;
                            overlayHue = ((float) Math.atan2(x - halfWidth, y - halfHeight)
                                    + (((float) Math.PI) * (isTop ? 0.25f : -0.25f) * centerRat)
                                    + (float) Math.PI) / (float) Math.PI * (isTop ? -1f : 1f)
                                    + (isTop != isOdd ? 0.5f : 0f);
                            break;
                        case "ns":
                            overlayHue = (1f / 6f) + ((width - x) + (y + 1)) / (float) (height + width);
                            break;
                        default:
                            overlayHue = (2f / 3f) + ((width - x) + (y + 1)) / (float) (height + width);
                            break;
                    }

                    for (int f = 0; f < frameCount; f++)
                    {
                        final int co = MathHelper.hsvToRgb(clampHue(overlayHue), 1f, 1f);
                        final float[] overlayRgbf = new float[]{((co >> 16) & 0xFF) / 255f, ((co >> 8) & 0xFF) / 255f, ((co) & 0xFF) / 255f};

                        float[] frgbf = new float[]{rgbf[0], rgbf[1], rgbf[2]};

                        frgbf = spectriteBlend(frgbf, overlayRgbf);

                        for (int c = 0; c < 3; c++)
                            rgb[c] = Math.round(frgbf[c] * 255f);

                        final int ca = (as << 24) & 0xFF000000;
                        final int cr = (rgb[0] << 16) & 0x00FF0000;
                        final int cg = (rgb[1] << 8) & 0x0000FF00;
                        final int cb = rgb[2] & 0x000000FF;
                        final int c = ca | cr | cg | cb;

                        ret.setColor(x, y + (height * f), c);

                        overlayHue -= frameHue;
                    }
                } else
                    for (int f = 0; f < frameCount; f++)
                        ret.setColor(x, y + (height * f), cs);
            }
        }

        return ret;
    }

    public static NativeImage getSuperchromaticChorusBlockTexture(ResourceManager resourceManager, String textureName,
                                                                  Identifier chorusBlockTextureLocation)
    {
        final NativeImage chorusBlockTexture = getNativeImage(resourceManager, chorusBlockTextureLocation);
        final int frameCount = 32;
        final float frameHue = 1f / 32f;
        final int size = Math.min(chorusBlockTexture.getHeight(), chorusBlockTexture.getWidth());
        final int height = size;
        final int width = size;

        final NativeImage ret = new NativeImage(width, height * frameCount, false);

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                final int cs = chorusBlockTexture.getColor(x, y);
                final int as = (cs >> 24) & 0xFF;

                if (as > 0)
                {
                    final int[] rgbs = new int[]{(cs >> 16) & 0xFF, (cs >> 8) & 0xFF, (cs) & 0xFF};
                    int[] rgb = new int[3];

                    float[] rgbf = new float[]{rgbs[0] / 255f, rgbs[1] / 255f, rgbs[2] / 255f};

                    final float gsf = (rgbf[0] + rgbf[1] + rgbf[2]) / 3f;

                    for (int c = 0; c < 3; c++)
                        rgbf[c] = gsf;

                    float overlayHue;
                    switch (textureName)
                    {
                        case "top":
                        case "bottom":
                        case "top_odd":
                        case "bottom_odd":
                        case "top_dead":
                        case "top_dead_odd":
                            boolean isTop = textureName.startsWith("top");
                            boolean isOdd = textureName.endsWith("_odd");
                            overlayHue = (isTop ? 0.25f : 0f) + (isTop != isOdd ? 0.5f : 0f);
                            break;
                        case "side_odd":
                        case "side_dead_odd":
                            overlayHue = (1f / 6f) + (y + 1) / (height * 2f);
                            break;
                        default:
                            overlayHue = (2f / 3f) + (y + 1) / (height * 2f);
                            break;
                    }

                    for (int f = 0; f < frameCount; f++)
                    {
                        final int co = MathHelper.hsvToRgb(clampHue(overlayHue), 1f, 1f);
                        final float[] overlayRgbf = new float[]{((co >> 16) & 0xFF) / 255f, ((co >> 8) & 0xFF) / 255f, ((co) & 0xFF) / 255f};

                        float[] frgbf = new float[]{rgbf[0], rgbf[1], rgbf[2]};

                        frgbf = spectriteBlend(frgbf, overlayRgbf);

                        for (int c = 0; c < 3; c++)
                            rgb[c] = Math.round(frgbf[c] * 255f);

                        final int ca = (as << 24) & 0xFF000000;
                        final int cr = (rgb[0] << 16) & 0x00FF0000;
                        final int cg = (rgb[1] << 8) & 0x0000FF00;
                        final int cb = rgb[2] & 0x000000FF;
                        final int c = ca | cr | cg | cb;

                        ret.setColor(x, y + (height * f), c);

                        overlayHue -= frameHue;
                    }
                } else
                    for (int f = 0; f < frameCount; f++)
                        ret.setColor(x, y + (height * f), cs);
            }
        }

        return ret;
    }

    private static NativeImage getSimpleSpectriteItemTexture(ResourceManager resourceManager,
                                                             Map.Entry<String, Integer> propertyOverride,
                                                             Identifier baseItemModelLocation)
    {
        final NativeImage baseItem = getNativeImage(resourceManager, new Identifier(baseItemModelLocation.getNamespace(),
                "textures/" + baseItemModelLocation.getPath() + ".png"));

        final int fullHeight = baseItem.getHeight();
        final int width = baseItem.getWidth();
        final int height = width;

        float saturation = 0f;

        if (propertyOverride != null && "stdamage".equals(propertyOverride.getKey()))
            saturation = 0.25f * (4 - propertyOverride.getValue());

        final int frameCount = fullHeight / width;

        final NativeImage ret = new NativeImage(width, fullHeight, false);

        for (int f = 0; f < frameCount; f++)
        {
            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    final int cs = baseItem.getColor(x, y + height * f);
                    final int as = (cs >> 24) & 0xFF;

                    final int c;

                    if (as > 0)
                    {
                        final int[] rgbs = new int[]{(cs >> 16) & 0xFF, (cs >> 8) & 0xFF, (cs) & 0xFF};
                        int[] rgb = new int[3];

                        float[] rgbf = new float[]{rgbs[0] / 255f, rgbs[1] / 255f, rgbs[2] / 255f};

                        final float gsf = (rgbf[0] + rgbf[1] + rgbf[2]) / 3f;

                        for (int cl = 0; cl < 3; cl++)
                            rgbf[cl] = gsf;

                        final float overlayHue = (2f / 3f) + ((width - x) + (y + 1)) / (float) (height + width);

                        final int co = MathHelper.hsvToRgb(clampHue(overlayHue), saturation, 1f);
                        final float[] overlayRgbf = new float[]{((co >> 16) & 0xFF) / 255f, ((co >> 8) & 0xFF) / 255f, ((co) & 0xFF) / 255f};

                        float[] frgbf = new float[]{rgbf[0], rgbf[1], rgbf[2]};

                        if (saturation > 0f)
                            frgbf = spectriteBlend(frgbf, overlayRgbf);

                        for (int cl = 0; cl < 3; cl++)
                            rgb[cl] = Math.round(frgbf[cl] * 255f);

                        final int ca = (as << 24) & 0xFF000000;
                        final int cr = (rgb[0] << 16) & 0x00FF0000;
                        final int cg = (rgb[1] << 8) & 0x0000FF00;
                        final int cb = rgb[2] & 0x000000FF;
                        c = ca | cr | cg | cb;
                    } else
                        c = cs;

                    ret.setColor(x, y + (height * f), c);
                }
            }
        }

        return ret;
    }

    private static NativeImage getSpectriteShieldBaseTexture(ResourceManager resourceManager, NativeImage baseTexture,
                                                             List<ModelPart> modelPartList)
    {
        final Identifier diamondBlockTextureLocation = getBaseModelTextureLocation(resourceManager, new Identifier("block/diamond_block"));
        final NativeImage baseBlockTexture = getNativeImage(resourceManager, new Identifier(diamondBlockTextureLocation.getNamespace(),
                "textures/" + diamondBlockTextureLocation.getPath() + ".png"));

        if (modelPartList.size() < 2)
            return baseTexture;

        final int height = baseTexture.getHeight();
        final int width = baseTexture.getWidth();
        final int baseBlockSize = Math.min(baseBlockTexture.getHeight(), baseBlockTexture.getWidth());
        final int baseBlockHeight = baseBlockSize;
        final int baseBlockWidth = baseBlockSize;
        final float rat = (baseBlockSize << 2) / (float) width;
        final NativeImage ret = new NativeImage(width, height, true);
        ret.copyFrom(baseTexture);

        final ModelPart plateModelPart = modelPartList.get(1);
        final ModelPart.Cuboid plateCuboid = plateModelPart.cuboids.get(0);

        for (ModelPart.Quad side : plateCuboid.sides)
        {
            final Direction direction = Direction.fromVector((int) side.direction.getX(), (int) side.direction.getY(), (int) side.direction.getZ());
            float minU = 1024f;
            float maxU = 0f;
            float minV = 1024f;
            float maxV = 0f;

            for (int v = 0; v < side.vertices.length; v++)
            {
                final ModelPart.Vertex vertex = side.vertices[v];
                if (vertex.u < minU)
                    minU = vertex.u;
                if (vertex.u > maxU)
                    maxU = vertex.u;
                if (vertex.v < minV)
                    minV = vertex.v;
                if (vertex.v > maxV)
                    maxV = vertex.v;
            }

            final int x1 = MathHelper.clamp((int) Math.floor(minU * width), 0, width);
            final int x2 = MathHelper.clamp((int) Math.floor(maxU * width), 0, width);
            final int y1 = MathHelper.clamp((int) Math.floor(minV * height), 0, height);
            final int y2 = MathHelper.clamp((int) Math.floor(maxV * height), 0, height);

            final int sizeX = x2 - x1;
            final int sizeY = y2 - y1;
            final int relMaxX = sizeX - 1;
            final int relMaxY = sizeY - 1;

            final int relWidthDiff = (int) (sizeX - (baseBlockWidth / rat));
            final int relXStretchStart = (int) (sizeX + relWidthDiff * 2f);

            final float ratXStretchStart = relXStretchStart / (float) sizeX;
            final float ratXStretchEnd = (sizeX - relXStretchStart) / (float) sizeX;
            final float ratXStretchSize = ratXStretchEnd - ratXStretchStart;

            final int relHeightDiff = (int) (sizeY - (baseBlockHeight / rat));
            final int relYStretchStart = (int) ((sizeY / 2f) - relHeightDiff);

            final float ratYStretchStart = relYStretchStart / (float) sizeY;
            final float ratYStretchEnd = (sizeY - relYStretchStart) / (float) sizeY;
            final float ratYStretchSize = ratYStretchEnd - ratYStretchStart;

            for (int y = y1; y < y2; y++)
            {
                final int ry = y - y1;
                final float ratY = ry / (float) sizeY;
                final float ratYS = relMaxY > 0 ? ry / (float) relMaxY : 0.5f;
                for (int x = x1; x < x2; x++)
                {
                    final int rx = x - x1;
                    final float ratX = rx / (float) sizeX;
                    final float ratXS = relMaxX > 0 ? rx / (float) relMaxX : 0.5f;
                    final int cs = baseTexture.getColor(x, y);
                    final int as = (cs >> 24) & 0xFF;

                    final int c;

                    if (as > 0)
                    {
                        final List<Integer> blockBlendColors = new ArrayList<>();
                        float xbf = rx * rat;
                        float ybf = ry * rat;
                        float xbbf = -1f;
                        float ybbf = -1f;

                        switch (direction)
                        {
                            case UP:
                            case DOWN:
                            case NORTH:
                            case SOUTH:
                                if (ratX >= ratXStretchStart)
                                    xbf += ratX >= ratXStretchEnd
                                            ? sizeX * rat * ratXStretchSize
                                            : sizeX * rat * ((ratXStretchSize) - (ratXStretchSize - (ratX - ratXStretchStart)));
                                break;
                            case EAST:
                            case WEST:
                                if (ratXS > 0f && ratXS < 1f)
                                    xbbf = (baseBlockWidth - (float) Math.ceil(rat)) - xbf;
                                break;
                        }

                        switch (direction)
                        {
                            case UP:
                            case DOWN:
                                if (ratYS > 0f && ratYS < 1f)
                                    ybbf = (baseBlockHeight - (float) Math.ceil(rat)) - ybf;
                                break;
                            case EAST:
                            case WEST:
                            case NORTH:
                            case SOUTH:
                                if (ratY >= ratYStretchStart)
                                    ybf -= ratY >= ratYStretchEnd
                                            ? sizeY * rat * ratYStretchSize * 0.5f
                                            : sizeY * rat * ((ratYStretchSize) - (ratYStretchSize - (ratY - ratYStretchStart))) * 0.5f;
                                break;
                        }

                        final int xb = (int) xbf;
                        final int yb = (int) ybf;
                        final int xbb = (int) Math.ceil(xbbf);
                        final int ybb = (int) Math.ceil(ybbf);

                        for (int ys = 0; ys < (int) Math.max(rat, 1f); ys++)
                        {
                            for (int xs = 0; xs < (int) Math.max(rat, 1f); xs++)
                            {
                                blockBlendColors.add(baseBlockTexture.getColor(xb + xs, yb + ys));
                                if (xbb > -1)
                                {
                                    blockBlendColors.add(baseBlockTexture.getColor(xbb + xs, yb + ys));
                                    if (ybb > -1)
                                        blockBlendColors.add(baseBlockTexture.getColor(xbb + xs, ybb + ys));
                                }
                                if (ybb > -1)
                                    blockBlendColors.add(baseBlockTexture.getColor(xb + xs, ybb + ys));
                            }
                        }

                        if (blockBlendColors.size() > 1 && blockBlendColors.stream().distinct().count() > 1l)
                        {
                            final float[] rgbf = new float[3];
                            final float colorCount = (float) blockBlendColors.size();
                            for (int cb : blockBlendColors)
                            {
                                final float rfb = ((cb >> 16) & 0xFF);
                                final float gfb = ((cb >> 8) & 0xFF);
                                final float bfb = ((cb) & 0xFF);
                                rgbf[0] += rfb / colorCount;
                                rgbf[1] += gfb / colorCount;
                                rgbf[2] += bfb / colorCount;
                            }
                            final int ca = (as << 24) & 0xFF000000;
                            final int cr = (Math.round(rgbf[0]) << 16) & 0x00FF0000;
                            final int cg = (Math.round(rgbf[1]) << 8) & 0x0000FF00;
                            final int cb = Math.round(rgbf[2]) & 0x000000FF;
                            c = ca | cr | cg | cb;
                        } else
                            c = blockBlendColors.get(0);
                    } else
                        c = cs;

                    ret.setColor(x, y, c);
                }
            }
        }

        return ret;
    }

    private static NativeImage getEntityBaseTexture(
            ResourceManager resourceManager,
            String entityId,
            String modelClassName,
            Identifier baseTextureLocation,
            List<ModelPart> modelPartList)
    {
        NativeImage ret = getNativeImage(resourceManager, baseTextureLocation);

        if (ShieldEntityModel.class.getName().equals(modelClassName))
            ret = getSpectriteShieldBaseTexture(resourceManager, ret, modelPartList);

        return ret;
    }

    public static NativeImage getEntityTexture(
            ResourceManager resourceManager,
            Identifier baseTextureLocation,
            SpectriteTextureOverlayData spectriteTextureOverlay)
    {
        final NativeImage baseTexture = getNativeImage(resourceManager, baseTextureLocation);

        final int height = baseTexture.getHeight();
        final int width = baseTexture.getWidth();

        final NativeImage ret = new NativeImage(width, height, true);
        ret.copyFrom(baseTexture);

        spectriteTextureOverlay.apply(ret);

        return ret;
    }

    public static NativeImage getEntityTexture(
            ResourceManager resourceManager,
            String entityId,
            String modelClassName,
            Identifier baseTextureLocation,
            List<ModelPart> rootModelPartList,
            boolean isPartial)
    {
        if (Spectrite.DEBUG)
            Spectrite.INSTANCE.log("Generating Superchromatic texture for " + entityId + " (" + modelClassName + ")");

        final NativeImage baseTexture = getEntityBaseTexture(resourceManager, entityId, modelClassName, baseTextureLocation, rootModelPartList);

        final int height = baseTexture.getHeight();
        final int width = baseTexture.getWidth();

        final NativeImage ret = new NativeImage(width, height, true);

        final List<ModelPart> modelPartList = new ArrayList<>();
        final Map<ModelPart, ModelPart> modelPartParents = new HashMap<>();
        populateModelParts(rootModelPartList, null, modelPartList, modelPartParents);

        float minY = 1000f;
        float maxY = -1000f;
        final List<Float> cuboidsMinY = new ArrayList<>();
        final List<Float> cuboidsMaxY = new ArrayList<>();

        for (ModelPart modelPart : modelPartList)
        {
            final int[] rotationIndexes = getRotationIndexes(modelPart, modelPartParents);
            final int pitchIndex = rotationIndexes[0];
            final int yawIndex = rotationIndexes[1];
            final int rollIndex = rotationIndexes[2];

            for (ModelPart.Cuboid cuboid : modelPart.cuboids)
            {
                float cuboidMinY = 1000f;
                float cuboidMaxY = -1000f;
                for (ModelPart.Quad side : cuboid.sides)
                {
                    for (int v = 0; v < side.vertices.length; v++)
                    {
                        final ModelPart.Vertex vertex = side.vertices[v];
                        final float posY = getEntityModelVertexPosY(cuboid, vertex, pitchIndex, yawIndex, rollIndex)
                                + getPivotY(modelPart, modelPartParents);
                        if (posY < cuboidMinY)
                        {
                            cuboidMinY = posY;
                            if (posY < minY)
                                minY = posY;
                        }
                        if (posY > cuboidMaxY)
                        {
                            cuboidMaxY = posY;
                            if (posY > maxY)
                                maxY = posY;
                        }
                    }
                }
                cuboidsMinY.add(cuboidMinY);
                cuboidsMaxY.add(cuboidMaxY);
            }
        }

        final float entityHeight = maxY - minY;
        int ci = 0;

        for (ModelPart modelPart : modelPartList)
        {
            final int[] rotationIndexes = getRotationIndexes(modelPart, modelPartParents);
            final int pitchIndex = rotationIndexes[0];
            final int yawIndex = rotationIndexes[1];
            final int rollIndex = rotationIndexes[2];

            for (ModelPart.Cuboid cuboid : modelPart.cuboids)
            {
                for (ModelPart.Quad side : cuboid.sides)
                {
                    final Direction direction = Direction.fromVector((int) side.direction.getX(), (int) side.direction.getY(), (int) side.direction.getZ());
                    final boolean switchCoords;
                    final boolean invertCoords;
                    final int modPitch = pitchIndex % 2;
                    final int modYaw = yawIndex % 2;
                    final int modRoll = rollIndex % 2;
                    if (modRoll == 0)
                    {
                        if (modPitch == 0)
                        {
                            switchCoords = false;
                            invertCoords = (direction == Direction.UP) == (pitchIndex == 0 == (rollIndex == 0));
                        } else
                        {
                            switchCoords = direction != Direction.UP && direction != Direction.DOWN;
                            invertCoords = (direction == Direction.EAST || direction == Direction.NORTH) == (pitchIndex == 1 == (rollIndex == 0));
                        }
                    } else
                    {
                        if (modYaw == 0)
                        {
                            switchCoords = true;
                            invertCoords = (direction == Direction.EAST || direction == Direction.SOUTH) == (rollIndex == 1 == (yawIndex == 0));
                        } else
                        {
                            switchCoords = (direction != Direction.UP && direction != Direction.DOWN) == (modPitch == 0);
                            if (modPitch == 0)
                                invertCoords = (direction == Direction.EAST || direction == Direction.NORTH) == (pitchIndex == 0 == (rollIndex == 1 == (yawIndex == 3)));
                            else
                                invertCoords = (direction == Direction.UP) == (pitchIndex == 1 == (rollIndex == 1 == (yawIndex == 1)));
                        }
                    }
                    float minU = 1024f;
                    float maxU = 0f;
                    float minV = 1024f;
                    float maxV = 0f;
                    float minPosY = 1000f;
                    float maxPosY = -1000f;
                    for (int v = 0; v < side.vertices.length; v++)
                    {
                        final ModelPart.Vertex vertex = side.vertices[v];
                        float posY = getEntityModelVertexPosY(cuboid, vertex, pitchIndex, yawIndex, rollIndex)
                                + getPivotY(modelPart, modelPartParents);
                        if (vertex.u < minU)
                            minU = vertex.u;
                        if (vertex.u > maxU)
                            maxU = vertex.u;
                        if (vertex.v < minV)
                            minV = vertex.v;
                        if (vertex.v > maxV)
                            maxV = vertex.v;
                        if (posY < minPosY)
                            minPosY = posY;
                        if (posY > maxPosY)
                            maxPosY = posY;
                    }

                    final int x1 = MathHelper.clamp((int) Math.floor(minU * width), 0, width);
                    final int x2 = MathHelper.clamp((int) Math.floor(maxU * width), 0, width);
                    final int y1 = MathHelper.clamp((int) Math.floor(minV * height), 0, height);
                    final int y2 = MathHelper.clamp((int) Math.floor(maxV * height), 0, height);
                    final float posYDiff = maxPosY - minPosY;

                    for (int y = switchCoords ? x1 : y1; switchCoords ? y < x2 : y < y2; y++)
                    {
                        final float size = (float) (switchCoords ? x2 - x1 : y2 - y1) - 1f;
                        final float ratio = size > 0 ? switchCoords ? (y - x1) / size : (y - y1) / size : 0f;
                        final float overlayHue = (2f / 3f) + ((entityHeight - maxY)
                                + (invertCoords ? cuboidsMaxY.get(ci) - ratio * posYDiff
                                : cuboidsMinY.get(ci) + ratio * posYDiff)) / entityHeight;

                        final int co = MathHelper.hsvToRgb(clampHue(overlayHue), 1f, 1f);
                        final float[] overlayRgbf = new float[]{((co >> 16) & 0xFF) / 255f, ((co >> 8) & 0xFF) / 255f, ((co) & 0xFF) / 255f};

                        for (int x = switchCoords ? y1 : x1; switchCoords ? x < y2 : x < x2; x++)
                        {
                            final int primaryCoord = switchCoords ? y : x;
                            final int secondaryCoord = switchCoords ? x : y;
                            final int cs = baseTexture.getColor(primaryCoord, secondaryCoord);
                            final int as = (cs >> 24) & 0xFF;
                            final int c;

                            if (as > 0)
                            {
                                final int[] rgbs = new int[]{(cs >> 16) & 0xFF, (cs >> 8) & 0xFF, (cs) & 0xFF};
                                int[] rgb = new int[3];

                                float[] rgbf = new float[]{rgbs[0] / 255f, rgbs[1] / 255f, rgbs[2] / 255f};
                                final float[] orgbf = new float[]{rgbf[0], rgbf[1], rgbf[2]};

                                final float gsf = (rgbf[0] + rgbf[1] + rgbf[2]) / 3f;

                                for (int cl = 0; cl < 3; cl++)
                                    rgbf[cl] = gsf;

                                float[] frgbf = new float[]{rgbf[0], rgbf[1], rgbf[2]};

                                frgbf = spectriteBlend(frgbf, overlayRgbf);

                                if (isPartial)
                                {
                                    for (int cl = 0; cl < 3; cl++)
                                        frgbf[cl] = (orgbf[cl] * 0.375f) + (frgbf[cl] * 0.625f);
                                }

                                for (int cl = 0; cl < 3; cl++)
                                    rgb[cl] = Math.round(frgbf[cl] * 255f);

                                final int ca = (as << 24) & 0xFF000000;
                                final int cr = (rgb[0] << 16) & 0x00FF0000;
                                final int cg = (rgb[1] << 8) & 0x0000FF00;
                                final int cb = rgb[2] & 0x000000FF;
                                c = ca | cr | cg | cb;
                            } else
                                c = cs;

                            ret.setColor(primaryCoord, secondaryCoord, c);
                        }
                    }
                }
                ci++;
            }
        }

        return ret;
    }

    private static void populateModelParts(
            List<ModelPart> parentModelPartList, ModelPart parentPart,
            List<ModelPart> modelPartList, Map<ModelPart, ModelPart> modelPartParents)
    {
        for (ModelPart modelPart : parentModelPartList)
        {
            modelPartList.add(modelPart);
            if (parentPart != null)
                modelPartParents.put(modelPart, parentPart);
            final List<ModelPart> childModelPartsList = ((ModelPartAccessor) (Object) modelPart)
                    .getChildren().values().stream().collect(Collectors.toList());
            if (!childModelPartsList.isEmpty())
                populateModelParts(childModelPartsList, modelPart, modelPartList, modelPartParents);
        }
    }

    private static float getPivotY(ModelPart modelPart, Map<ModelPart, ModelPart> modelPartParents)
    {
        float pivotY = modelPart.pivotY;

        ModelPart parentPart = modelPart;
        while (modelPartParents.containsKey(parentPart))
        {
            parentPart = modelPartParents.get(parentPart);
            pivotY += parentPart.pivotY;
        }

        return pivotY;
    }

    private static int[] getRotationIndexes(ModelPart modelPart, Map<ModelPart, ModelPart> modelPartParents)
    {
        float pitch = modelPart.pitch;
        float yaw = modelPart.yaw;
        float roll = modelPart.roll;

        ModelPart parentPart = modelPart;
        while (modelPartParents.containsKey(parentPart))
        {
            parentPart = modelPartParents.get(parentPart);
            pitch += parentPart.pitch;
            yaw += parentPart.yaw;
            roll += parentPart.roll;
        }

        int pitchIndex = pitch != 0f
                ? (int) Math.round((pitch / (Math.PI / 2f)) - 0.01f)
                : 0;
        int yawIndex = yaw != 0f
                ? (int) Math.round((yaw / (Math.PI / 2f)) - 0.01f)
                : 0;
        int rollIndex = roll != 0f
                ? (int) Math.round((roll / (Math.PI / 2f)) - 0.01f)
                : 0;

        if (pitchIndex > 4)
            pitchIndex = pitchIndex % 4;
        else
        {
            while (pitchIndex < 0)
                pitchIndex += 4;
        }

        if (yawIndex > 4)
            yawIndex = yawIndex % 4;
        else
        {
            while (yawIndex < 0)
                yawIndex += 4;
        }

        if (rollIndex > 4)
            rollIndex = rollIndex % 4;
        else
        {
            while (rollIndex < 0)
                rollIndex += 4;
        }

        return new int[]{pitchIndex, yawIndex, rollIndex};
    }

    private static float getEntityModelVertexPosY(ModelPart.Cuboid cuboid, ModelPart.Vertex vertex, int pitchIndex, int yawIndex, int rollIndex)
    {
        final Vec3f pos = vertex.pos;
        final int modPitch = pitchIndex % 2;
        final int modYaw = yawIndex % 2;
        final int modRoll = rollIndex % 2;
        final float minX = cuboid.minX;
        final float maxX = cuboid.maxX;
        final float minY = cuboid.minY;
        final float maxY = cuboid.maxY;
        final float minZ = cuboid.minZ;
        final float maxZ = cuboid.maxZ;
        if (modPitch == 0)
        {
            if (modRoll == 0)
                return pitchIndex == 0 == (rollIndex == 0) ? pos.getY() : (pos.getY() - minY) + ((minY * -1f) - (maxY - minY));
            if (modYaw == 0)
                return rollIndex == 1 == (yawIndex == 0) ? pos.getX() : (pos.getX() - minX) + ((minX * -1f) - (maxX - minX));
            return pitchIndex == 0 == (rollIndex == 1 == (yawIndex == 1)) ? pos.getZ() : (pos.getZ() - minZ) + ((minZ * -1f) - (maxZ - minZ));
        }
        if (modRoll == 0)
            return pitchIndex == 1 == (rollIndex == 2) ? pos.getZ() : (pos.getZ() - minZ) + ((minZ * -1f) - (maxZ - minZ));
        if (modYaw == 0)
            return rollIndex == 1 == (yawIndex == 0) ? pos.getX() : (pos.getX() - minX) + ((minX * -1f) - (maxX - minX));
        return pitchIndex == 1 == (rollIndex == 1 == (yawIndex == 1)) ? pos.getY() : (pos.getY() - minY) + ((minY * -1f) - (maxY - minY));
    }

    private static NativeImage getStretchedBlockTexture(NativeImage blockTexture, int width, int height, float rat)
    {
        final int baseBlockSize = Math.min(blockTexture.getHeight(), blockTexture.getWidth());
        final int blockWidth = baseBlockSize;
        final int blockHeight = baseBlockSize;
        final NativeImage ret = new NativeImage(width, height, true);

        if (rat >= 1f)
        {
            final int ri = (int) rat;
            final int widthDiff = (int) ((blockWidth - width) / rat);
            final int heightDiff = (int) ((blockHeight - height) / rat);
            final float widthThreshold = width / (float) widthDiff;
            final float heightThreshold = height / (float) heightDiff;
            final float halfWidthThreshold = widthThreshold / 2f;
            final float halfHeightThreshold = heightThreshold / 2f;

            for (int y = 0; y < height; y++)
            {
                final float ratY = y / (float) height;
                for (int x = 0; x < width; x++)
                {
                    final float ratX = x / (float) width;
                    final int cs = blockTexture.getColor(x, y);
                    final int as = (cs >> 24) & 0xFF;

                    final int c;

                    if (as > 0)
                    {
                        final List<Integer> blockBlendColors = new ArrayList<>();

                        int xc = x + ri;
                        int yc = y + ri;
                        int xcc = xc + ri;
                        int ycc = yc + ri;

                        int xbt = xc + (int) Math.floor((x - halfWidthThreshold) / widthThreshold) * ri;
                        int xbbt = ((int) Math.floor((xc - halfWidthThreshold) / widthThreshold) * ri) - (xbt - xc) >= ri ? xbt + ri : -1;
                        int ybt = yc + (int) Math.floor((y - halfHeightThreshold) / heightThreshold) * ri;
                        int ybbt = ((int) Math.floor((yc - halfHeightThreshold) / heightThreshold) * ri) - (ybt - yc) >= ri ? ybt + ri : -1;

                        final int xb = xbt;
                        final int yb = ybt;
                        final int xbb = xbbt;
                        final int ybb = ybbt;

                        for (int ys = 0; ys < ri; ys++)
                        {
                            for (int xs = 0; xs < ri; xs++)
                            {
                                blockBlendColors.add(blockTexture.getColor(xb + xs, yb + ys));
                                if (xbb > -1)
                                {
                                    blockBlendColors.add(blockTexture.getColor(xbb + xs, yb + ys));
                                    if (ybb > -1)
                                        blockBlendColors.add(blockTexture.getColor(xbb + xs, ybb + ys));
                                }
                                if (ybb > -1)
                                    blockBlendColors.add(blockTexture.getColor(xb + xs, ybb + ys));
                            }
                        }

                        if (blockBlendColors.size() > 1 && blockBlendColors.stream().distinct().count() > 1l)
                        {
                            final float[] rgbf = new float[3];
                            final float colorCount = (float) blockBlendColors.size();
                            for (int cb : blockBlendColors)
                            {
                                final float rfb = ((cb >> 16) & 0xFF);
                                final float gfb = ((cb >> 8) & 0xFF);
                                final float bfb = ((cb) & 0xFF);
                                rgbf[0] += rfb / colorCount;
                                rgbf[1] += gfb / colorCount;
                                rgbf[2] += bfb / colorCount;
                            }
                            final int ca = (as << 24) & 0xFF000000;
                            final int cr = (Math.round(rgbf[0]) << 16) & 0x00FF0000;
                            final int cg = (Math.round(rgbf[1]) << 8) & 0x0000FF00;
                            final int cb = Math.round(rgbf[2]) & 0x000000FF;
                            c = ca | cr | cg | cb;
                        } else
                            c = blockBlendColors.get(0);
                    } else
                        c = cs;

                    ret.setColor(x, y, c);
                }
            }
        }

        return ret;
    }

    public static float[] spectriteBlend(float[] a, float[] b)
    {
        final float[] ret = new float[3];

        if ((a[0] + a[1] + a[2]) / 3f > 0.5f)
        {
            for (int c = 0; c < 3; c++)
                ret[c] = 1f - 2f * (1f - a[c]) * (1f - b[c]);
        } else
        {
            for (int c = 0; c < 3; c++)
                ret[c] = 2f * a[c] * b[c];
        }

        for (int c = 0; c < 3; c++)
        {
            ret[c] = a[c] * 0.75f + ret[c] * 0.25f;
            ret[c] = (1f - ret[c]) * ret[c] * b[c] + ret[c] * (1f - (1f - ret[c]) * (1f - b[c]));
        }

        return ret;
    }

    public static float clampHue(float hue)
    {
        return hue - (float) Math.floor(hue);
    }
}
