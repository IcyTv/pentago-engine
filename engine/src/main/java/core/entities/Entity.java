package core.entities;

import org.joml.Vector3f;

import core.Constants;
import core.audio.Source;
import core.loaders.Loader;
import core.loaders.OBJFileLoader;
import core.models.RawModel;
import core.models.TexturedModel;
import core.textures.ModelTexture;

public class Entity {

	private TexturedModel model;
	private Vector3f position;
	private float rotX, rotY, rotZ;
	private float scale;
	
	private Source source;
	
	private int textureIndex;
	
	public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this(model, 0, position, rotX, rotY, rotZ, scale);
	}
	
	public Entity(TexturedModel model, int textureIndex, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.textureIndex = textureIndex;
		source = null;
	}

	public Entity(String fileName, Vector3f position, float scale){
		
		ModelTexture texture = new ModelTexture(Loader.loadTexture("entities/" + fileName));
		RawModel model = Loader.loadToVAO(OBJFileLoader.loadOBJ("entities/" + fileName));
		this.model = new TexturedModel(model, texture);
		this.position = position;
		this.rotX = 0;
		this.rotY = 0;
		this.rotZ = 0;
		this.scale = scale;
		this.textureIndex = 0;
		source = null;
	}
	
	public void increasePosition(float dx, float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
		if(source != null) {
			source.setPosition(this.position.x, this.position.y, this.position.z);
			Constants.logger.info("Source: " + this.position.x + " " + this.position.y + " " + this.position.z);
		}
	}
	
	public void increaseRotation(float dx, float dy, float dz) {
		this.rotX += dx;
		this.rotY += dy;
		this.rotZ += dz;
	}
	
	public void setRotation(float dx, float dy, float dz) {
		this.rotX = dx;
		this.rotY = dy;
		this.rotZ = dz;
	}
	
	public void connectSource(Source source) {
		this.source = source;
		source.setPosition(this.position.x, this.position.y, this.position.z);
	}
	
	public Source getSource() {
		return source;
	}
	
	public float getXOffset() {
		int column = textureIndex % model.getTexture().getRows();
		return (float) column / (float) model.getTexture().getRows(); 
	}
	
	public float getYOffset() {
		int row = (int) Math.floor(textureIndex / model.getTexture().getRows());
		return (float) row / (float) model.getTexture().getRows();
	}
	
	public TexturedModel getModel() {
		return model;
	}

	public void setModel(TexturedModel model) {
		this.model = model;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;

		if(source != null) {
			source.setPosition(this.position.x, this.position.y, this.position.z);
			System.out.println("Source: " + this.position.x + " " + this.position.y + " " + this.position.z);
		}
	}

	public float getRotX() {
		return rotX;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}

	public float getRotZ() {
		return rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}
	
}
