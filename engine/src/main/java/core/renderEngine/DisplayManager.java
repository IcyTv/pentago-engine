package core.renderEngine;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import core.inputs.Mouse;

import static org.lwjgl.system.MemoryUtil.NULL;

import java.util.logging.Logger;

public class DisplayManager {

	public static Logger logger = Logger.getLogger("Debug");
	
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	private static final int FPS_CAP = 120;
	
	public static long window = NULL;
	
	private static long lastFrameTime;
	private static float delta;
	
	public static void createDisplay() {
		if(!GLFW.glfwInit()) {
			// Throw an error.
			System.err.println("GLFW initialization failed!");
		}
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 5);
		window = GLFW.glfwCreateWindow(WIDTH, HEIGHT, "Pentago", NULL, NULL);
		if(window == NULL) {
			GLFW.glfwTerminate();
		}
		
		GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		GLFW.glfwSetWindowPos(window, 100, 100);
		GLFW.glfwMakeContextCurrent(window);
		GLFW.glfwShowWindow(window);
		GL.createCapabilities();
	}
	
	public static void updateDisplay() {
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime)/1000f;
		lastFrameTime = currentFrameTime;
		GLFW.glfwPollEvents();
		GLFW.glfwSwapBuffers(window);
	}
	
	public static boolean closeRequested() {
		return GLFW.glfwWindowShouldClose(window);
	}
	
	public static float getFrameTimeSeconds() {
		return delta;
	}
	
	public static void closeDisplay() {
		logger.info("Closing window");
		//GLFW.glfwDestroyWindow(window);
		GLFW.glfwTerminate();
		//Mouse.cleanUp();
		//GL.setCapabilities(null);
	}
	
	public static long getCurrentTime() {
		return (long) (GLFW.glfwGetTime()*1000);
	}
}
