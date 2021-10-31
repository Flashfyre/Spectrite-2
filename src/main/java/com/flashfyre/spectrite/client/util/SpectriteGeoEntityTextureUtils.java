package com.flashfyre.spectrite.client.util;

import com.flashfyre.spectrite.client.etc.IOrigin;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoQuad;
import software.bernie.geckolib3.geo.render.built.GeoVertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpectriteGeoEntityTextureUtils
{
    public static NativeImage getEntityTexture(
            ResourceManager resourceManager,
            Identifier baseTextureLocation,
            List<GeoBone> rootGeoBoneList)
    {
        final NativeImage baseTexture = SpectriteTextureUtils.getNativeImage(resourceManager, baseTextureLocation);

        final int height = baseTexture.getHeight();
        final int width = baseTexture.getWidth();

        final NativeImage ret = new NativeImage(width, height, true);

        final List<GeoBone> geoBoneList = new ArrayList<>();
        final Map<GeoBone, GeoBone> geoBoneParents = new HashMap<>();
        populateGeoBones(rootGeoBoneList, null, geoBoneList, geoBoneParents);

        float minY = 1000f;
        float maxY = -1000f;
        final List<Float> cubesMinY = new ArrayList<>();
        final List<Float> cubesMaxY = new ArrayList<>();

        for (GeoBone geoBone : geoBoneList)
        {
            final int[] rotationIndexes = getRotationIndexes(geoBone, geoBoneParents);
            final int pitchIndex = rotationIndexes[0];
            final int yawIndex = rotationIndexes[1];
            final int rollIndex = rotationIndexes[2];

            for (GeoCube cube : geoBone.childCubes)
            {
                float cubeMinY = 1000f;
                float cubeMaxY = -1000f;
                for (GeoQuad quad : cube.quads)
                {
                    for (int q = 0; q < quad.vertices.length; q++)
                    {
                        final GeoVertex vertex = quad.vertices[q];
                        final float posY = getEntityModelVertexPosY(cube, vertex, pitchIndex, yawIndex, rollIndex)
                                + getPivotY(geoBone, geoBoneParents);
                        if (posY < cubeMinY)
                        {
                            cubeMinY = posY;
                            if (posY < minY)
                                minY = posY;
                        }
                        if (posY > cubeMaxY)
                        {
                            cubeMaxY = posY;
                            if (posY > maxY)
                                maxY = posY;
                        }
                    }
                }
                cubesMinY.add(cubeMinY);
                cubesMaxY.add(cubeMaxY);
            }
        }

        final float entityHeight = maxY - minY;
        int ci = 0;

        for (GeoBone geoBone : geoBoneList)
        {
            final int[] rotationIndexes = getRotationIndexes(geoBone, geoBoneParents);
            final int pitchIndex = rotationIndexes[0];
            final int yawIndex = rotationIndexes[1];
            final int rollIndex = rotationIndexes[2];

            for (GeoCube cube : geoBone.childCubes)
            {
                for (GeoQuad quad : cube.quads)
                {
                    final Direction direction = quad.direction;
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
                            invertCoords = (direction == Direction.DOWN) == (pitchIndex == 0 == (rollIndex == 0));
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
                                invertCoords = (direction == Direction.DOWN) == (pitchIndex == 1 == (rollIndex == 1 == (yawIndex == 1)));
                        }
                    }
                    float minU = 1024f;
                    float maxU = 0f;
                    float minV = 1024f;
                    float maxV = 0f;
                    float minPosY = 1000f;
                    float maxPosY = -1000f;
                    for (int v = 0; v < quad.vertices.length; v++)
                    {
                        final GeoVertex vertex = quad.vertices[v];
                        float posY = getEntityModelVertexPosY(cube, vertex, pitchIndex, yawIndex, rollIndex)
                                + getPivotY(geoBone, geoBoneParents);
                        if (vertex.textureU < minU)
                            minU = vertex.textureU;
                        if (vertex.textureU > maxU)
                            maxU = vertex.textureU;
                        if (vertex.textureV < minV)
                            minV = vertex.textureV;
                        if (vertex.textureV > maxV)
                            maxV = vertex.textureV;
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
                                + (invertCoords ? cubesMaxY.get(ci) - ratio * posYDiff
                                : cubesMinY.get(ci) + ratio * posYDiff)) / entityHeight;

                        final int co = MathHelper.hsvToRgb(SpectriteTextureUtils.clampHue(overlayHue), 1f, 1f);
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

                                final float gsf = (rgbf[0] + rgbf[1] + rgbf[2]) / 3f;

                                for (int cl = 0; cl < 3; cl++)
                                    rgbf[cl] = gsf;

                                float[] frgbf = new float[]{rgbf[0], rgbf[1], rgbf[2]};

                                frgbf = SpectriteTextureUtils.spectriteBlend(frgbf, overlayRgbf);

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

    private static void populateGeoBones(
            List<GeoBone> parentGeoBoneList, GeoBone parentBone,
            List<GeoBone> geoBoneList, Map<GeoBone, GeoBone> geoBoneParents)
    {
        for (GeoBone geoBone : parentGeoBoneList)
        {
            geoBoneList.add(geoBone);
            if (parentBone != null)
                geoBoneParents.put(geoBone, parentBone);
            if (!geoBone.childBones.isEmpty())
                populateGeoBones(geoBone.childBones, geoBone, geoBoneList, geoBoneParents);
        }
    }

    private static float getPivotY(GeoBone geoBone, Map<GeoBone, GeoBone> geoBoneParents)
    {
        float pivotY = geoBone.pivotY;

        GeoBone parentBone = geoBone;
        while (geoBoneParents.containsKey(parentBone))
        {
            parentBone = geoBoneParents.get(parentBone);
            pivotY += parentBone.pivotY;
        }

        return pivotY;
    }

    private static int[] getRotationIndexes(GeoBone geoBone, Map<GeoBone, GeoBone> geoBoneParents)
    {
        float pitch = geoBone.getRotationX();
        float yaw = geoBone.getRotationY();
        float roll = geoBone.getRotationZ();

        GeoBone parentPart = geoBone;
        while (geoBoneParents.containsKey(parentPart))
        {
            parentPart = geoBoneParents.get(parentPart);
            pitch += parentPart.getRotationX();
            yaw += parentPart.getRotationY();
            roll += parentPart.getRotationZ();
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

    private static float getEntityModelVertexPosY(GeoCube cube, GeoVertex vertex, int pitchIndex, int yawIndex, int rollIndex)
    {
        final Vec3f pos = vertex.position.copy();
        pos.multiplyComponentwise(16f, 16f, 16f);
        final int modPitch = pitchIndex % 2;
        final int modYaw = yawIndex % 2;
        final int modRoll = rollIndex % 2;
        final Vec3f origin = ((IOrigin) cube).getOrigin();
        final float minX = -(origin.getX() + cube.size.getX());
        final float maxX = -origin.getX();
        final float minY = origin.getY();
        final float maxY = minY + cube.size.getY();
        final float minZ = origin.getZ();
        final float maxZ = minZ + cube.size.getZ();
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
}
