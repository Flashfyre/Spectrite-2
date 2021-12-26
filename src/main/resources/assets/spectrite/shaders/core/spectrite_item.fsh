#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float STime;
uniform float Saturation;
uniform int Charged;
uniform int Partial;

in vec2 texCoord;
in vec2 oneTexel;
in float scale;

out vec4 fragColor;

vec3 rgb2hsv(vec3 c)
{
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = c.g < c.b ? vec4(c.bg, K.wz) : vec4(c.gb, K.xy);
    vec4 q = c.r < p.x ? vec4(p.xyw, c.r) : vec4(c.r, p.yzx);

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

vec3 spectriteBlend(vec3 a, vec3 b)
{
    vec3 r;
    if ((a.r + a.g + a.b) / 3 > 0.5)
    {
        r = 1.0 - 2.0 * (1.0 - a) * (1.0 - b);
    } else
    {
        r = 2.0 * a * b;
    }
    r = (r * 0.25) + (a * 0.75);
    return (1.0 - r) * r * b + r * (1.0 - (1.0 - r) * (1.0 - b));
}

vec3 screen(vec3 a, vec3 b)
{
    vec3 r = 1.0 - (1.0 - a) * (1.0 - b);
    return r;
}

void main()
{
    vec4 color = texture(Sampler0, texCoord);
    if (color.a == 0.0) discard;
    vec3 hsv = rgb2hsv(color.rgb);
    float overlayHueOffset = STime * 0.3;
    hsv[0] = mod(hsv[0] + overlayHueOffset, 1.0);
    hsv[1] *= Saturation;

    vec3 overlayHsv = rgb2hsv(vec3(1.0, 0.0, 0.0));
    float originalSaturation = overlayHsv[1];
    vec2 textureSize = vec2(oneTexel.x * scale * 16.0, oneTexel.y * scale * 16.0);
    vec2 coordOffset = vec2(texCoord.x - mod(texCoord.x, oneTexel.x), texCoord.y - mod(texCoord.y, oneTexel.y));

    overlayHsv[0] = mod(overlayHsv[0] + overlayHueOffset + ((coordOffset.x / textureSize.x - coordOffset.y / textureSize.y) / 2.0), 1.0);
    overlayHsv[1] *= Saturation;

    vec3 blendRgb;
    if (Partial == 0)
    {
        blendRgb = spectriteBlend(hsv2rgb(hsv), hsv2rgb(overlayHsv));
    } else
    {
        blendRgb = mix(spectriteBlend(hsv2rgb(hsv), hsv2rgb(overlayHsv)), color.rgb, 0.625);
    }

    if (Charged == 1)
    {
        if (texture(Sampler0, texCoord - vec2(oneTexel.x, 0.0)).a == 0.0
        || texture(Sampler0, texCoord + vec2(oneTexel.x, 0.0)).a == 0.0
        || texture(Sampler0, texCoord - vec2(0.0, oneTexel.y)).a == 0.0
        || texture(Sampler0, texCoord + vec2(0.0, oneTexel.y)).a == 0.0)
        {
            vec3 chargedOverlayHsv = vec3(overlayHsv[0], originalSaturation, overlayHsv[2] * 1.5);
            if (chargedOverlayHsv[2] > 1.0) {
                chargedOverlayHsv[2] = 1.0;
            }
            blendRgb = screen(blendRgb, hsv2rgb(chargedOverlayHsv));
        }
    }

    fragColor = vec4(blendRgb, color.a) * ColorModulator;
}