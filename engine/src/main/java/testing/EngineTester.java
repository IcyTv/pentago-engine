package testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.joml.Vector3f;
import org.joml.Vector4f;

import core.entities.Camera;
import core.entities.Entity;
import core.entities.Light;
import core.entities.LightedEntity;
import core.loaders.Loader;
import core.loaders.OBJFileLoader;
import core.models.RawModel;
import core.models.TexturedModel;
import core.renderEngine.DisplayManager;
import core.renderEngine.MasterRenderer;
import core.textures.ModelTexture;
import core.inputs.Mouse;
import core.inputs.MousePicker;
//import tools.MousePicker;
import tools.Maths;

public class EngineTester {

	public static void main(String[] args) {
		DisplayManager.createDisplay();
		
		RawModel model;
		TexturedModel texturedModel;
		
		Mouse.init();
		
		List<Entity> entities = new ArrayList<Entity>();
		ModelTexture texture;
		
		List<Light> lights = new ArrayList<Light>();
		Light sun = new Light(new Vector3f(0, 1000, -700), Maths.rgbToVector(255, 255, 255));
		
		texture = new ModelTexture(Loader.loadTexture("entities/lamp"));
		model = Loader.loadToVAO(OBJFileLoader.loadOBJ("entities/lamp"));
		texturedModel = new TexturedModel(model, texture);
		
		
		//GuiRenderer guiRenderer = new GuiRenderer();
		
		MasterRenderer renderer = new MasterRenderer();
		//waters.add(new WaterTile(-400, -400, -17));
		
		//List<GuiTexture> guis = new ArrayList<GuiTexture>();
		//guis.add(new GuiTexture(fbos.getRefractionDepthTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 0.5f)));
		
		texture = new ModelTexture(Loader.loadTexture("entities/stallTexture"));
		//texture.setReflectivity(10f);
		//texture.setFakeLighting(true);
		model = Loader.loadToVAO(OBJFileLoader.loadOBJ("entities/stall"));
		texturedModel = new TexturedModel(model, texture);
		Entity player = new Entity(texturedModel, new Vector3f(0, 0, -300), 0, 0, 0, 0.3f);
		entities.add(player);
		Camera camera = new Camera(player);
		//MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), entities);
		
		while(!DisplayManager.closeRequested()) {
			camera.move();
			//picker.update();
			//System.out.println(picker.getCurrentEntityPoint());
			
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			
			//entities.add(player);
			
			Light.sort(lights, camera);
			lights.add(0, sun);

			
			renderer.renderScene(entities, lights, camera, null);
			lights.remove(sun);
			//entities.remove(player);
			
			//waterRenderer.render(new ArrayList<WaterTile>(waters), camera, sun);
			
			//guiRenderer.render(guis);
			
			DisplayManager.updateDisplay();
		}
		
		//fbos.cleanUp();
		//waterShader.cleanUp();
		//guiRenderer.cleanUp();
		renderer.cleanUp();
		Loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
