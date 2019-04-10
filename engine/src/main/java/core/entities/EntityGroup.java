package core.entities;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

public class EntityGroup<T> {

	public List<T> list;
	
	public Vector3f position;

	public EntityGroup(Vector3f position, int size) {
		this.position = position;
		list = new ArrayList<T>();
		for(int i = 0; i < size; i++) {
			list.add(null);
		}
	}

	public EntityGroup(int size) {
		this(new Vector3f(0, 0, 0), size);
	}

	public void set(T e, int index, boolean adjustPosition) {
		if (adjustPosition) {
			Vector3f ePos = ((Entity) e).getPosition();
			Vector3f posClone = new Vector3f(position);

			((Entity) e).setPosition(posClone.add(ePos));
		}
		list.set(index, e);
	}

	public List<T> getEntities() {
		return new ArrayList<T>(list);
	}

	public T getEntity(int index) {
		return list.get(index);
	}

	public int size() {
		return list.size();
	}

}