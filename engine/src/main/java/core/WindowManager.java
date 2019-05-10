package core;

import java.util.ArrayList;
import java.util.List;

public abstract class WindowManager {

	protected List<Scene> scenes;

	protected Options currentOptions;
	protected Scene currentScene;

	public WindowManager() {
		scenes = new ArrayList<Scene>();
	}

	public void toScene(int index) {
		if (currentScene != null) {
			currentScene.stop();
		}
		currentScene = scenes.get(index);
		currentScene.start();
	}

}
