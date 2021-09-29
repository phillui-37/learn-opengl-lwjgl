#version 330 core

out vec4 FragColor;

in vec3 vertColor;
//uniform vec4 globalColor;

void main()
{
    FragColor = vec4(vertColor,1.0);
//    FragColor = globalColor;
}