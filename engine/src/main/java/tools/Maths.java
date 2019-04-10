package tools;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import core.entities.Camera;

public class Maths {

	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.identity();
		matrix = translate(matrix, translation);

		matrix.scale(new Vector3f(scale.x, scale.y, 1f));
		return matrix;
	}

	private static Matrix4f translate(Matrix4f src, Vector2f vec) {
		Matrix4f dest = new Matrix4f();

		dest.m30(dest.m30() + src.m00() * vec.x + src.m10() * vec.y);
		dest.m31(dest.m31() + src.m01() * vec.x + src.m11() * vec.y);
		dest.m32(dest.m32() + src.m02() * vec.x + src.m12() * vec.y);
		dest.m33(dest.m33() + src.m03() * vec.x + src.m13() * vec.y);

		return dest;

	}

	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.identity();
		matrix.translate(translation, matrix);
		matrix.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0), matrix);
		matrix.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0), matrix);
		matrix.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1), matrix);
		matrix.scale(new Vector3f(scale, scale, scale), matrix);
		return matrix;
	}

	// public static Matrix4f createTransformationMatrix(Vector2f translation,
	// Vector2f scale) {
	// Matrix4f matrix = new Matrix4f();
	// matrix.identity();
	// matrix.translate(translation, matrix);
	// matrix.scale(new Vector3f(scale.x, scale.y, 1f), matrix);
	// return matrix;
	// }

	public static Matrix4f createViewMatrix(Camera camera) {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.identity();
		viewMatrix.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix);
		viewMatrix.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix);
		viewMatrix.rotate((float) Math.toRadians(camera.getRoll()), new Vector3f(0, 0, 1), viewMatrix);

		Vector3f camPos = camera.getPosition();
		Vector3f negCamPos = new Vector3f(-camPos.x, -camPos.y, -camPos.z);
		viewMatrix.translate(negCamPos, viewMatrix);

		return viewMatrix;
	}

	public static float lerp(float current, float toNum, float percentage) {
		return current + percentage * (toNum - current);
		// return (current *(1 - percentage)) + (toNum * percentage);
	}

	public static float rgbToFloat(int rgbValue) {
		return rgbValue / 255f;
	}

	public static Vector3f rgbToVector(int r, int g, int b, float scale) {
		return (Vector3f) new Vector3f(r / 255, g / 255, b / 255).normalize(scale);
	}

	public static Vector3f rgbToVector(int r, int g, int b) {
		return (Vector3f) new Vector3f(r / 255, g / 255, b / 255);
	}
}
