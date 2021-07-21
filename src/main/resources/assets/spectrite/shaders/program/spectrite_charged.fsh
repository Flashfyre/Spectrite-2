#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D Sampler;
uniform float STime;

in vec2 texCoord;
in vec2 vPosition;
in vec2 oneTexel;

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

vec3 screen(vec3 a, vec3 b)
{
    vec3 r = 1.0 - (1.0 - a) * (1.0 - b);
    return r;
}

void main()
{
    vec4 center = texture(DiffuseSampler, texCoord);
    vec4 left = texture(DiffuseSampler, texCoord - vec2(oneTexel.x * 2.0, 0.0));
    vec4 right = texture(DiffuseSampler, texCoord + vec2(oneTexel.x * 2.0, 0.0));
    vec4 up = texture(DiffuseSampler, texCoord - vec2(0.0, oneTexel.y * 2.0));
    vec4 down = texture(DiffuseSampler, texCoord + vec2(0.0, oneTexel.y * 2.0));
    float leftDiff  = abs(center.a - left.a);
    float rightDiff = abs(center.a - right.a);
    float upDiff    = abs(center.a - up.a);
    float downDiff  = abs(center.a - down.a);
    float total = clamp(leftDiff + rightDiff + upDiff + downDiff, 0.0, 1.0);
    if (center.a > 0.0 && total == 0.0) {
        fragColor = center;
    } else
    {
        vec3 color = center.rgb * center.a + left.rgb * left.a + right.rgb * right.a + up.rgb * up.a + down.rgb * down.a;
        vec4 outColor = vec4(color * 0.2, total);
        if (total > 0.0) {
            float overlayHueOffset = STime * 0.3;
            vec3 overlayHsv = rgb2hsv(vec3(1.0, 0.0, 0.0));
            overlayHsv[0] = mod(overlayHsv[0] + overlayHueOffset + (((vPosition.x + vPosition.y) + 2.0) / 2.0) * 7.0, 1.0);
            overlayHsv[2] *= 1.5;
            if (overlayHsv[2] > 1.0) {
                overlayHsv[2] = 1.0;
            }
            outColor = vec4(screen(outColor.rgb, hsv2rgb(overlayHsv)), outColor.a);
        }
        fragColor = mix(texture(Sampler, texCoord), outColor, total);
    }
}
