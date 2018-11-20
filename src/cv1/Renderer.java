package cv1;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import oglutils.OGLBuffers;
import oglutils.OGLRenderTarget;
import oglutils.OGLTextRenderer;
import oglutils.OGLTexture2D;
import oglutils.OGLUtils;
import oglutils.ShaderUtils;
import oglutils.ToFloatArray;
import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4OrthoRH;
import transforms.Mat4PerspRH;
import transforms.Mat4RotXYZ;
import transforms.Mat4Scale;
import transforms.Mat4Transl;
import transforms.Vec3D;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/**
 * 
 * @author Luboš Hájek PGRF FIM UHK
 * @version 1.0
 * @since 2018-11-18
 */
public class Renderer implements GLEventListener, MouseListener, MouseMotionListener, KeyListener {

	private int width, height, ox, oy, mouseButton, locFunction, function = 0, shaderProgram, locTime, locMatView,
			locMatProj, locTrans, shaderProgramLight, mode, locLightPos, locEye, locLightViewVector;
	private boolean lightcamPerson = false, camFirstPerson = false, tenTrianglesMode = true, lightMove = true,
			ortoPresp = false;
	private double lightX = 0, lightY = 0, lightZ = 20;
	private OGLRenderTarget renderTarget;
	private OGLBuffers buffers, buffers10, buffers200;
	private OGLTextRenderer textRenderer;
	private OGLTexture2D texture, texture2;
	private OGLTexture2D.Viewer textureViewer;
	private float time = 0, timeforLight;
	private Camera cam = new Camera();
	private Camera camForLight = new Camera().withPosition(new Vec3D(3, 2, 1)).withAzimuth(3.3104532587202464)
			.withZenith(-0.8560839981032209).withFirstPerson(lightcamPerson).withRadius(5);

	private Mat4 proj;
	private Mat4 lightMatProj = new Mat4OrthoRH(30, 30, 0.1, 500);
	private Mat4 lightView = camForLight.getViewMatrix();
	private Mat4 tranForGrif = new Mat4Transl(0, 0, -2).mul(new Mat4Scale(15, 15, 1));
	private Mat4Transl tranForKoule = new Mat4Transl(0, -2, 0);
	private Mat4Transl tranForRing = new Mat4Transl(0, 2, 0);
	private Mat4Transl tranForWave = new Mat4Transl(0, 18, 0);
	private Mat4 tranForDiabolo = new Mat4Transl(-5, 18, 0);
	private Mat4 tranForLighPos = new Mat4Transl(lightX, lightY, lightZ).mul(new Mat4Scale(0.3, 0.3, 0.3));
	private Mat4 tranForGlass = new Mat4Transl(10, 10, 0);
	private Mat4 tranForWater = new Mat4Transl(5, 18, 0);
	private Mat4 tranForPiramid = new Mat4Transl(10, 18, 0);
	private Mat4 tranForDr = new Mat4Transl(-10, 18, 0);
	private Mat4 tranForFlag = new Mat4RotXYZ(1.6, 0, 1).mul(new Mat4Transl(-14, 19, 5));
	private Mat4 tranForFlagstuff = new Mat4Transl(-14.85, 17.65, 1.7);

	private Vec3D lightpos = new Vec3D(lightX, lightY, lightZ);

