package core.inputs;

import java.util.List;

//import org.lwjgl.input.Mouse;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import core.entities.Camera;
import core.entities.Entity;
import core.renderEngine.DisplayManager;
import tools.Maths;

public class MousePicker {

    private final float STEP_SIZE = 1.0f;
    private final int MAX_STEPS = 500;

	private static final int RECURSION_COUNT = 200;
	private static final float RAY_RANGE = 600f;
	
	private Vector3f currentRay = new Vector3f();
	
	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private Camera camera;
	
	private List<Entity> entities;
	private Vector3f currentEntityPoint;
	
	public MousePicker(Camera camera, Matrix4f projectionMatrix, List<Entity> entities) {
		this.camera = camera;
		this.projectionMatrix = projectionMatrix;
		this.entities = entities;
		
		viewMatrix = Maths.createViewMatrix(camera);
	}
	
	public Vector3f getCurrentEntityPoint() {
		return currentEntityPoint;
	}
	
	public Vector3f getCurrentRay() {
		return currentRay;
	}
	
	public void update() {
		viewMatrix = Maths.createViewMatrix(camera);
		currentRay = calculateMouseRay();
	}
	
	private Vector3f calculateMouseRay() {
		float mouseX = Mouse.getX();
		float mouseY = Mouse.getY();
		Vector2f normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		Vector3f worldRay = toWorldCoords(eyeCoords);
		return worldRay;
	}
	
	private Vector3f toWorldCoords(Vector4f eyeCoords) {
		Matrix4f invertedView = viewMatrix.invert();
		Vector4f rayWorld = invertedView.transform(eyeCoords);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalize();
		return mouseRay;
	}
	
	private Vector4f toEyeCoords(Vector4f clipCoords) {
		Matrix4f invertedProjection = projectionMatrix.invert();
		Vector4f eyeCoords = invertedProjection.transform(clipCoords);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1.0f, 1.0f);
	}
	
	private Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY) {
		float x = (2.0f * mouseX) / DisplayManager.WIDTH -1f;
		float y = (2.0f * mouseY) / DisplayManager.HEIGHT -1f;
		return new Vector2f(x, y);
	}
    
	//************
    
    public Entity getEntity(Vector3f ray){
        for (int i = 0; i < MAX_STEPS; i++) {
			Vector3f pointOnRay = getPointOnRay(ray, i * STEP_SIZE);
			if(getClosestEntity(pointOnRay).inside(pointOnRay)){
				return getClosestEntity(pointOnRay);
			}
		}
		return null;
    }

	private Vector3f getPointOnRay(Vector3f ray, float distance) {
		Vector3f camPosition = camera.getPosition();
		Vector3f start = new Vector3f(camPosition.x, camPosition.y, camPosition.z);
		Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
		return start.add(scaledRay);
	}
	
	private Entity getClosestEntity(Vector3f pos) {
		Entity closest = null;
		float distanceSquared = Integer.MAX_VALUE;
		for(Entity e: entities) {
			if(distanceSq(pos, e.getPosition()) < distanceSquared) {
				closest = e;
				distanceSquared = distanceSq(pos, e.getPosition());
				//System.out.println("Closest " + distanceSquared);
			}
			//System.out.println("Closest " + distanceSquared);
		}
		return closest;
	}
	
	private float distanceSq(Vector3f a, Vector3f b) {
		if(a == null || b== null) {
			return Integer.MAX_VALUE;
		}
		return (a.x - b.x)*(a.x-b.x) + (a.y-b.y)*(a.y-b.y) + (a.z-b.z)*(a.z-b.z);
	}
}
