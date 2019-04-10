package core.renderEngine;

import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import core.audio.AudioMaster;
import core.inputs.Mouse;

public class DisplayManager {

	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	public static final int TICKRATE = 128;

	public static long window = NULL;

	private static long lastFrameTime;
	private static float delta;

	private static boolean clean = true;

	public static void createDisplay() {
		if(window != NULL) {
			return;
		}
		
		clean = false;
		if (!GLFW.glfwInit()) {
			// Throw an error.
			throw new RuntimeException("GLFW initialization failed!");
		}
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 5);
		GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);
		window = GLFW.glfwCreateWindow(WIDTH, HEIGHT, "Pentago", NULL, NULL);
		if (window == NULL) {
			PointerBuffer description = PointerBuffer.allocateDirect(1000);
			GLFW.glfwGetError(description);
			GLFW.glfwTerminate();
			System.err.println(description.get());
			System.err.println("Window failed to load");
			System.exit(-1);
		}
		GLFW.glfwSetWindowTitle(window, "Pentago");

		// GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		GLFW.glfwSetWindowPos(window, 100, 100);
		GLFW.glfwMakeContextCurrent(window);
		GLFW.glfwShowWindow(window);
		GL.createCapabilities();
	}

	public static void updateDisplay() {
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
		GLFW.glfwPollEvents();
		GLFW.glfwSwapBuffers(window);
	}
	
	public static void sync(float starttime) {
		float endtime = starttime + 1.0f / TICKRATE;
		while (getCurrentTime() < endtime) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}
	}

	public static boolean closeRequested() {
		return GLFW.glfwWindowShouldClose(window);
	}

	public static float getFrameTimeSeconds() {
		return delta;
	}

	public static void closeDisplay() {
		if (clean)
			return;
		AudioMaster.cleanUp();
		GLFW.glfwDestroyWindow(window);
		GLFW.glfwTerminate();
		clean = true;
		Mouse.cleanUp();
		// GL.setCapabilities(null);
	}

	public static long getCurrentTime() {
		return (long) (GLFW.glfwGetTime() * 1000);
	}
	
}
