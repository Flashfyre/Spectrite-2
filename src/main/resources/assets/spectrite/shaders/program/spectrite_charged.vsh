#version 150

in vec4 Position;

uniform mat4 ProjMat;
uniform vec2 InSize;
uniform vec2 OutSize;

out vec2 texCoord;
out vec2 vPosition;
out vec2 oneTexel;

void main()
{
    vec4 outPos = ProjMat * vec4(Position.xy, 0.0, 1.0);
    gl_Position = vec4(outPos.xy, 0.2, 1.0);
    vPosition = outPos.xy;

    oneTexel = 1.0 / InSize;

    texCoord = Position.xy / OutSize;
}
