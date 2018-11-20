#version 150
in vec3 inPosition; // input from the vertex buffer
out vec3 vertColor; // output from this shader to the next pipeline stage
out vec2 textCoord;
out vec3 normal;
out vec4 depthTexCoord;
out vec3 lightDirection;
out vec3 viewDirection;
out float distance;
out vec3 posColor;
uniform float time; // variable constant for all vertices in a single draw
uniform mat4 matView;
uniform mat4 matProj;
uniform mat4 matTrans;
uniform mat4 matLightVP;
uniform vec3 lightPosition;
uniform vec3 eyePos;
uniform float function;
uniform int mode;
const float PI = 3.14159265;
const float DELTA = 0.001;
const float spotCutOff = 45;
vec3 spotDirection = vec3(0, 0, -1);

vec3 getWater(vec2 pos) {
	/*
	 * Water
	 */

	return vec3(pos.x, pos.y, sin(5 * pos.x) * cos(5 * pos.y + time) / 5);

}

vec3 getPiramid(vec2 pos) {
	/*
	 * Piramida
	 */
	return vec3(2 * pos.x, 2 * pos.y,
			1 - abs(pos.x + pos.y) - abs(pos.y - pos.x));
}

vec3 getSphere(vec2 pos) {
	/*
	 * Koule
	 */
	float az = pos.x * PI + time / 4;
	float ze = pos.y * PI / 2;
	float r = 1;

	float x = r * cos(az) * cos(ze);
	float y = r * sin(az) * cos(ze);
	float z = r * sin(ze);
	return vec3(x, y, z);

}

vec3 getSphereRing(vec2 pos) {
	/*
	 * Ring
	 */

	float s = pos.x * PI;
	float t = pos.y * PI - PI / 2;
	vec3 position;
	position.x = 4 * cos(s) + cos(t) * cos(s);
	position.y = 4 * sin(s) + cos(t) * sin(s);
	position.z = sin(t);
	return position / 1.5; // zmenšení

	/*

	 float s = pos.y  * PI;
	 //float t = paramPos.x; spatny rozsah
	 float t = pos.x ;

	 float R = t*s;
	 float z = sqrt(t);

	 return vec3(R * cos(s), R * sin((s)), z);
	 */
}

vec3 getSphereDiabolo(vec2 pos) {
	/*
	 * Diablo
	 */

	float x = pos.x * PI;
	float y = pos.y * PI;
	return vec3(y * cos(x + time) / 4, y * sin(x + time) / 4, y / 4);

	vec3 position;
	position.xy = pos;
}

vec3 getVodniDymka(vec2 pos) {
	/*
	 * Vodní dýmka v cylindrických souřadnicích..
	 */
	float s = pos.y * 2.0 * PI;
	float t = (pos.x * PI * 3.0);
	float r = (1 + max(sin(t * 2) + sin(t / 2) + sin(s / 2), 0)) * 1;
	float theta = s;
	vec3 position;

	position.x = r * cos(theta);
	position.y = r * sin(theta);
	position.z = t;
	return position / 4;
}

vec3 getGlass(vec2 pos) {
	/*
	 * Sklenicka
	 */
	float azimuth = pos.x * PI + time / 20;
	float zenith = pos.y * PI;
	float r = (1 + max(sin(zenith), 0)) * 0.5 * zenith;
	vec3 position;
	position.x = r * sin(azimuth) * 1 / 2;
	position.y = r * cos(azimuth) * 1 / 2;
	position.z = (3 - zenith) * 1 / 2;

	return position;
}

vec3 getSphereWave(vec2 pos) {
	/*
	 * Wave
	 */
	vec3 xyz = vec3(pos, 1);
	xyz.z = sin(3.14 * pos.x + time / 4);
	return xyz;
}

vec3 getNationalFlag(vec2 pos) {
	/*
	 * Státní vlajka
	 */
	vec3 xyz = vec3(pos, 1);
	/*
	 * Aby se na začátku nehýbala
	 */
	if (pos.x >= -0.999) {
		xyz.z = sin(pos.x + time / 4) / 5;
	} else {
		xyz.z = 0;
	}
	xyz.x *= 1.5; // zvětšení v x souřadnici.. (asi ne uplně korektní)
	return xyz;
}