	@Override
	public void init(GLAutoDrawable glDrawable) {

		// check whether shaders are supported
		GL2GL3 gl = glDrawable.getGL().getGL2GL3();
		OGLUtils.shaderCheck(gl);
		OGLUtils.printOGLparameters(gl);
		textRenderer = new OGLTextRenderer(gl, glDrawable.getSurfaceWidth(), glDrawable.getSurfaceHeight());
		cam = cam.withPosition(new Vec3D(20, 20, 20)).withAzimuth(Math.PI * 1.25).withZenith(Math.PI * -0.25)
				.withFirstPerson(false).withRadius(7);
		texture = new OGLTexture2D(gl, "/textures/bricks.jpg");
		texture2 = new OGLTexture2D(gl, "/textures/mosaic.jpg");

		shaderProgram = ShaderUtils.loadProgram(gl, "/cv1/start.vert", "/cv1/start.frag", null, null, null, null);
		shaderProgramLight = ShaderUtils.loadProgram(gl, "/cv1/light.vert", "/cv1/light.frag", null, null, null, null);
		locMatView = gl.glGetUniformLocation(shaderProgram, "matView");
		locMatProj = gl.glGetUniformLocation(shaderProgram, "matProj");
		locFunction = gl.glGetUniformLocation(shaderProgram, "function");
		locTrans = gl.glGetUniformLocation(shaderProgram, "matTrans");
		locTime = gl.glGetUniformLocation(shaderProgram, "time");
		mode = gl.glGetUniformLocation(shaderProgram, "mode");
		mode = gl.glGetUniformLocation(shaderProgramLight, "mode");
		locLightPos = gl.glGetUniformLocation(shaderProgram, "lightPos");
		locEye = gl.glGetUniformLocation(shaderProgram, "eyePos");

		buffers200 = GridFactory.createGridStrip(gl, 120, 120);
		buffers10 = GridFactory.createGridStrip(gl, 10, 10);
		buffers = buffers200;

		gl.glEnable(GL2GL3.GL_DEPTH_TEST);

		renderTarget = new OGLRenderTarget(gl, 1024, 1024);
		textureViewer = new OGLTexture2D.Viewer(gl);
	}

	public void renderFromViewer(GL2GL3 gl) {

		gl.glUseProgram(shaderProgram);
		gl.glBindFramebuffer(GL2GL3.GL_FRAMEBUFFER, 0);
		gl.glViewport(0, 0, width, height);
		gl.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);
		gl.glClear(GL2GL3.GL_COLOR_BUFFER_BIT | GL2GL3.GL_DEPTH_BUFFER_BIT);

		mode = gl.glGetUniformLocation(shaderProgram, "mode");
		locMatView = gl.glGetUniformLocation(shaderProgram, "matView");
		locMatProj = gl.glGetUniformLocation(shaderProgram, "matProj");
		locFunction = gl.glGetUniformLocation(shaderProgram, "function");
		locTrans = gl.glGetUniformLocation(shaderProgram, "matTrans");
		locLightPos = gl.glGetUniformLocation(shaderProgram, "lightPosition");
		locEye = gl.glGetUniformLocation(shaderProgram, "eyePos");
		locLightViewVector = gl.glGetUniformLocation(shaderProgram, "shadowLightViewVector");
		gl.glPolygonMode(GL2GL3.GL_FRONT, GL2GL3.GL_FILL);
		int locLightVP = gl.glGetUniformLocation(shaderProgram, "matLightVP");
		time += 0.2;

		gl.glUniformMatrix4fv(locLightVP, 1, false, ToFloatArray.convert(lightView.mul(lightMatProj)), 0);
		gl.glUniformMatrix4fv(locMatProj, 1, false, ToFloatArray.convert(proj), 0);
		gl.glUniformMatrix4fv(locMatView, 1, false, ToFloatArray.convert(cam.getViewMatrix()), 0);
		gl.glUniformMatrix3fv(locEye, 1, false, ToFloatArray.convert(cam.getEye()), 0);

		gl.glUniform1f(locFunction, function);
		gl.glUniform3f(locLightPos, (float) lightpos.getX(), (float) lightpos.getY(), (float) lightpos.getZ());
		gl.glUniform3f(locLightViewVector, (float) camForLight.getViewVector().getX(),
				(float) camForLight.getViewVector().getY(), (float) camForLight.getViewVector().getZ());
		gl.glUniform1f(locTime, time); // correct shader must be set before this
		gl.glUniformMatrix4fv(locTrans, 1, false, ToFloatArray.convert(tranForKoule), 0);
		texture.bind(shaderProgram, "textureID", 0);
		texture2.bind(shaderProgram, "textureID2", 2);

		renderTarget.getDepthTexture().bind(shaderProgram, "textureDepth", 1);

