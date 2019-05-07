package core;

import java.util.ArrayList;
import java.util.List;

import core.audio.AudioMaster;
import core.entities.Camera;
import core.entities.Entity;
import core.entities.Light;
import core.event.Callback;
import core.font.TextMaster;
import core.gui.GUI;
import core.inputs.Keyboard;
import core.inputs.Mouse;
import core.loaders.Loader;
import core.renderEngine.DisplayManager;
import core.renderEngine.MasterRenderer;

public abstract class Scene {

	protected WindowManager manager;

	protected List<Entity> entities;
	protected List<Light> lights;
	protected List<GUI> guis;
	protected Camera camera;
	// protected MousePicker picker;
	protected MasterRenderer renderer;

	private boolean stop;

	public Scene(WindowManager manager) {
		DisplayManager.createDisplay();
		Keyboard.init();
		Mouse.init();
		TextMaster.init();
		try {
			AudioMaster.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
		entities = new ArrayList<Entity>();
		lights = new ArrayList<Light>();
		guis = new ArrayList<GUI>();
		renderer = new MasterRenderer();

		stop = false;

		this.manager = manager;
	}

	public abstract void init();

	public abstract void tickGame();

	public abstract void cleanUp();

	public void start() {

		init();

		while (!DisplayManager.closeRequested() && !stop) {
			float loopStart = DisplayManager.getCurrentTime();
			tickGame();
			DisplayManager.sync(loopStart);
		}
		cleanUpEverything();
	}

	protected void render() {
		renderer.renderScene(entities, lights, camera, null);
		GUI.render(guis);
		TextMaster.render();
		DisplayManager.updateDisplay();
	}

	protected void addCallback(Callback c) {
		Keyboard.addCallback(c);
		Mouse.addCallback(c);
	}

	public void stop() {
		stop = true;
	}

	public void cleanUpEverything() {
		for (Entity entity : entities) {
			if (entity.getSource() != null) {
				entity.getSource().delete();
			}
		}
		cleanUp();
		GUI.cleanUp();
		Keyboard.cleanUp();
		Mouse.cleanUp();
		AudioMaster.cleanUp();
		renderer.cleanUp();
		entities.clear();
		lights.clear();
		Loader.cleanUp();
		DisplayManager.closeDisplay();
	}

}
