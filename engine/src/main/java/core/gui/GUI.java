package core.gui;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

import core.loaders.Loader;
import core.renderEngine.GUIRenderer;
import core.textures.GUITexture;

public class GUI {

	public static final List<GUITexture> guis = new ArrayList<GUITexture>();
	public static final GUIRenderer renderer = new GUIRenderer();

	GUITexture texture;

	public GUI(String texturePath, Vector2f position, Vector2f scale) {
		texture = new GUITexture(Loader.loadTexture(texturePath), position, scale);
		GUI.guis.add(texture);
	}

	public static void render() {
		renderer.render(guis);
	}

}
