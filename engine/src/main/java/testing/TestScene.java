package testing;

import java.io.IOException;

import core.Constants;
import core.Scene;
import core.entities.EntityGroup;
import core.entities.Light;
import core.event.KeyEvent;
import core.event.MouseClickedEvent;
import core.gui.GUI;
import core.renderEngine.DisplayManager;

//#endregion

/**
 * Scene for testing engine.
 */
public class TestScene extends Scene {
	private float distance = 2;
	private int currentPanel = -1;
	private EntityGroup[] test;

	/**
	 * Simple Constructor calling Super.
	 */
	public TestScene() {
		super();
	}

	public void init() {
		// GUI testGui = new GUI("gui/test", new Vector2f(0, 0), new Vector2f(0.25f,
		// 0.25f));

		// super.guis.add(testGui);

		// Light sun = new Light(new Vector3f(0, 1000, -700), Maths.rgbToVector(255,
		// 255, 255));
		// super.lights.add(sun);
		// AudioMaster.setDistanceAttenuationMethod(1, true);

		// test = new EntityGroup[9];

		// for (int n = 0; n < test.length; n++) {
		// test[n] = new EntityGroup(new Vector3f((n / 3) * distance * 3, 0, (n % 3) *
		// distance * 3), 18);
		// for (int i = 0; i < 18; i += 2) {
		// Entity piece = new Entity("pentagoPiece", new Vector3f((i / 6) * distance, 0,
		// ((i / 2) % 3) * distance),
		// 1);
		// Entity ball = new Entity("ball", new Vector3f((i / 6) * distance, 1.2f, ((i /
		// 2) % 3) * distance), 1);
		// ball.setHidden(true);
		// test[n].setEntity(piece, i);
		// test[n].setEntity(ball, i + 1);
		// }
		// }

		// super.camera = new Camera(test[test.length /
		// 2].getEntities()[test[test.length / 2].getEntities().length / 2]);

		// for (int i = 0; i < test.length; i++) {

		// for (Entity var : test[i].getEntities()) {
		// super.entities.add(var);
		// }
		// }

		// picker = new MousePicker(camera, );
	}

	@Override
	public void tickGame() {
		// Constants..info(Constants.state);
		super.camera.move();

		Light.sort(super.lights, super.camera);
		super.render();

	}

	// Static methods

	public static void main(String[] args) throws SecurityException, IOException {

		// RUN CONFIG FOR DEBUGGING PURPOSES:
		// -javaagent:/home/michael/eclipse-workspace/engine/libs/debug/lwjglx-debug-1.0.0.jar

		System.getProperties().setProperty("javaagent", "/home/michael/git/pentago-engine/lwjglx-debug-1.0.0.jar");

		System.setProperty("org.lwjgl.util.Debug", "true");
		DisplayManager.createDisplay();
		Constants.state = Constants.STATE.GAME;
		TestScene scene = new TestScene();
		scene.start();
		DisplayManager.closeDisplay();
	}

	@Override
	public void cleanUp() {
		GUI.cleanUp();
	}

}
