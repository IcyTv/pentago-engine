package core.entities;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joml.Vector3f;

public class Light {

	private Vector3f position;
	private Vector3f color;
	private Vector3f attenuation;

	public Light(Vector3f position, Vector3f color) {
		this(position, color, new Vector3f(1, 0, 0));
	}

	public Light(Vector3f position, Vector3f color, Vector3f attenuation) {
		this.position = position;
		this.color = color;
		this.attenuation = attenuation;
	}

	public static void sort(List<Light> lights, final Camera camera) {
		Collections.sort(lights, new Comparator<Light>() {
			public int compare(Light o1, Light o2) {
				Vector3f toCameraVector1 = o1.getPosition().sub(camera.getPosition());
				Vector3f toCameraVector2 = o2.getPosition().sub(camera.getPosition());
				return (int) ((toCameraVector1.length() - toCameraVector2.length()) * 1000);
			}
		});
	}

	public Vector3f getAttenuation() {
		return attenuation;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getColor() {
		return color;
	}

	public void setColor(Vector3f color) {
		this.color = color;
	}

}
