#version 400 core

const int lightNum = 4;

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector[lightNum];
in vec3 toCameraVector;
in float visibility;

out vec4 outCol;

uniform vec3 lightColor[lightNum];
uniform vec3 attenuation[lightNum];

uniform sampler2D textureSampler;

uniform float shineDamper;
uniform float reflectivity;

uniform vec3 skyColor;

void main(void){
	
	vec3 unitNormal = normalize(surfaceNormal);
	
	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);
	
	vec3 unitCameraVector = normalize(toCameraVector);
	
	for(int i = 0; i < lightNum; i++){
		float distance = length(toLightVector[i]);
		float attenFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
		
		vec3 unitLightVector = normalize(toLightVector[i]);
		float nDotl = dot(unitNormal, unitLightVector);
		float brightness = max(nDotl, 0.0);
		
		vec3 lightDirection = -unitLightVector;
		vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
		
		float specularFactor = dot(unitCameraVector, reflectedLightDirection);
		specularFactor = max(specularFactor, 0.0);
		
		float dampedFactor = pow(specularFactor, shineDamper);
		
		totalDiffuse = totalDiffuse + (brightness * lightColor[i]) / attenFactor;
		totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColor[i]) / attenFactor;
	}
	
	totalDiffuse = max(totalDiffuse, 0.15);
	
	vec4 textureColor = texture(textureSampler, pass_textureCoords);
	if(textureColor.a < 0.5){
		discard;
	}

	outCol = vec4(totalDiffuse, 1.0) * textureColor + vec4(totalSpecular, 1.0);
	outCol = mix(vec4(skyColor, 1.0), outCol, visibility);
}