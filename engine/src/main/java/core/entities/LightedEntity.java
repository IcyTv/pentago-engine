package core.entities;

import org.joml.Vector3f;

import core.entities.Entity;
import core.entities.Light;
import core.models.TexturedModel;

public class LightedEntity extends Entity {

	private Light light;
	
	public LightedEntity(TexturedModel model, int texIndex, Vector3f position, float rotX, float rotY, float rotZ, float scale, Vector3f lcolor, Vector3f lattenuation) {
		super(model, texIndex, position, rotX, rotY, rotZ, scale);
		this.light = new Light(new Vector3f(position.x, position.y + 15, position.z), lcolor, lattenuation);
	}

	@Override
	public void setPosition(Vector3f newPos) {
		super.setPosition(newPos);
		light.setPosition(new Vector3f(newPos.x, newPos.y + 15, newPos.z));
	}
	
	public Light getLight() {
		return light;
	}
}
