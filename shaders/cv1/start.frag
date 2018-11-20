#version 150
in vec3 vertColor; // input from the previous pipeline stage
in vec2 textCoord;
in vec3 normal;
in vec4 depthTexCoord;
in vec3 lightDirection;
in vec3 viewDirection;
in vec3 posColor;
in float distance;
out vec4 outColor; // output from the fragment shader
uniform sampler2D textureID;
uniform sampler2D textureID2;
uniform sampler2D textureDepth;
uniform vec3 lightPosition;
uniform float function;
uniform vec3 shadowLightViewVector;
uniform int mode;

void main() {
	vec4 baseColor;

	switch (mode) {
	case 3:
		/*
		 * Vykresleni Diabola
		 */
		outColor = vec4(texture(textureID, textCoord).xyz, 1.0);
		return;
	case 5:
		/*
		 * Vykresleni svetla pøi osvìtlení
		 */
		outColor = vec4(1.0, 0.9, 0.0, 1.0);
		return;
	case 4:
		/*
		 * Zobrazeni pozice barevne (wave)
		 */
		outColor = vec4(normalize(vertColor), 1.0);
		return;
	case 7:
		/*
		 * Zobrazeni normaly barevne (water)
		 */
		outColor = vec4(normalize(normal), 1.0);
		return;
	case 8:
		/*
		 * Zobrazeni souradnic do textury barevne (piramid)
		 */
		outColor = vec4(textCoord, 1.0, 1.0);
		return;
	case 9:
		/*
		 * Zobrazeni z-tové souøadnice (vodní dýmka)
		 */
		outColor = vec4(normalize(posColor).zzz, 1.0);
		return;
	case 10:
		/*
		 * Státní vlajka
		 */

		/*
		 * Pøepoèet pro lehèí výpoèet modrého klímu
		 */
		vec2 helper;
		helper.x = 2 * textCoord.x - 1;
		helper.y = 2 * textCoord.y - 1;

		/*
		 * èervený a bílý pruh
		 */
		if (helper.y < 0) {
			outColor = vec4(1.0, 0.0, 0.0, 1.0);
		} else {
			outColor = vec4(1.0, 1.0, 1.0, 1.0);
		}

		/*
		 * Modrý "klín"
		 */
		if (helper.x < helper.y && abs(helper.x) > helper.y && helper.x < 0) {
			outColor = vec4(0.0, 0.0, 1.0, 1.0);
		}
		return;
	case 11:
		/*
		 * Stožár vlajky
		 */
		outColor = vec4(0.6, 0.2, 0.1, 1.0);
		return;
	}

	/*
	 * Støídání textur
	 */
	if (mode == 0) {
		baseColor = vec4(texture(textureID, textCoord).xyz, 1.0);
	} else {
		baseColor = vec4(texture(textureID2, textCoord).xyz, 1.0);
	}

	if (function == 0) {
		/*
		 * Stíny
		 */
		float z1 = texture(textureDepth, depthTexCoord.xy / depthTexCoord.w).r;

		float z2 = depthTexCoord.z / depthTexCoord.w;

		/*
		 * Stín pro zadní strany a vyhlazení hran (cos L < 0)
		 */
		float backSide = dot(normal, -shadowLightViewVector);
		/**
		 * Pro podložku vynechat.. ta bude osvìtlená celá
		 */
		if (mode == 0) {
			backSide = 1;
		}
		float bias = 0.00001;
		bool shadow = (z1 < (z2 - bias) || backSide < 0 || gl_FrontFacing); // Ve stínu || zadní stìna || stìna uvnitø

		if (shadow) {
			if (mode == 0) {
				/*
				 * Støídání textur
				 */
				outColor.xyz = texture(textureID, textCoord).xyz / 2;
			} else {
				outColor.xyz = texture(textureID2, textCoord).xyz / 2;
			}

		} else {
			if (mode == 0) {
				outColor.xyz = texture(textureID, textCoord).xyz;
			} else {
				outColor.xyz = texture(textureID2, textCoord).xyz;
			}
		}

	} else if (function == 1 || function == 3) {
		/*
		 * Per pixel
		 */

		vec3 attenuation = vec3(0.3, 0.3, 0.3);
		vec3 ld = normalize(lightDirection);
		vec3 nd = normalize(normal);

		vec3 halfVector = normalize(lightDirection + lightDirection);

		vec4 totalAmbient = vec4(.1) * baseColor;

		float NDotH = max(0.0, dot(nd, halfVector));
		float NDotL = max(dot(nd, ld), 0.0);
		vec4 totalDiffuse = vec4(0.3) * NDotL * baseColor;
		float totalSpecular = max(0, dot(nd, halfVector));
		totalSpecular = pow(NDotH, 70);
		float att = 1.0
				/ (attenuation.x + attenuation.y * distance
						+ attenuation.z * distance * distance);

		outColor = totalAmbient + att * (min(totalDiffuse + totalSpecular, 1));

		/*
		 * Reflektor
		 */
		float spotCutOff = 45;
		if (function == 4) {
			vec3 spotDirection = vec3(0.0, 0.0, -1.0);
			float spotEffect = max(
					dot(normalize(spotDirection), normalize(-lightDirection)),
					0);
			if (spotEffect > radians(spotCutOff)) {

				float blend = clamp(
						(spotEffect - spotCutOff) / (1 - spotCutOff), 0.0, 1.0); //orezani na rozsah <0;1>
				outColor = mix(totalAmbient,
						totalAmbient + att * (totalDiffuse + totalSpecular),
						blend);

			} else {
				outColor = totalAmbient;
			}
		}
		/*
		 * Svìtlo zadní strana
		 */
		float backSide = dot(normal, lightDirection);
		if (backSide < 0 || gl_FrontFacing) {
			outColor = totalAmbient;
		}

		return;
	} else if (function >= 2) {
		/*
		 * Per verex
		 */
		if (mode == 0) {
			outColor.xyz = texture(textureID, textCoord).xyz * vertColor.xyz;
		} else {
			outColor.xyz = texture(textureID2, textCoord).xyz * vertColor.xyz;
		}
	}
}
