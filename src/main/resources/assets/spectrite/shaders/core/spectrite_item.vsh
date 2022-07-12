#version 150

in vec3 Position;
in vec2 UV0;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec2 TexSize;

out vec4 vertexColor;
out vec2 texCoord;
out vec2 oneTexel;

void main()
{
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    texCoord = UV0;
    oneTexel = 1.0 / TexSize;
}