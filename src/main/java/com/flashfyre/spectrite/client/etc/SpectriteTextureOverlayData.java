package com.flashfyre.spectrite.client.etc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class SpectriteTextureOverlayData
{
    private final List<SpectriteTextureOverlay> overlays;

    public SpectriteTextureOverlayData()
    {
        overlays = new ArrayList<>();
    }

    public SpectriteTextureOverlayData add(float u1, float v1, float u2, float v2, float from, float to, boolean useV)
    {
        overlays.add(new SpectriteTextureOverlay(u1, v1, u2, v2, from, to, useV));
        return this;
    }

    public void apply(NativeImage image)
    {
        for (SpectriteTextureOverlay overlay : overlays)
            overlay.apply(image);
    }
}