vec3 getFlagstaff(vec2 pos) {
	/*
	 * Stožár pro vlajku
	 */
	float s = pos.y * 2.0 * PI;
	float t = (pos.x * PI * 3.0);
	float r = 1;
	float theta = s;
	vec3 position;

	position.x = r * cos(theta) / 10;
	position.y = r * sin(theta) / 10;
	position.z = t / 2;
	return position;
}

vec3 generatePosition(vec2 pos) {
	vec3 position;

	switch (mode) {
	case 0:
		/*
		 * Grid
		 */
		position = vec3(pos.xy, 1.0);
		break;
	case 1:
		/*
		 * Koule
		 */
		position = getSphere(pos.xy);
		break;
	case 2:
		/*
		 * Ring
		 */
		position = getSphereRing(pos.xy);
		break;
	case 3:
		/*
		 * Diablo
		 */
		position = getSphereDiabolo(pos.xy);
		break;
	case 4:
		/*
		 * Wave
		 */
		position = getSphereWave(pos.xy);
		break;
	case 5:
		/*
		 * Light
		 */
		position = getSphere(pos.xy);
		break;
	case 6:
		/*
		 * Glass
		 */
		position = getGlass(pos.xy);
		break;
	case 7:
		/*
		 * Water
		 */
		position = getWater(pos.xy);
		break;

	case 8:
		/*
		 * Piramid
		 */
		position = getPiramid(pos.xy);
		break;
	case 9:
		/*
		 * Vodní dýmka v cylindrických souřadnicích..
		 */
		position = getVodniDymka(pos.xy);
		posColor = position;
		break;
	case 10:
		/*
		 * Státní vlajka
		 */
		position = getNationalFlag(pos.xy);
		break;
	case 11:
		/*
		 * Stožár vlajky
		 */
		position = getFlagstaff(pos.xy);
	}
	return position;
}

vec3 getNormal(vec2 xy) {

	/*
	 * Normála pro podložku
	 */
	if (mode == 0) {
		return vec3(xy.xy, 1.0);
	}

	vec2 dx = vec2(DELTA, 0), dy = vec2(0, DELTA);
	vec3 u = generatePosition((xy + dx).xy) - generatePosition((xy - dx).xy);
	vec3 v = generatePosition((xy + dy).xy) - generatePosition((xy - dy).xy);

	return cross(u, v);
}

void main() {
	vec4 pos;
	pos.x = inPosition.x * 2 - 1;
	pos.y = inPosition.y * 2 - 1;

	textCoord = inPosition.xy;

	vec3 position = generatePosition(pos.xy);
	normal = getNormal(pos.xy);

	viewDirection = normalize(lightPosition - position);

	gl_Position = matProj * matView * matTrans * vec4(position, 1.0);

	/*
	 * Pro zobrazení pozice barevně
	 */
	if (mode == 4) {
		vertColor = position;
		return;
	}

	depthTexCoord = matLightVP * matTrans * vec4(position, 1.0);
	depthTexCoord.xyz = (depthTexCoord.xyz + 1.) / 2;

	lightDirection = normalize(lightPosition - position);

	vec3 halfVec = normalize(viewDirection + lightDirection);

// -1 Aby světlo mohlo být dále (přiblížení pro osvětlení)
	distance = length(lightDirection) - 1;

	/*
	 * Osvětlení
	 */
	float diffusion = max(dot(normalize(normal), lightDirection), 0.0);
	float specular = max(0.0, dot(normalize(normal), halfVec));
	specular = 6 * pow(specular, 70);
	float ambient = .1;
	vec3 attenuation = vec3(0.9, 0.9, 0.9);
	float att = 1.0
			/ (attenuation.x + attenuation.y * distance
					+ attenuation.z * distance * distance);

	vertColor = att * (min(ambient + diffusion, 1)) + vec3(1, 1, 1) * specular;

	/*
	 * Reflektor
	 */
	if (function == 5) {
		spotDirection = -position;
		float spotEffect = max(
				dot(normalize(spotDirection), normalize(-lightDirection)), 0);
		if (spotEffect > radians(spotCutOff)) {

			vertColor = att * (min(ambient + diffusion, 1))
					+ vec3(1, 1, 1) * specular;

			float blend = clamp(
					(spotEffect - spotCutOff) / (1 - spotCutOff), 0.0, 1.0);

			vertColor = mix(vec3(1,1,1)*ambient,
					vertColor,
					vec3(1,1,1)*blend);
		} else {
			vertColor.xyz = vec3(1, 1, 1) * ambient;
		}
	}
}
