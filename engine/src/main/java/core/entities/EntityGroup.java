package core.entities;

import org.joml.Vector3f;

public class EntityGroup {

	public Entity[] entities;

	public Vector3f position;

	public EntityGroup(Vector3f position, int size) {
		this.position = position;
		entities = new Entity[size];
	}

	public void setEntity(Entity e, int index) {
		Vector3f ePos = e.getPosition();
		Vector3f posClone = new Vector3f(position);

		e.setPosition(posClone.add(ePos));
		entities[index] = e;
	}

	public Entity[] getEntities() {
		return entities;
	}

	public Entity getEntity(int index) {
		return entities[index];
	}

}