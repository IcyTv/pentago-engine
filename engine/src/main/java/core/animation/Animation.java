package core.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;

import core.entities.Camera;
import core.entities.Entity;

public class Animation {

	private List<Vector3f[]> animation;
	private List<AnimationEntity> entities;
	private int duration;
	private int currentFrame;
	
	public Animation(int duration) {
		this.duration = duration;
		currentFrame = 0;
		animation = new ArrayList<Vector3f[]>();
		entities = new ArrayList<AnimationEntity>();
	}
	
	public boolean hasCamera() {
		for(AnimationEntity  i: entities) {
			if(i.isCamera()) {
				return true;
			}
		}
		return false;
	}
	
	public AnimationEntity addEntity(Entity e) {
		AnimationEntity aEntity = new AnimationEntity(e, null);
		entities.add(aEntity);
		animation.add(new Vector3f[duration]);
		return aEntity;
	}
	
	public AnimationEntity addCamera(Camera c) {
		AnimationEntity aEntity = new AnimationEntity(null, c);
		entities.add(aEntity);
		animation.add(new Vector3f[duration]);
		return aEntity;
	}
	
	public void addKeyframe(int frame, Vector3f position, AnimationEntity object) {
		int index = entities.indexOf(object);
		animation.get(index)[frame] = position;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public Map<AnimationEntity, Vector3f> giveFrame(){
		 Map<AnimationEntity, Vector3f> ret = new HashMap<AnimationEntity, Vector3f>();
		 int i = 0;
		 for(AnimationEntity ae: entities) {
			 ret.put(ae, animation.get(i)[currentFrame]);
			 i++;
		 }
		currentFrame++;
		 return ret;
	}
	
	public void finalize() {
		for(Vector3f[] v: animation) {
			int[] spacesSinceLast = new int[v.length];
			int ssl = 0;
			for(int i = 0; i < v.length; i++) {
				if (v[i] == null) {
					ssl++;
				} else {
					ssl = 0;
				}
				spacesSinceLast[i] = ssl;
			}
			int[] spacesToNext = new int[v.length];
			int stn = 0;
			for(int i = v.length - 1; i >= 0; i--) {
				if(v[i] == null) {
					stn++;
				} else {
					stn = 0;
				}
				spacesToNext[i] = stn;
			}
			
			for(int i = 0; i < v.length; i++) {
				if(v[i] == null) {
					v[i] = lerp(v, i, spacesSinceLast[i], spacesToNext[i]);
				}
			}
		}
	}
	
	public String toString() {
		String ret = "";
		for(Vector3f[] v: animation) {
			for(Vector3f n: v) {
				ret += n.toString() + "\n";
			}
			ret += "\n";
		}
		return ret;
	}
	
	public static Vector3f lerp(Vector3f[] array, int index, int ssl, int stn) {
		Vector3f initial = array[index-ssl];
		Vector3f last = array[index+stn];
		
		float dist = ssl + stn;
		float percentage = ssl/dist;
		
		float newX = lerp(initial.x, last.x, percentage);
		float newY = lerp(initial.y, last.y, percentage);
		float newZ = lerp(initial.z, last.z, percentage);
		
		return new Vector3f(newX, newY, newZ);
	}
	
	private static float lerp(float v0, float v1, float t) {
		return (1 - t) * v0 + t * v1;
	}
	
	
	public static void main(String[] args) {
		Animation test = new Animation(5);
		Entity e = new Entity(null, null, 0, 0, 0, 0);
		AnimationEntity ae = test.addEntity(e);
		test.addKeyframe(0, new Vector3f(0,0,0), ae);
		test.addKeyframe(2, new Vector3f(0,1,12.5f), ae);
		test.addKeyframe(4, new Vector3f(0,0,25), ae);
		test.finalize();
		System.out.println(test);
	}
	
}
