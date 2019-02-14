package testing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.joml.Vector3f;

import core.Constants;
import core.Scene;
import core.animation.Animation;
import core.animation.AnimationEntity;
import core.entities.Camera;
import core.entities.Entity;
import core.entities.Light;
import core.inputs.Mouse;
import core.loaders.Loader;
import core.loaders.OBJFileLoader;
import core.models.RawModel;
import core.models.TexturedModel;
import core.renderEngine.DisplayManager;
import core.textures.ModelTexture;
import tools.Maths;

public class TestScene extends Scene{

	public static Logger logger = Logger.getLogger("Debug");
	
	public TestScene() {
		super();
	}
	
	public void init2() {
		Light sun = new Light(new Vector3f(0, 1000, -700), Maths.rgbToVector(255, 255, 255));
		super.lights.add(sun);
		
		ModelTexture texture = new ModelTexture(Loader.loadTexture("entities/stallTexture"));
		texture.setReflectivity(10f);
		//texture.setFakeLighting(true);
		RawModel model = Loader.loadToVAO(OBJFileLoader.loadOBJ("entities/stall"));
		TexturedModel texturedModel = new TexturedModel(model, texture);
		Entity tmp = new Entity(texturedModel, new Vector3f(0, 0, -300), 0, 0, 0, 0.3f);
		
		Constants.state = Constants.STATE.CUTSCENE;
		
		super.camera = new Camera();
		
		Animation animation = new Animation(20);
		
		AnimationEntity cam = animation.addCamera(super.camera);
		AnimationEntity dragon = animation.addEntity(tmp);
		
		animation.addKeyframe(0, new Vector3f(0,0,0), cam);
		animation.addKeyframe(19, new Vector3f(10,10,10), cam);
		animation.addKeyframe(0, new Vector3f(-1, -1, -1), dragon);
		animation.addKeyframe(19, new Vector3f(-20, 10, -20), dragon);
		
		
		animation.finalize();
		super.renderer.playAnimation(animation, super.lights);
		
		Constants.state = Constants.STATE.GAME;
	}
	
	public void init() {
		logger.info("Init");
		Mouse.init();
		Light sun = new Light(new Vector3f(0, 1000, -700), Maths.rgbToVector(255, 255, 255));
		super.lights.add(sun);
		logger.info("Texture");
		
		ModelTexture texture = new ModelTexture(Loader.loadTexture("entities/lamp"));
		texture.setReflectivity(10f);
		//texture.setFakeLighting(true);
		logger.info("Model");
		RawModel model = Loader.loadToVAO(OBJFileLoader.loadOBJ("entities/lamp"));
		TexturedModel texturedModel = new TexturedModel(model, texture);
		Entity tmp = new Entity(texturedModel, new Vector3f(0, 0, -300), 0, 0, 0, 0.3f);
		//AudioMaster.setDistanceAttenuationMethod(1, true);
		
		//Source source = new Source();
//		source.setLooping(true);
//		source.play(AudioMaster.loadSound("songMono"));
//		source.setGain(1f);
//		source.setAttenuationVariables(2f, 25, 500);
//		
//		tmp.connectSource(source);

		logger.info("Camera");
		super.camera = new Camera(tmp);
		
		super.entities.add(tmp);
		logger.info("Done with init()");
	}
	
	
	@Override
	public void tickGame() {
		//logger.info(Constants.state);
		logger.info("Tick");
		super.camera.move();
		
		Light.sort(super.lights, super.camera);
		super.render();
		
	}

	@Override
	public void tickMenu() {
		// TODO Auto-generated method stub
		
	}
	
	//Static methods
	
	public static void main(String[] args) throws SecurityException, IOException {
		
		//RUN CONFIG FOR DEBUGGING PURPOSES: -javaagent:/home/michael/eclipse-workspace/engine/libs/debug/lwjglx-debug-1.0.0.jar
		
		String loggingProperties = "handlers= java.util.logging.ConsoleHandler\r\n" + 
		".level=WARNING\r\n" + 
		"java.util.logging.ConsoleHandler.level = INFO\r\n" + 
		"java.util.logging.ConsoleHandler.formatter = java.util.logging.SimpleFormatter\r\n" + 
		"java.util.logging.SimpleFormatter.format=[%1$tF %1$tT] [%4$-7s] %5$s %n";
		
		LogManager.getLogManager().readConfiguration(new ByteArrayInputStream(loggingProperties.getBytes(StandardCharsets.UTF_8)));
		
		logger.setUseParentHandlers(false);
		Handler ch = new ConsoleHandler();
		ch.setFormatter(new SimpleFormatter());
		logger.addHandler(ch);
		
		
		
		System.setProperty("org.lwjgl.util.Debug", "true");
		logger.info("Creating Display");
		DisplayManager.createDisplay();
		logger.info("Done");
		Constants.state = Constants.STATE.GAME;
		TestScene scene = new TestScene();
		scene.start();
		logger.info("Closing display");
		DisplayManager.closeDisplay();
	}

}
