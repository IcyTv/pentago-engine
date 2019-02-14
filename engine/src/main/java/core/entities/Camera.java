package core.entities;

import org.joml.Vector3f;

import core.inputs.Mouse;

import core.audio.AudioMaster;

public class Camera {

	//User controllable variables
	private float distanceFromEntity = 20f;
	private float angleAroundEntity = 0f;
	private float pitch = 20f;
	
	private Vector3f position;
	private float yaw;
	private float roll;
	
	private Entity trackableEntity;
	
	public Camera(Entity trackable) {
		trackableEntity = trackable;
		position = new Vector3f(0,0,0);
	}
	
	public Camera() {
		position = new Vector3f(0,0,0);
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
		yaw = 180-(angleAroundEntity);
	
		AudioMaster.setListenerData(position.x, position.y, position.z);
		//System.out.println(position.x + " " + position.y + " " + position.z);
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
		if(Mouse.isButtonDown(1)) {
			float pitchChange = Mouse.getDY() * 0.1f;
			pitch -= pitchChange;
		}
		//pitch = Math.max(Math.min(pitch, 50), 5);
	}
	
	private void calculateAAP() {
		if(Mouse.isButtonDown(1)) {
			float angleChange = Mouse.getDX() * 0.2f;
			angleAroundEntity -= angleChange;
		}
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
