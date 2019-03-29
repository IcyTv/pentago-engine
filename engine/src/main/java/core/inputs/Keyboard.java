package core.inputs;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import core.event.Callback;
import core.event.CallbackStackInterruption;
import core.event.KeyEvent;
import core.renderEngine.DisplayManager;

public abstract class Keyboard {

	private static Map<Integer, String> toCharacterMap;
	private static Map<String, Integer> toKeyCodeMap;

	private static GLFWKeyCallback keyCallback;
	private static Stack<Callback> callbacks;

	public static void init() {

		toCharacterMap = new HashMap<Integer, String>();
		toKeyCodeMap = new HashMap<String, Integer>();

		initKeys();

		callbacks = new Stack<Callback>();

		GLFW.glfwSetKeyCallback(DisplayManager.window, keyCallback = new GLFWKeyCallback() {

			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				for (Callback c : callbacks) {
					try {
						c.invoke(new KeyEvent(key, action, mods));
					} catch (CallbackStackInterruption e) {
						break;
					}
				}
			}
		});
	}

	private static void initKeys() {
		for (int i = 65; i < 90; i++) {
			addKey(i, Character.toString(Character.toChars(i)[0]));
		}

		for (int i = 320; i < 330; i++) {
			addKey(i, "NUM_" + (i - 320));
		}

		addKey(263, "ARROW_LEFT");
		addKey(264, "ARROW_DOWN");
		addKey(262, "ARROW_RIGHT");
		addKey(265, "ARROW_UP");

		addKey(257, "ENTER");
		addKey(256, "ESC");
	}

	public static void addCallback(Callback c) {
		Stack<Callback> tmpCallStack = new Stack<Callback>();

		while (!callbacks.empty() && tmpCallStack.peek().priority() < c.priority()) {
			tmpCallStack.push(callbacks.pop());
		}
		callbacks.push(c);
		while (!tmpCallStack.empty()) {
			callbacks.push(tmpCallStack.pop());
		}
	}

	public static void removeCallback(Callback c) {
		callbacks.remove(c);
	}

	public static void cleanUp() {
		callbacks.clear();
		keyCallback.free();
	}

	public static void addKey(int keyCode, String character) {
		toCharacterMap.put(keyCode, character);
		toKeyCodeMap.put(character, keyCode);
	}

	public static String toCharacter(int keyCode) {
		return toCharacterMap.get(keyCode) != null ? toCharacterMap.get(keyCode) : Integer.toString(keyCode);
	}

	public static int toKeyCode(String character) {
		return toKeyCodeMap.get(character);
	}
}