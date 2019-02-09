package core.animation;

import core.entities.Camera;
import core.entities.Entity;

public class AnimationEntity {

	private Entity entity;
	private Camera camera;
	
	public AnimationEntity(Entity entity, Camera camera) {
		super();
		this.entity = entity;
		this.camera = camera;
	}
	
	public boolean isCamera() {
		return camera != null;
	}
	
	public Entity getEntity() {
		return entity;
	}
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	public Camera getCamera() {
		return camera;
	}
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	
	public boolean equals(AnimationEntity e){
		if(entity != null && entity.equals(e.getEntity())) {
			return true;
		} else if(camera != null && camera.equals(e.getCamera())) {
			return true;
		} else {
			return false;
		}
	}
	
}
