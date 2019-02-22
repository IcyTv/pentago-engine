package core.inputs;

import java.util.logging.Logger;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import core.renderEngine.DisplayManager;

public class Mouse {

	public static Logger logger = Logger.getLogger("Debug");

	private static float x;
	private static float y;
	private static float dx;
	private static float dy;

	private static float xoffset;
	private static float yoffset;

	private static GLFWCursorPosCallback cursorCallback;
	private static GLFWScrollCallback scrollCallback;

	public static void init() {
		GLFW.glfwSetCursorPosCallback(DisplayManager.window, cursorCallback = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				logger.info("Moved Cursor");
				Mouse.dx = -(Mouse.x - (float) xpos);
				Mouse.dy = (Mouse.y - (float) ypos);

				Mouse.x = (float) xpos;
				Mouse.y = (float) ypos;
			}
		});

		GLFW.glfwSetScrollCallback(DisplayManager.window, scrollCallback = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				logger.info("Scrolled");
				Mouse.xoffset = (float) xoffset;
				Mouse.yoffset = (float) yoffset;
			}
		});
	}

	public static float getX(){
		return x;
	}

	public static float getY(){
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

	public static float getXOffset(){
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
		cursorCallback.free();
		scrollCallback.free();
	}

}
