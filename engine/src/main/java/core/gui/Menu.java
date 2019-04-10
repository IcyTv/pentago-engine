package core.gui;

import java.util.ArrayList;
import java.util.List;

import core.Options;
import core.WindowManager;
import core.audio.AudioMaster;
import core.event.Callback;
import core.font.GUIText;
import core.font.TextMaster;
import core.inputs.Keyboard;
import core.inputs.Mouse;
import core.loaders.Loader;
import core.renderEngine.DisplayManager;

public abstract class Menu {
	
	protected WindowManager manager;

	protected List<GUI> guiElements;
	protected List<GUIText> texts;
	
	protected Options options;
	
	private boolean stop;
	
	public Menu(WindowManager manager) {
		DisplayManager.createDisplay();
		Keyboard.init();
		Mouse.init();
		
		guiElements = new ArrayList<GUI>();
		texts = new ArrayList<GUIText>();
		
		this.manager = manager;
	}
	
	public abstract void init();
	
	public abstract void tick();
	
	public abstract void cleanUp();
	
	public void start() {
		init();
		while (!DisplayManager.closeRequested() && !stop) {
			float loopStart = DisplayManager.getCurrentTime();
			tick();
			DisplayManager.sync(loopStart);
		}
		cleanUpEverything();
	}
	
	public Options getOptions() {
		return options;
	}
	
	protected void render() {
		GUI.render(guiElements);
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
		guiElements.clear();
		texts.clear();
		cleanUp();
		GUI.cleanUp();
		Keyboard.cleanUp();
		//Mouse.cleanUp();
		AudioMaster.cleanUp();
		Loader.cleanUp();
		DisplayManager.closeDisplay();
	}
	
}
