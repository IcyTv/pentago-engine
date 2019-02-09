package core.renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.lwjgl.opengl.GL11;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import core.animation.Animation;
import core.animation.AnimationEntity;
import core.entities.Camera;
import core.entities.Entity;
import core.entities.Light;
import core.models.TexturedModel;
import core.shaders.StaticShader;
import testing.TestScene;
import tools.Maths;

public class MasterRenderer {
	
	public static Logger logger = Logger.getLogger("Debug");
	
	private static final float FOV = 70;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 1000;
	
	private static final float SKY_RED = Maths.rgbToFloat(12);
	private static final float SKY_GREEN = Maths.rgbToFloat(19);
	private static final float SKY_BLUE = Maths.rgbToFloat(23);
	
	private static float FOG_DENSITY = 0.003f;
	private static float FOG_GRADIENT = 3f;

	private Matrix4f projectionMatrix;
	
	private EntityRenderer renderer;
	private SkyboxRenderer skyboxRenderer;

	private StaticShader shader = new StaticShader();
	
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	
	private static boolean cullingOn = false;
	
	public MasterRenderer() {
		enableCulling();
		
		createProjectionMatrix();
		renderer = new EntityRenderer(shader, projectionMatrix);
		renderer.loadFog(FOG_DENSITY, FOG_GRADIENT);
		skyboxRenderer = new SkyboxRenderer(projectionMatrix);
	}
	
	public void playAnimation(Animation animation, List<Light> lights, Camera cam) {
		if(cam == null) {
			cam = new Camera();
		}
		List<Entity> entities = new ArrayList<Entity>();
		for(int i = 0; i < animation.getDuration(); i++) {
			Map<AnimationEntity, Vector3f> frame = animation.giveFrame();
			for(AnimationEntity ae: frame.keySet()) {
				if(ae.isCamera()) {
					cam.move(frame.get(ae));
				} else {
					Entity tmp = ae.getEntity();
					tmp.setPosition(frame.get(ae));
					entities.add(tmp);
				}
			}
		}
		
		renderScene(entities, lights, cam, null);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void playAnimation(Animation animation, List<Light> lights) {
		playAnimation(animation, lights, null);
	}
	
	public static void enableCulling() {
		if(!cullingOn) {
			cullingOn = true;
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glCullFace(GL11.GL_BACK);
		}
	}
	
	public static void disableCulling() {
		cullingOn = false;
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public void renderScene(List<Entity> entities, List<Light> lights, Camera camera, Vector4f clipPlane) {
		renderScene(entities, lights, camera, clipPlane, true);
	}
	
	public void renderScene(List<Entity> entities, List<Light> lights,
			Camera camera, Vector4f clipPlane, boolean render) {
		for(Entity e: entities) {
			processEntity(e);
		}
		if(render) {
			render(lights, camera, clipPlane);
		}
	}
	
	public void render(List<Light> lights, Camera camera, Vector4f clipPlane) {
		logger.info("Rendering");
		prepare();
		shader.start();
		if(clipPlane != null) {
			shader.loadClipPlane(clipPlane);
		}
		shader.loadLights(lights);
		shader.loadViewMatrix(camera);
		shader.loadSkyColor(SKY_RED, SKY_GREEN, SKY_BLUE);

		renderer.render(entities);

		shader.stop();
		entities.clear();

		skyboxRenderer.render(camera, SKY_RED, SKY_GREEN, SKY_BLUE);
	}
	
	public void processEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		
		if(batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}
	
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(SKY_RED, SKY_GREEN, SKY_BLUE, 1);
	}
	
	public void cleanUp() {
		shader.cleanUp();
	}
	
	private void createProjectionMatrix(){
        float aspectRatio = (float) DisplayManager.WIDTH / (float) DisplayManager.HEIGHT;
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV/2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;
        
        projectionMatrix = new Matrix4f();
        projectionMatrix._m00(x_scale);
        projectionMatrix._m11(y_scale);
        projectionMatrix._m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
        projectionMatrix._m23(-1);
        projectionMatrix._m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
        projectionMatrix._m33(0);
	}	
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
}
