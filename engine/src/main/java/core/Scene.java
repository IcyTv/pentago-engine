package core;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

import core.audio.AudioMaster;
import core.entities.Camera;
import core.entities.Entity;
import core.entities.Light;
import core.event.Callback;
import core.font.TextMaster;
import core.gui.GUI;
import core.gui.GUIImage;
import core.inputs.Keyboard;
import core.inputs.Mouse;
import core.loaders.Loader;
import core.renderEngine.DisplayManager;
import core.renderEngine.MasterRenderer;

public abstract class Scene {

	protected WindowManager manager;

	protected List<Entity> entities;
	protected List<Light> lights;
	protected GUI mainGUI;
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
		renderer = new MasterRenderer();
		mainGUI = new GUI(new Vector2f(0, 0), new Vector2f(1, 1));

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
		GUIImage.render(mainGUI.getImages());
		TextMaster.render(mainGUI.getRenderMap());
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
		GUIImage.cleanUp();
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
