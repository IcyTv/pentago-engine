#version 400 core

const int lightNum = 4;

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector[lightNum];
out vec3 toCameraVector;
out float visibility;

uniform vec3 lightPos[lightNum];

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform float useFakeLighting;

uniform float density;
uniform float gradient;

uniform float numOfRows;
uniform vec2 offset;

uniform vec4 plane;

void main(void){

	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);

	gl_ClipDistance[0] = dot(worldPosition, plane);

	vec4 posRelativeToCam = viewMatrix * worldPosition;

	gl_Position = projectionMatrix * posRelativeToCam;
	pass_textureCoords = (textureCoords / numOfRows) + offset;

	vec3 actualNormal = normal;
	if(useFakeLighting > 0.0){
		actualNormal = vec3(0.0, 1.0, 0.0);
	}

	surfaceNormal = (transformationMatrix * vec4(actualNormal, 0.0)).xyz;
	for(int i = 0; i < lightNum; i++){
		toLightVector[i] = lightPos[i] - worldPosition.xyz;
	}

	toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;

	float distance = length(posRelativeToCam.xyz);
	visibility = exp(-pow(distance*density, gradient));
	visibility = clamp(visibility, 0.0, 1.0);
}
