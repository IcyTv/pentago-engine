package core;

import java.util.ArrayList;
import java.util.List;

import core.audio.AudioMaster;
import core.inputs.MousePicker;
import core.entities.Camera;
import core.entities.Entity;
import core.entities.Light;
import core.loaders.Loader;
import core.renderEngine.DisplayManager;
import core.renderEngine.MasterRenderer;

public abstract class Scene {
	
	protected List<Entity> entities;
	protected List<Light> lights;
	protected Camera camera;
	protected MousePicker picker;
	protected MasterRenderer renderer;
	protected int tickrate = 128;
	
	public Scene() {
		AudioMaster.init();
		entities = new ArrayList<Entity>();
		lights = new ArrayList<Light>();
		renderer = new MasterRenderer();
	}
	
	public abstract void init();
	
	public abstract void tickGame();
	
	public abstract void tickMenu();
	
	public void start() {
		init();
		while(!DisplayManager.closeRequested()) {
			float loopStart = DisplayManager.getCurrentTime();
			if(Constants.state == Constants.STATE.MENU) {
				tickMenu();
			} else if(Constants.state == Constants.STATE.GAME) {
				tickGame();
			}
			sync(loopStart, tickrate);
		}
		cleanUpEverything();
	}
	
	protected void sync(float starttime, int tickrate) {
		float endtime = starttime + 1.0f/tickrate;
		while(DisplayManager.getCurrentTime() < endtime) {
			try {
				Thread.sleep(1);
			} catch(InterruptedException e) {}
		}
	}
	
	protected void render() {
		renderer.renderScene(entities, lights, camera, null);
		DisplayManager.updateDisplay();
	}
	
	public void cleanUpEverything() {
		for(Entity entity: entities) {
			if(entity.getSource() != null) {
				entity.getSource().delete();
			}
		}
		AudioMaster.cleanUp();
		renderer.cleanUp();
		entities.clear();
		lights.clear();
		Loader.cleanUp();
		DisplayManager.closeDisplay();
	}
	
}
