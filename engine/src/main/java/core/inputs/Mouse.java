package core.inputs;

import java.util.Stack;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import core.event.Callback;
import core.event.CallbackStackInterruption;
import core.event.MouseClickedEvent;
import core.renderEngine.DisplayManager;

public class Mouse {

	private static float x;
	private static float y;
	private static float dx;
	private static float dy;

	private static float xoffset;
	private static float yoffset;

	private static GLFWCursorPosCallback cursorCallback;
	private static GLFWScrollCallback scrollCallback;
	private static GLFWMouseButtonCallback clickCallback;

	private static Stack<Callback> callbacks;
	
	private static boolean initialized = false;

	public static void init() {
		
		if(initialized) {
			return;
		}

		callbacks = new Stack<Callback>();

		GLFW.glfwSetCursorPosCallback(DisplayManager.window, cursorCallback = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				Mouse.dx = -(Mouse.x - (float) xpos);
				Mouse.dy = (Mouse.y - (float) ypos);

				Mouse.x = (float) xpos;
				Mouse.y = (float) ypos;
			}
		});

		GLFW.glfwSetScrollCallback(DisplayManager.window, scrollCallback = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				Mouse.xoffset = (float) xoffset;
				Mouse.yoffset = (float) yoffset;
			}
		});

		GLFW.glfwSetMouseButtonCallback(DisplayManager.window, clickCallback = new GLFWMouseButtonCallback() {

			@Override
			public void invoke(long window, int button, int action, int mods) {
				for (Callback c : callbacks) {
					try {
						c.invoke(new MouseClickedEvent(Mouse.x, Mouse.y, button, action));
					} catch (CallbackStackInterruption e) {
						break;
					}
				}
			}
		});
		
		initialized = true;

	}

	public static void addCallback(Callback c) {
		Stack<Callback> tmpCallStack = new Stack<Callback>();
		
		if(!callbacks.empty()) {
			tmpCallStack.push(callbacks.pop());
		}

		while (!callbacks.empty() && tmpCallStack.peek().priority() < c.priority()) {
			tmpCallStack.push(callbacks.pop());
		}
		callbacks.push(c);
		while (!tmpCallStack.empty()) {
			callbacks.push(tmpCallStack.pop());
		}
	}

	public static float getX() {
		return x;
	}

	public static float getY() {
		return y;
	}

	public static float getDWheel() {
		float tmp = yoffset;
		yoffset = 0;
		return tmp;
	}

	public static boolean isButtonDown(int i) {

		return GLFW.glfwGetMouseButton(DisplayManager.window, GLFW.GLFW_MOUSE_BUTTON_1 + i) == 1;
	}

	public static float getXOffset() {
		return xoffset;
	}

	public static float getDY() {
		float tmp = dy;
		dy = 0;
		return tmp;
	}

	public static float getDX() {
		float tmp = dx;
		dx = 0;
		return tmp;
	}

	public static void cleanUp() {
		if (cursorCallback == null) {
			return;
		}
		initialized = false;
		// cursorCallback.free();
		// scrollCallback.free();
		// clickCallback.free();
	}

}
