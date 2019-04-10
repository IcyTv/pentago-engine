package core;

import java.util.ArrayList;
import java.util.List;

import core.gui.Menu;

public abstract class WindowManager {

	protected List<Menu> menus;
	protected List<Scene> scenes;
	
	protected Options currentOptions;
	protected Menu currentMenu;
	protected Scene currentScene;
	
	public WindowManager() {
		menus = new ArrayList<Menu>();
		scenes = new ArrayList<Scene>();
	}
	
	public void toMenu(int index) {
		if(currentMenu!= null) {
			currentMenu.stop();
		}
		currentMenu = menus.get(index);
		currentMenu.start();
	}
	
	public void toScene(int index) {
		if(currentScene!= null) {
			currentScene.stop();
		}
		currentScene = scenes.get(index);
		currentScene.start();
	}
	
}
