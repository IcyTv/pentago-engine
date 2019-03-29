package testing;

import java.io.IOException;

import org.joml.Vector2f;
import org.joml.Vector3f;

import core.Constants;
import core.Scene;
import core.audio.AudioMaster;
import core.entities.Camera;
import core.entities.Entity;
import core.entities.EntityGroup;
import core.entities.Light;
import core.event.KeyEvent;
import core.event.MouseClickedEvent;
import core.gui.GUI;
import core.inputs.Keyboard;
import core.renderEngine.DisplayManager;
import tools.Maths;

//#endregion

/**
 * Scene for testing engine.
 */
public class TestScene extends Scene {
	private float distance = 2;
	private EntityGroup test;

	/**
	 * Simple Constructor calling Super.
	 */
	public TestScene() {
		super();
	}

	public void init() {
		GUI testGui = new GUI("gui/test", new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		Light sun = new Light(new Vector3f(0, 1000, -700), Maths.rgbToVector(255, 255, 255));
		super.lights.add(sun);
		AudioMaster.setDistanceAttenuationMethod(1, true);

		test = new EntityGroup(new Vector3f(0, 0, 0), 18);

		for (int i = 0; i < 18; i += 2) {
			Entity piece = new Entity("pentagoPiece", new Vector3f((i / 6) * distance, 0, ((i / 2) % 3) * distance), 1);
			Entity ball = new Entity("ball", new Vector3f((i / 6) * distance, 1.2f, ((i / 2) % 3) * distance), 1);
			ball.setHidden(true);
			test.setEntity(piece, i);
			test.setEntity(ball, i + 1);
		}

		super.camera = new Camera(test.getEntities()[test.getEntities().length / 2]);

		for (Entity var : test.getEntities()) {
			super.entities.add(var);
		}

		// picker = new MousePicker(camera, );
	}

	@Override
	public void tickGame() {
		// Constants..info(Constants.state);
		super.camera.move();

		Light.sort(super.lights, super.camera);
		super.render();
		GUI.render();

	}

	@Override
	public void tickMenu() {
	}

	@Override
	public void onKey(KeyEvent ev, boolean press) {
		if (press) {
			switch (Keyboard.toCharacter(ev.getKey())) {
			case "NUM_3":
				test.getEntity(1).toggleHidden();
				break;
			case "NUM_6":
				test.getEntity(3).toggleHidden();

				break;
			case "NUM_9":
				test.getEntity(5).toggleHidden();

				break;
			case "NUM_2":
				test.getEntity(7).toggleHidden();

				break;
			case "NUM_5":
				test.getEntity(9).toggleHidden();

				break;
			case "NUM_8":
				test.getEntity(11).toggleHidden();

				break;
			case "NUM_1":
				test.getEntity(13).toggleHidden();

				break;
			case "NUM_4":
				test.getEntity(15).toggleHidden();
				break;
			case "NUM_7":
				test.getEntity(17).toggleHidden();
				break;
			default:
				System.out.println(Keyboard.toCharacter(ev.getKey()));
			}
		}

	}

	@Override
	public void onClick(MouseClickedEvent ev, boolean press) {
		if (press)
			System.out.println(ev.getX());
	}

	// Static methods

	public static void main(String[] args) throws SecurityException, IOException {

		// RUN CONFIG FOR DEBUGGING PURPOSES:
		// -javaagent:/home/michael/eclipse-workspace/engine/libs/debug/lwjglx-debug-1.0.0.jar

		System.setProperty("org.lwjgl.util.Debug", "true");
		DisplayManager.createDisplay();
		Constants.state = Constants.STATE.GAME;
		TestScene scene = new TestScene();
		scene.start();
		DisplayManager.closeDisplay();
	}

}
