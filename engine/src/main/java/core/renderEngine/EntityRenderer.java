package core.renderEngine;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.joml.Matrix4f;

import core.entities.Entity;
import core.models.RawModel;
import core.models.TexturedModel;
import core.shaders.StaticShader;
import core.textures.ModelTexture;
import tools.Maths;

public class EntityRenderer {

	private StaticShader shader;
	public static Logger logger = Logger.getLogger("Debug");
	
	public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(Map<TexturedModel, List<Entity>> entities) {
		logger.info("Rendering entities length: " + entities.keySet().size());
		for(TexturedModel model: entities.keySet()) {
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for(Entity entity: batch) {
				logger.info("Preparing entity");
				prepareInstance(entity);
				logger.info("Drawing Entity");
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
	}
	
	private void prepareTexturedModel(TexturedModel texturedModel) {
		RawModel model = texturedModel.getRawModel();
		GL30.glBindVertexArray(model.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		ModelTexture texture = texturedModel.getTexture();
		shader.loadNumberOfRows(texturedModel.getTexture().getRows());
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		
		shader.loadFakeLighting(texture.usingFakeLighting());
		
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		logger.info("Texture id: " + texture.getID());
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.getTexture().getID());
		shader.loadTexture(0);
	}
	
	private void unbindTexturedModel() {
		//MasterRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	
	private void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), 
				entity.getRotX(),
				entity.getRotY(),
				entity.getRotZ(),
				entity.getScale());
		
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadOffset(entity.getXOffset(), entity.getYOffset());
	}
	
	public void loadFog(float density, float gradient) {
		shader.start();
		shader.loadFog(density, gradient);
		shader.stop();
	}
}
