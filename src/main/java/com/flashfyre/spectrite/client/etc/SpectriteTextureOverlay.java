package com.flashfyre.spectrite.client.etc;

import com.flashfyre.spectrite.client.util.SpectriteTextureUtils;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.math.MathHelper;

public class SpectriteTextureOverlay
{
    private float u1;
    private float v1;
    private float u2;
    private float v2;
    private float from;
    private float to;
    private boolean useV;

    SpectriteTextureOverlay(float u1, float v1, float u2, float v2, float from, float to, boolean useV)
    {
        assert (from <= to);
        this.u1 = u1;
        this.v1 = v1;
        this.u2 = u2;
        this.v2 = v2;
        this.from = from;
        this.to = to;
        this.useV = useV;
    }

    public void apply(NativeImage image)
    {
        final int width = image.getWidth();
        final int height = image.getHeight();

        final int x1 = (int) (u1 * (float) width);
        final int y1 = (int) (v1 * (float) height);
        final int x2 = (int) (u2 * (float) width);
        final int y2 = (int) (v2 * (float) height);

        final boolean reverseX = x2 < x1;
        final boolean reverseY = y2 < y1;
        final boolean reverseCoord = useV ? reverseY : reverseX;

        final int minX = reverseX ? x2 : x1;
        final int minY = reverseY ? y2 : y1;
        final int maxX = reverseX ? x1 : x2;
        final int maxY = reverseY ? y1 : y2;

        for (int y = useV ? minY : minX; y < (useV ? maxY : maxX); y++)
        {
            final int ry = y - (useV ? minY : minX);
            final int size = useV ? maxY - minY : maxX - minX;
            final float overlayHue = (2f / 3f) +
                    from + ((to - from) * (reverseCoord ? 1f - (ry / (float) size) : (ry / (float) size)));

            final int co = MathHelper.hsvToRgb(SpectriteTextureUtils.clampHue(overlayHue), 1f, 1f);
            final float[] overlayRgbf = new float[]{((co >> 16) & 0xFF) / 255f, ((co >> 8) & 0xFF) / 255f, ((co) & 0xFF) / 255f};

            for (int x = minX; x < maxX; x++)
            {
                final int primaryCoord = useV ? x : y;
                final int secondaryCoord = useV ? y : x;

                final int cs = image.getColor(primaryCoord, secondaryCoord);
                final int as = (cs >> 24) & 0xFF;

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
                    final int c = ca | cr | cg | cb;
                    image.setColor(primaryCoord, secondaryCoord, c);
                }
            }
        }
    }
}