		if (function <= 2) {
			gl.glUniformMatrix4fv(locTrans, 1, false, ToFloatArray.convert(tranForKoule), 0);
			// Koule
			gl.glUniform1i(mode, 1);
			buffers.draw(GL4.GL_TRIANGLE_STRIP, shaderProgram);

			gl.glUniformMatrix4fv(locTrans, 1, false, ToFloatArray.convert(tranForRing), 0);
			// Prsten
			gl.glUniform1i(mode, 2);
			buffers.draw(GL4.GL_TRIANGLE_STRIP, shaderProgram);
			// Sklenièka
			gl.glUniformMatrix4fv(locTrans, 1, false, ToFloatArray.convert(tranForGlass), 0);
			gl.glUniform1i(mode, 6);
			buffers.draw(GL4.GL_TRIANGLE_STRIP, shaderProgram);
			gl.glUniform1f(locTime, time);
		}
		gl.glUniformMatrix4fv(locTrans, 1, false, ToFloatArray.convert(tranForDiabolo), 0);
		// Diabola
		gl.glUniform1i(mode, 3);
		buffers.draw(GL4.GL_TRIANGLE_STRIP, shaderProgram);

		gl.glUniformMatrix4fv(locTrans, 1, false, ToFloatArray.convert(tranForWave), 0);
		// Vlna
		gl.glUniform1i(mode, 4);
		buffers.draw(GL4.GL_TRIANGLE_STRIP, shaderProgram);

		// Voda
		gl.glUniformMatrix4fv(locTrans, 1, false, ToFloatArray.convert(tranForWater), 0);
		gl.glUniform1i(mode, 7);
		buffers.draw(GL4.GL_TRIANGLE_STRIP, shaderProgram);
		gl.glUniform1f(locTime, time);

		// Piramida
		gl.glUniformMatrix4fv(locTrans, 1, false, ToFloatArray.convert(tranForPiramid), 0);
		gl.glUniform1i(mode, 8);
		buffers.draw(GL4.GL_TRIANGLE_STRIP, shaderProgram);
		gl.glUniform1f(locTime, time);

		// Vodní dýmka
		gl.glUniformMatrix4fv(locTrans, 1, false, ToFloatArray.convert(tranForDr), 0);
		gl.glUniform1i(mode, 9);
		buffers.draw(GL4.GL_TRIANGLE_STRIP, shaderProgram);
		gl.glUniform1f(locTime, time);

		// Státní vlajka
		gl.glUniformMatrix4fv(locTrans, 1, false, ToFloatArray.convert(tranForFlag), 0);
		gl.glUniform1i(mode, 10);
		buffers.draw(GL4.GL_TRIANGLE_STRIP, shaderProgram);
		gl.glUniform1f(locTime, time);

		// Stožár vlajky
		gl.glUniformMatrix4fv(locTrans, 1, false, ToFloatArray.convert(tranForFlagstuff), 0);
		gl.glUniform1i(mode, 11);
		buffers.draw(GL4.GL_TRIANGLE_STRIP, shaderProgram);
		gl.glUniform1f(locTime, time);

