#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float STime;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec3 relPos;
in vec4 normal;

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

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;

    vec3 hsv = rgb2hsv(color.rgb);
    float overlayHueOffset = STime * 0.3;
    hsv[0] = mod(hsv[0] + overlayHueOffset, 1.0);

    vec3 overlayHsv = rgb2hsv(vec3(1.0, 0.0, 0.0));
    vec2 coordOffset = vec2(relPos.x, relPos.y);

    overlayHsv[0] = mod(overlayHsv[0] + overlayHueOffset + ((coordOffset.x / 16.0 - coordOffset.y / 16.0)), 1.0);

    vec3 blendRgb = mix(spectriteBlend(hsv2rgb(hsv), hsv2rgb(overlayHsv)), color.rgb, 0.625);

    color = vec4(blendRgb, color.a) * ColorModulator;

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
