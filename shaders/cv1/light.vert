#version 150
in vec3 inPosition; // input from the vertex buffer
uniform float time; // variable constant for all vertices in a single draw
uniform mat4 matView;
uniform mat4 matProj;
uniform mat4 matTrans;
uniform int mode;
const float PI = 3.14159265;

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

}

vec3 getGlass(vec2 pos) {
	/*
	 * Sklenicka
	 */
	float azimuth = pos.x * PI;
	float zenith = pos.y * PI;
	float r = (1 + max(sin(zenith), 0)) * 0.5 * zenith;
	vec3 position;
	position.x = r * sin(azimuth) * 1 / 2;
	position.y = r * cos(azimuth) * 1 / 2;
	position.z = (3 - zenith) * 1 / 2;

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
	case 4:
		/*
		 * Wave
		 * Vlna a další se nestínuje jelikož na ní je zobrazena barva podle pozice
		 */
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
	}
	return position;
}

void main() {
	vec4 pos;
	pos.x = inPosition.x * 2 - 1;
	pos.y = inPosition.y * 2 - 1;

	vec3 position = generatePosition(pos.xy);
	gl_Position = matProj * matView * matTrans * vec4(position, 1.0);
}
