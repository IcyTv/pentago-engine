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
import core.audio.AudioMaster;
import core.entities.Camera;
import core.entities.Entity;
import core.entities.Light;
import core.event.KeyEvent;
import core.inputs.Keyboard;
import core.renderEngine.DisplayManager;
import tools.Maths;

//#endregion

/**
 * Scene for testing engine.
 */
public class TestScene extends Scene {

	private int current = 0;

	/**
	 * Simple Constructor calling Super.
	 */
	public TestScene() {
		super();
	}

	public void init() {
		Constants.logger.info("Init");
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
		Entity entity = new Entity("fern", new Vector3f(0, 0, 0), 0.3f);
		AudioMaster.setDistanceAttenuationMethod(1, true);

		// Source source = new Source();
		// source.setLooping(true);
		// source.play(AudioMaster.loadSound("res/audio/songMono.ogg"));
		// source.setGain(0.1f);
		// source.setAttenuationVariables(2f, 25, 500);

		// source.play();

		// tmp.connectSource(source);

		Constants.logger.info("Camera");
		super.camera = new Camera(entity);

		super.entities.add(entity);
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

	@Override
	public void onKey(KeyEvent ev, boolean press) {
		Entity entity = super.entities.get(current);
		if (press) {
			switch (Keyboard.toCharacter(ev.getKey())) {
			case "ARROW_LEFT":
				entity.increasePosition(1, 0, 0);
				break;
			case "ARROW_RIGHT":
				entity.increasePosition(-1, 0, 0);
				break;
			case "ARROW_UP":
				entity.increasePosition(0, 0, 1);
				break;
			case "ARROW_DOWN":
				entity.increasePosition(0, 0, -1);
				break;
			case "ENTER":
				super.entities.add(new Entity("fern", new Vector3f(0, 0, 0), 0.3f));
				current++;
				break;
			default:
				System.out.println(Keyboard.toCharacter(ev.getKey()));
			}
		}
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