		if (function != 0) {
			// zobrazení pozice svìtla
			gl.glUniformMatrix4fv(locTrans, 1, false, ToFloatArray.convert(tranForLighPos), 0);
			gl.glUniform1i(mode, 5);
			buffers.draw(GL4.GL_TRIANGLE_STRIP, shaderProgram);
		}
		// Grid
		gl.glUniformMatrix4fv(locTrans, 1, false, ToFloatArray.convert(tranForGrif), 0);
		gl.glUniform1i(mode, 0);
		buffers.draw(GL4.GL_TRIANGLE_STRIP, shaderProgram);
	}

	public void renderFromLight(GL2GL3 gl) {

		gl.glUseProgram(shaderProgramLight);
		gl.glCullFace(GL.GL_FRONT);
		lightView = camForLight.getViewMatrix();
		renderTarget.bind();
		gl.glClearColor(0.4f, 0.0f, 0.0f, 1.0f);
		gl.glClear(GL2GL3.GL_COLOR_BUFFER_BIT | GL2GL3.GL_DEPTH_BUFFER_BIT);

		mode = gl.glGetUniformLocation(shaderProgramLight, "mode");
		locMatView = gl.glGetUniformLocation(shaderProgramLight, "matView");
		locMatProj = gl.glGetUniformLocation(shaderProgramLight, "matProj");
		locFunction = gl.glGetUniformLocation(shaderProgramLight, "function");
		locTrans = gl.glGetUniformLocation(shaderProgramLight, "matTrans");

		gl.glPolygonMode(GL2GL3.GL_FRONT, GL2GL3.GL_FILL);

		gl.glUniformMatrix4fv(locMatProj, 1, false, ToFloatArray.convert(lightMatProj), 0);
		gl.glUniformMatrix4fv(locMatView, 1, false, ToFloatArray.convert(lightView), 0);
		gl.glUniformMatrix4fv(locTrans, 1, false, ToFloatArray.convert(tranForKoule), 0);

		gl.glUniform1f(locFunction, function);
		gl.glUniform1f(locTime, time); // correct shader must be set before this

		texture.bind(shaderProgramLight, "textureID", 0);

		// render Koule
		gl.glUniformMatrix4fv(locTrans, 1, false, ToFloatArray.convert(tranForKoule), 0);
		gl.glUniform1i(mode, 1);
		buffers.draw(GL4.GL_TRIANGLE_STRIP, shaderProgramLight);
		gl.glUniform1f(locTime, time);

		// render prsten
		gl.glUniformMatrix4fv(locTrans, 1, false, ToFloatArray.convert(tranForRing), 0);
		gl.glUniform1i(mode, 2);
		buffers.draw(GL4.GL_TRIANGLE_STRIP, shaderProgramLight);
		gl.glUniform1f(locTime, time);

		// Diablo
		gl.glUniformMatrix4fv(locTrans, 1, false, ToFloatArray.convert(tranForDiabolo), 0);
		gl.glUniform1i(mode, 3);
		buffers.draw(GL4.GL_TRIANGLE_STRIP, shaderProgramLight);
		gl.glUniform1f(locTime, time);

		// Sklenièka
		gl.glUniformMatrix4fv(locTrans, 1, false, ToFloatArray.convert(tranForGlass), 0);
		gl.glUniform1i(mode, 6);
		buffers.draw(GL4.GL_TRIANGLE_STRIP, shaderProgramLight);
		gl.glUniform1f(locTime, time);

		// Grid
		gl.glUniformMatrix4fv(locTrans, 1, false, ToFloatArray.convert(tranForGrif), 0);
		gl.glUniform1i(mode, 0);
		buffers.draw(GL4.GL_TRIANGLE_STRIP, shaderProgramLight);
	}

	@Override
	public void display(GLAutoDrawable glDrawable) {
		GL2GL3 gl = glDrawable.getGL().getGL2GL3();
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glClear(GL2GL3.GL_COLOR_BUFFER_BIT | GL2GL3.GL_DEPTH_BUFFER_BIT);
		gl.glCullFace(GL.GL_BACK);

		// Pohyb prstenu
		tranForRing = new Mat4Transl(1, 6, Math.cos(time / 2) + 2);
		/*
		 * Zobrazeni
		 */
		if (ortoPresp) {
			proj = new Mat4OrthoRH(50, 50, 0.1, 100);
		} else {
			proj = new Mat4PerspRH(Math.PI / 4, height / (double) width, 1.0, 100.0);
		}

		/*
		 * Pohyb Svìtla
		 */
		if (lightMove) {
			timeforLight += 0.02;
			lightX = 50 * Math.cos(timeforLight);
			lightY = 50 * Math.sin(timeforLight);
		}
		tranForLighPos = new Mat4Transl(lightX, lightY, lightZ).mul(new Mat4Scale(0.3, 0.3, 0.3));
		lightpos = new Vec3D(lightX, lightY, lightZ);
		camForLight = camForLight.withFirstPerson(lightcamPerson);
		cam = cam.withFirstPerson(camFirstPerson);
		if (function == 0) {
			renderFromLight(gl);
		}
		renderFromViewer(gl);

		if (function == 0) {
			textureViewer.view(texture, -1, -1, 0.5);
			textureViewer.view(renderTarget.getColorTexture(), -1, -0.5, 0.5);
			textureViewer.view(renderTarget.getDepthTexture(), -1, 0, 0.5);
		}

		makeDescriptionText();

	}

	private void makeDescriptionText() {
		String textMode = " ";

		if (function == 2) {
			textMode = "Per-Vertex";
		} else if (function == 0) {
			textMode = "Shadows";
		} else if (function == 1) {
			textMode = "Per-Pixel";
		} else if (function == 3) {
			textMode = "Reflektor per-pixel";
		} else if (function == 4) {
			textMode = "Reflektor per-vertex";
		}

		textRenderer.drawStr2D(width - 120, height - 50, textMode);
		String text = "Luboš Hájek | PGRF3 2018/19";
		textRenderer.drawStr2D(3, height - 20, text);

		textRenderer.drawStr2D(3, height - 35,
				"Pohyb pozorovatele: WASD + myš (nebo stlaèení koleèka a tažení myší L/P)");
		textRenderer.drawStr2D(3, height - 50, "Pohyb svìtla u stínù: Pravím tlaèítkem myši");
		textRenderer.drawStr2D(3, height - 65, "Pohyb svìtla (když stojí): IJKL");
		textRenderer.drawStr2D(3, height - 80, "Zastavení svìtla: M");
		textRenderer.drawStr2D(3, height - 95, "Pøepímání módù svìtla/stíný: Enter");
		textRenderer.drawStr2D(3, height - 110, "Pøepímání zobrazení Presp/Orth: P");
		textRenderer.drawStr2D(3, height - 125, "Znázornìní rozdíl mezi per-pixel a per vertex (velikost gridu): Q");
		textRenderer.drawStr2D(3, height - 140, "Osoba kamery: Mezerník");

		textRenderer.drawStr2D(width - 90, 3, " (c) PGRF UHK");
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		this.width = width;
		this.height = height;
		proj = new Mat4PerspRH(Math.PI / 4, height / (double) width, 1.0, 100.0);
		lightMatProj = new Mat4OrthoRH(30, 30, 0.1, 500);
		textRenderer.updateSize(width, height);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseButton = e.getButton();
		ox = e.getX();
		oy = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (mouseButton == MouseEvent.BUTTON1) {
			cam = cam.addAzimuth((double) Math.PI * (ox - e.getX()) / width)
					.addZenith((double) Math.PI * (e.getY() - oy) / width).withFirstPerson(camFirstPerson)
					.withRadius(7);
		} else if (mouseButton == MouseEvent.BUTTON3 && function == 0) {
			camForLight = camForLight.addAzimuth((double) Math.PI * (ox - e.getX()) / width)
					.addZenith((double) Math.PI * (e.getY() - oy) / width).withFirstPerson(lightcamPerson)
					.withRadius(5);

		} else if (mouseButton == MouseEvent.BUTTON2) {

			if (ox < e.getX())
				cam = cam.forward(0.5);
			else
				cam = cam.backward(0.5);
		}
		ox = e.getX();
		oy = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_W:
			cam = cam.forward(1);
			break;
		case KeyEvent.VK_D:
			cam = cam.right(1);
			break;
		case KeyEvent.VK_S:
			cam = cam.backward(1);
			break;
		case KeyEvent.VK_A:
			cam = cam.left(1);
			break;
		case KeyEvent.VK_CONTROL:
			cam = cam.down(1);
			break;
		case KeyEvent.VK_SHIFT:
			cam = cam.up(1);
			break;
		case KeyEvent.VK_SPACE:
			camFirstPerson = !camFirstPerson;
			break;
		case KeyEvent.VK_R:
			lightcamPerson = !lightcamPerson;
			break;
		case KeyEvent.VK_F:
			cam = cam.mulRadius(1.1f);
			break;
		case KeyEvent.VK_M:
			lightMove = !lightMove;
			break;
		case KeyEvent.VK_Q:
			if (tenTrianglesMode) {
				buffers = buffers10;
				tenTrianglesMode = !tenTrianglesMode;
			} else {
				buffers = buffers200;
				tenTrianglesMode = !tenTrianglesMode;
			}
			break;
		case KeyEvent.VK_I:
			if (function > 0) {
				lightX--;
			}
			break;
		case KeyEvent.VK_L:
			if (function > 0) {
				lightY++;
			}
			break;
		case KeyEvent.VK_K:
			if (function > 0) {
				lightX++;
			}
			break;
		case KeyEvent.VK_J:
			if (function > 0) {
				lightY--;
			}
			break;
		case KeyEvent.VK_P:
			ortoPresp = !ortoPresp;
			break;
		case KeyEvent.VK_ENTER:
			function++;
			if (function > 4) {
				function = 0;
				break;
			}
			if (function >= 3) {
				lightX = 0;
				lightY = 0;
			}

			lightMove = function < 3;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void dispose(GLAutoDrawable glDrawable) {
		GL2GL3 gl = glDrawable.getGL().getGL2GL3();
		gl.glDeleteProgram(shaderProgram);
	}

}