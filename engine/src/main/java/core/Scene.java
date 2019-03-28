package core;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import core.audio.AudioMaster;
import core.entities.Camera;
import core.entities.Entity;
import core.entities.Light;
import core.event.KeyEvent;
import core.inputs.Keyboard;
import core.inputs.Mouse;
import core.loaders.Loader;
import core.renderEngine.DisplayManager;
import core.renderEngine.MasterRenderer;

public abstract class Scene {

	protected List<Entity> entities;
	protected List<Light> lights;
	protected Camera camera;
	// protected MousePicker picker;
	protected MasterRenderer renderer;

	public Scene() {
		Keyboard.init();
		Mouse.init();
		try {
			AudioMaster.init();
		} catch (Exception e) {
			Constants.logger.severe(e.getMessage());
		}
		entities = new ArrayList<Entity>();
		lights = new ArrayList<Light>();
		renderer = new MasterRenderer();
	}

	public abstract void init();

	public abstract void tickGame();

	public abstract void tickMenu();

	public abstract void onKey(KeyEvent ev, boolean press);

	public void start() {

		Keyboard.addCallback(e -> onKey((KeyEvent) e, ((KeyEvent) e).getAction() == GLFW.GLFW_PRESS));

		init();

		while (!DisplayManager.closeRequested()) {
			float loopStart = DisplayManager.getCurrentTime();
			if (Constants.state == Constants.STATE.MENU) {
				tickMenu();
			} else if (Constants.state == Constants.STATE.GAME) {
				tickGame();
			}
			sync(loopStart, DisplayManager.TICKRATE);
		}
		cleanUpEverything();
	}

	protected void sync(float starttime, int tickrate) {
		float endtime = starttime + 1.0f / tickrate;
		while (DisplayManager.getCurrentTime() < endtime) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}
	}

	protected void render() {
		renderer.renderScene(entities, lights, camera, null);
		DisplayManager.updateDisplay();
	}

	public void cleanUpEverything() {
		for (Entity entity : entities) {
			if (entity.getSource() != null) {
				entity.getSource().delete();
			}
		}
		Keyboard.cleanUp();
		AudioMaster.cleanUp();
		renderer.cleanUp();
		entities.clear();
		lights.clear();
		Loader.cleanUp();
		DisplayManager.closeDisplay();
	}

}
