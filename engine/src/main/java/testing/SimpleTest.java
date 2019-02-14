package testing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import core.entities.Camera;
import core.entities.Entity;
import core.entities.Light;
import core.inputs.Mouse;
import core.loaders.Loader;
import core.loaders.OBJFileLoader;
import core.models.RawModel;
import core.models.TexturedModel;
import core.textures.ModelTexture;
import core.renderEngine.DisplayManager;
import core.renderEngine.MasterRenderer;
import tools.Maths;

public class SimpleTest {
	private static MasterRenderer renderer;
	private static List<Entity> entities;
	private static List<Light> lights;
	private static Camera camera;
	
	public static void main(String[] args) throws SecurityException, IOException {		
		String loggingProperties = "handlers= java.util.logging.ConsoleHandler\r\n" + 
		".level=WARNING\r\n" + 
		"java.util.logging.ConsoleHandler.level = INFO\r\n" + 
		"java.util.logging.ConsoleHandler.formatter = java.util.logging.SimpleFormatter\r\n" + 
		"java.util.logging.SimpleFormatter.format=[%1$tF %1$tT] [%4$-7s] %5$s %n";
	
		LogManager.getLogManager().readConfiguration(new ByteArrayInputStream(loggingProperties.getBytes(StandardCharsets.UTF_8)));
		
		entities = new ArrayList<Entity>();
		lights = new ArrayList<Light>();
		DisplayManager.createDisplay();
		renderer = new MasterRenderer();
		Mouse.init();
		Light sun = new Light(new Vector3f(0, 1000, -700), Maths.rgbToVector(255, 255, 255));
		lights.add(sun);
		
		ModelTexture texture = new ModelTexture(Loader.loadTexture("entities/lamp"));
		RawModel model = Loader.loadToVAO(OBJFileLoader.loadOBJ("entities/testPlane"));
		
		TexturedModel texturedModel = new TexturedModel(model, texture);
		Entity tmp = new Entity(texturedModel, new Vector3f(0, 0, -300), 0, 0, 0, 0.3f);
		
		camera = new Camera(tmp);
		entities.add(tmp);
		
		//GL11.glEnable();
		
		while(!DisplayManager.closeRequested()) {
			camera.move();
			
			render();
		}
	}
	
	public static void render() {
		renderer.renderScene(entities, lights, camera, null);
		DisplayManager.updateDisplay();	
	}
}
