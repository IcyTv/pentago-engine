package core.entities;

import org.joml.Vector3f;

import core.audio.AudioMaster;
import core.inputs.Mouse;

public class Camera {

	public static float MAX_RIGHT = 45.0f;
	public static float MAX_LEFT = -45.0f;
	
	public static float MAX_UP = 45f;
	public static float MAX_DOWN = -10f;

	// User controllable variables
	private float distanceFromEntity = 20f;
	private float angleAroundEntity = 0f;
	private float pitch = 20f;

	private Vector3f position;
	private float yaw;
	private float roll;

	private Entity trackableEntity;

	public Camera(Entity trackable) {
		trackableEntity = trackable;
		position = new Vector3f(0, 0, 0);
	}

	public Camera() {
		position = new Vector3f(0, 0, 0);
	}

	public void move(Vector3f pos, float pitch, float yaw, float roll) {
		this.position = pos;
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
	}

	public void move(Vector3f pos) {
		this.position = pos;
	}

	public void move() {
		calculateZoom();
		calculatePitch();
		calculateAAP();

		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticDist();

		calculateCamPos(horizontalDistance, verticalDistance);
		yaw = 180 - (angleAroundEntity);

		AudioMaster.setListenerDataPos(position.x, position.y, position.z);
		Vector3f rotation = getRotation();
		AudioMaster.setListenerDataRot(rotation.x, rotation.y, rotation.z);
		// System.out.println(position.x + " " + position.y + " " + position.z);
	}

	private void calculateCamPos(float horizontalDistance, float verticDistance) {
		float theta = trackableEntity.getRotY() + angleAroundEntity;
		float xoff = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float zoff = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		position.y = trackableEntity.getPosition().y + verticDistance;
		position.x = trackableEntity.getPosition().x - xoff;
		position.z = trackableEntity.getPosition().z - zoff;
	}

	private float calculateHorizontalDistance() {
		return (float) (distanceFromEntity * Math.cos(Math.toRadians(pitch)));
	}

	private float calculateVerticDist() {
		return (float) (distanceFromEntity * Math.sin(Math.toRadians(pitch)));
	}

	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 5;
		distanceFromEntity -= zoomLevel;
		distanceFromEntity = Math.max(Math.min(distanceFromEntity, 500), 10);
	}

	private void calculatePitch() {
		if (Mouse.isButtonDown(1)) {
			float pitchChange = Mouse.getDY() * 0.1f;
			pitch -= pitchChange;
			if(pitch > MAX_UP) {
				pitch = MAX_UP;
			} else if(pitch < MAX_DOWN) {
				pitch = MAX_DOWN;
			}
		}
		// pitch = Math.max(Math.min(pitch, 50), 5);
	}

	private void calculateAAP() {
		if (Mouse.isButtonDown(1)) {
			float angleChange = Mouse.getDX() * 0.2f;
			angleAroundEntity -= angleChange;
			if (angleAroundEntity > MAX_RIGHT) {
				angleAroundEntity = MAX_RIGHT;
			} else if (angleAroundEntity < MAX_LEFT) {
				angleAroundEntity = MAX_LEFT;
			}
		}
	}

	public Vector3f getRotation() {
		float tmp = (float) Math.cos(pitch);
		float x = tmp * (float) Math.cos(yaw);
		float y = (float) Math.sin(pitch);
		float z = tmp * (float) Math.sin(-yaw);
		return new Vector3f(x, y, z);
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}

	public void invertPitch() {
		pitch = -pitch;

	};
}
