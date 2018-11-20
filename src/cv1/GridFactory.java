package cv1;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL2GL3;

import oglutils.OGLBuffers;
import oglutils.ToIntArray;

public class GridFactory {
	static float[] vertex;

	public static OGLBuffers createGrid(GL2GL3 gl, int width, int height) {
		vertex = new float[width * height * 2];
		int tmp = 0;
		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				vertex[tmp++] = (float) (1. / (width - 1) * k);
				vertex[tmp] = (float) (1. / (height - 1) * i);
				tmp++;
			}
		}
		List<Integer> indices = new ArrayList<>();
		for (int i = 0; i < width - 1; i++) {
			for (int j = 0; j < height - 1; j++) {
				indices.add(i * height + j);
				indices.add(i * height + j + 1);
				indices.add(i * height + j + height);

				indices.add(i * height + j + 1);
				indices.add(i * height + height + j + 1);
				indices.add(i * height + j + height);
			}
		}

		OGLBuffers.Attrib[] attributes = { new OGLBuffers.Attrib("inPosition", 2), // 2 floats
		};
		return new OGLBuffers(gl, vertex, attributes, ToIntArray.convert(indices));
	}

	public static OGLBuffers createGridStrip(GL2GL3 gl, int m, int n) {
		//all days work ** after few days - only god know what It This is doing.. :-|
		int vertices = m * n;
		int triangles_strip = 2 * (n - 1) * m;
		float[] vb = new float[vertices * 2];
		List<Integer> ib = new ArrayList<>(triangles_strip + n * 2 - 2);
		int index = 0;

		for (int j = 0; j < n; j++) {
			for (int i = 0; i < m; i++) {
				vb[index] = i / (float) (m - 1);
				vb[index + 1] = j / (float) (n - 1);
				index += 2;
			}
		}
		int height = n;
		int width = m;
		for (int i = 0; i < height - 1; i++) {
			if (i % 2 == 0) {
				for (int j = 0; j < width; j++) { 
					ib.add(i * width + j);
					ib.add((i + 1) * width + j);
					if (j + 1 == width) {
						ib.add((i + 1) * width + j);
						ib.add((i + 1) * width + j);
					}
				}
			} else {
				for (int j = width - 1; j >= 0; j--) { 
					ib.add((i + 1) * width + j);
					ib.add((i) * width + j);
					if (j - 1 < 0) {
						ib.add((i + 1) * width + j);
						ib.add((i + 1) * width + j);
					}
				}
			}
		}
		OGLBuffers.Attrib[] attributes = { new OGLBuffers.Attrib("inPosition", 2),
		};
		OGLBuffers buffers = new OGLBuffers(gl, vb, attributes, ToIntArray.convert(ib));
		return buffers;
	}

}
