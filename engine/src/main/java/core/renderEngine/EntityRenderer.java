package core.renderEngine;

import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import core.entities.Entity;
import core.models.RawModel;
import core.models.TexturedModel;
import core.shaders.StaticShader;
import core.textures.ModelTexture;
import tools.Maths;

public class EntityRenderer {

	private StaticShader shader;

	public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	public void render(Map<TexturedModel, List<Entity>> entities) {
		for (TexturedModel model : entities.keySet()) {
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for (Entity entity : batch) {
				if (!entity.isHidden()) {
					prepareInstance(entity);
					GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT,
							0);
				}
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
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.getTexture().getID());
		shader.loadTexture(0);
	}

	private void unbindTexturedModel() {
		// MasterRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	private void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(),
				entity.getRotY(), entity.getRotZ(), entity.getScale());

		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadOffset(entity.getXOffset(), entity.getYOffset());
	}

	public void loadFog(float density, float gradient) {
		shader.start();
		shader.loadFog(density, gradient);
		shader.stop();
	}
}
