package testing;

//#region
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;

import org.joml.Vector3f;

import core.Constants;
import core.Scene;
import core.animation.Animation;
import core.animation.AnimationEntity;
import core.audio.AudioMaster;
import core.audio.Source;
import core.entities.Camera;
import core.entities.Entity;
import core.entities.Light;
import core.inputs.Mouse;
import core.inputs.MousePicker;
import core.loaders.Loader;
import core.loaders.OBJFileLoader;
import core.models.RawModel;
import core.models.TexturedModel;
import core.renderEngine.DisplayManager;
import core.textures.ModelTexture;
import tools.Maths;

//#endregion

/**
 * Scene for testing engine.
 */
public class TestScene extends Scene {

	private MousePicker picker;

	/**
	 * Simple Constructor calling Super.
	 */
	public TestScene() {
		super();
	}

	/**
	 * Second test for animation.
	 */
	public void init2() {
		Light sun = new Light(new Vector3f(0, 1000, -700), Maths.rgbToVector(255, 255, 255));
		super.lights.add(sun);

		ModelTexture texture = new ModelTexture(Loader.loadTexture("entities/stallTexture"));
		texture.setReflectivity(10f);
		// texture.setFakeLighting(true);
		RawModel model = Loader.loadToVAO(OBJFileLoader.loadOBJ("entities/stall"));
		TexturedModel texturedModel = new TexturedModel(model, texture);
		Entity tmp = new Entity(texturedModel, new Vector3f(0, 0, -300), 0, 0, 0, 0.3f);

		Constants.state = Constants.STATE.CUTSCENE;

		super.camera = new Camera();

		Animation animation = new Animation(20);

		AnimationEntity cam = animation.addCamera(super.camera);
		AnimationEntity dragon = animation.addEntity(tmp);

		animation.addKeyframe(0, new Vector3f(0, 0, 0), cam);
		animation.addKeyframe(19, new Vector3f(10, 10, 10), cam);
		animation.addKeyframe(0, new Vector3f(-1, -1, -1), dragon);
		animation.addKeyframe(19, new Vector3f(-20, 10, -20), dragon);

		animation.finalize();
		super.renderer.playAnimation(animation, super.lights);

		Constants.state = Constants.STATE.GAME;
	}

	public void init() {
		Constants.logger.info("Init");
		Mouse.init();
		Light sun = new Light(new Vector3f(0, 1000, -700), Maths.rgbToVector(255, 255, 255));
		super.lights.add(sun);
		Constants.logger.info("Texture");
		/*
		 * ModelTexture texture = new ModelTexture(Loader.loadTexture("entities/fern"));
		 * // texture.setReflectivity(10f); // texture.setFakeLighting(true);
		 * texture.setTransparency(true); Constants.logger.info("Model"); RawModel model
		 * = Loader.loadToVAO(OBJFileLoader.loadOBJ("entities/fern")); TexturedModel
		 * texturedModel = new TexturedModel(model, texture);
		 */
		// Entity tmp = new Entity(texturedModel, new Vector3f(0, 0, -300), 0, 0, 0,
		// 0.3f);
		Entity tmp = new Entity("fern", new Vector3f(0, 0, 0), 0.3f);
		Constants.logger.warning(Boolean.toString(tmp.inside(new Vector3f(0.1f, 0.1f, 0.1f))));
		AudioMaster.setDistanceAttenuationMethod(1, true);

		Source source = new Source();
		source.setLooping(true);
		source.play(AudioMaster.loadSound("res/audio/songMono.ogg"));
		source.setGain(0.1f);
		source.setAttenuationVariables(2f, 25, 500);

		source.play();

		tmp.connectSource(source);

		Constants.logger.info("Camera");
		super.camera = new Camera(tmp);

		super.entities.add(tmp);
		Constants.logger.info("Done with init()");
		// picker = new MousePicker(camera, );
	}

	@Override
	public void tickGame() {
		// Constants.logger.info(Constants.state);
		Constants.logger.info("Tick");
		super.camera.move();

		Light.sort(super.lights, super.camera);
		super.render();

	}

	@Override
	public void tickMenu() {
	}

	// Static methods

	public static void main(String[] args) throws SecurityException, IOException {

		// RUN CONFIG FOR DEBUGGING PURPOSES:
		// -javaagent:/home/michael/eclipse-workspace/engine/libs/debug/lwjglx-debug-1.0.0.jar

		String loggingProperties = "handlers= java.util.logging.ConsoleHandler\r\n" + ".level=WARNING\r\n"
				+ "java.util.logging.ConsoleHandler.level = INFO\r\n"
				+ "java.util.logging.ConsoleHandler.formatter = java.util.logging.SimpleFormatter\r\n"
				+ "java.util.logging.SimpleFormatter.format=[%1$tF %1$tT] [%4$-7s] %5$s %n";

		LogManager.getLogManager()
				.readConfiguration(new ByteArrayInputStream(loggingProperties.getBytes(StandardCharsets.UTF_8)));

		Constants.logger.setUseParentHandlers(false);
		Handler ch = new ConsoleHandler();
		ch.setFormatter(new SimpleFormatter());
		Constants.logger.addHandler(ch);

		System.setProperty("org.lwjgl.util.Debug", "true");
		Constants.logger.info("Creating Display");
		DisplayManager.createDisplay();
		Constants.logger.info("Done");
		Constants.state = Constants.STATE.GAME;
		TestScene scene = new TestScene();
		scene.start();
		Constants.logger.info("Closing display");
		DisplayManager.closeDisplay();
	}

}
