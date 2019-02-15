//package core.inputs;
//
//import java.util.List;
//
////import org.lwjgl.input.Mouse;
//import org.joml.Matrix4f;
//import org.joml.Vector2f;
//import org.joml.Vector3f;
//import org.joml.Vector4f;
//
//import core.entities.Camera;
//import core.entities.Entity;
//import core.renderEngine.DisplayManager;
//import tools.Maths;
//
//public class MousePicker {
//
//	private static final int RECURSION_COUNT = 200;
//	private static final float RAY_RANGE = 600f;
//	
//	private Vector3f currentRay = new Vector3f();
//	
//	private Matrix4f projectionMatrix;
//	private Matrix4f viewMatrix;
//	private Camera camera;
//	
//	private List<Entity> entities;
//	private Vector3f currentEntityPoint;
//	
//	public MousePicker(Camera camera, Matrix4f projectionMatrix, List<Entity> entities) {
//		this.camera = camera;
//		this.projectionMatrix = projectionMatrix;
//		this.entities = entities;
//		
//		viewMatrix = Maths.createViewMatrix(camera);
//	}
//	
//	public Vector3f getCurrentEntityPoint() {
//		return currentEntityPoint;
//	}
//	
//	public Vector3f getCurrentRay() {
//		return currentRay;
//	}
//	
//	public void update() {
//		viewMatrix = Maths.createViewMatrix(camera);
//		currentRay = calculateMouseRay();
//		if(intersectionInRange(0, RAY_RANGE, currentRay)) {
//			currentEntityPoint = binarySearch(0, 0, RAY_RANGE, currentRay);
//		} else {
//			currentEntityPoint = null;
//		}
//	}
//	
//	private Vector3f calculateMouseRay() {
//		float mouseX = Mouse.getX();
//		float mouseY = Mouse.getY();
//		Vector2f normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
//		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
//		Vector4f eyeCoords = toEyeCoords(clipCoords);
//		Vector3f worldRay = toWorldCoords(eyeCoords);
//		return worldRay;
//	}
//	
//	private Vector3f toWorldCoords(Vector4f eyeCoords) {
//		Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
//		Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
//		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
//		mouseRay.normalise();
//		return mouseRay;
//	}
//	
//	private Vector4f toEyeCoords(Vector4f clipCoords) {
//		Matrix4f invertedProjection = Matrix4f.invert(projectionMatrix, null);
//		Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoords, null);
//		return new Vector4f(eyeCoords.x, eyeCoords.y, -1.0f, 1.0f);
//	}
//	
//	private Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY) {
//		float x = (2.0f * mouseX) / DisplayManager.getWidth() -1f;
//		float y = (2.0f * mouseY) / DisplayManager.getHeight() -1f;
//		return new Vector2f(x, y);
//	}
//	
//	//************
//	
//	private Vector3f getPointOnRay(Vector3f ray, float distance) {
//		Vector3f camPosition = camera.getPosition();
//		Vector3f start = new Vector3f(camPosition.x, camPosition.y, camPosition.z);
//		Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
//		return Vector3f.add(start, scaledRay, null);
//	}
//	
//	private Vector3f binarySearch(int count, float start, float finish, Vector3f ray) {
//		float half = start + ((finish - start) / 2f);
//		if(count >= RECURSION_COUNT) {
//			 Vector3f endPoint = getPointOnRay(ray, half);
//			 Entity entity = getEntity(endPoint);
//			 if(entity != null) {
//				 return endPoint;
//			 } else {
//				 return null;
//			 }
//		}
//		if(intersectionInRange(start, half, ray)) {
//			return binarySearch(count + 1, start, half, ray);
//		} else {
//			return binarySearch(count + 1, half, finish, ray);
//		}
//	}
//	
//	private boolean intersectionInRange(float start, float finish, Vector3f ray) {
//		Vector3f startPoint = getPointOnRay(ray, start);
//		Vector3f endPoint = getPointOnRay(ray, finish);
//		if(!isUnderEntity(startPoint) && isUnderEntity(endPoint)) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//	
//	private boolean isUnderEntity(Vector3f point) {
//		Entity entity = getEntity(point);
//		float height = 0;
//		if(entity != null) {
//			height = entity.getPosition().y;
//		}
//		if(point.y < height) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//	
//	private Entity getEntity(Vector3f pos) {
//		Entity closest = null;
//		float distanceSquared = Integer.MAX_VALUE;
//		for(Entity e: entities) {
//			if(distanceSq(pos, e.getPosition()) < distanceSquared) {
//				closest = e;
//				distanceSquared = distanceSq(pos, e.getPosition());
//				//System.out.println("Closest " + distanceSquared);
//			}
//			//System.out.println("Closest " + distanceSquared);
//		}
//		return closest;
//	}
//	
//	private float distanceSq(Vector3f a, Vector3f b) {
//		if(a == null || b== null) {
//			return Integer.MAX_VALUE;
//		}
//		return (a.x - b.x)*(a.x-b.x) + (a.y-b.y)*(a.y-b.y) + (a.z-b.z)*(a.z-b.z);
//	}
//}
