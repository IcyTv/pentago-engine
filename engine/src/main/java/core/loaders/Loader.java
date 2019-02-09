package core.loaders;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import core.models.ModelData;
import core.models.RawModel;
import core.textures.TextureData;

public abstract class Loader {
	
	public static Logger logger = Logger.getLogger("Debug");

	//Vertex Array Objects for later use
	private static List<Integer> vaos = new ArrayList<Integer>();
	//Vertex Buffer Objects for later use
	private static List<Integer> vbos = new ArrayList<Integer>();
	//Texture indices for later use
	private static List<Integer> textures = new ArrayList<Integer>();
	
	
	/**
	 * Function to load Models into VAO
	 * 
	 * @param positions Position of verticies
	 * @param textures  Texture Coordinates
	 * @param normals   Normal vectors
	 * @param indices   
	 * @return raw model with data
	 */
	public static RawModel loadToVAO(float[] positions, float[] textures, float[] normals, int[] indices) {
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textures);
		storeDataInAttributeList(2, 3, normals);
		unbindVAO();
		return new RawModel(vaoID, indices.length);
	}
	
	public static RawModel loadToVAO(float[] positions, int dimensions) {
		int vaoID = createVAO();
		storeDataInAttributeList(0, dimensions, positions);
		unbindVAO();
		return new RawModel(vaoID, positions.length / dimensions);
	}
	

	public static int loadTexture(String file) {
		logger.info("Loading texture");
		int textureID = GL11.glGenTextures();
		logger.info("textureId: " + textureID);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		logger.info("Data");
		TextureData data = decodeTextureFile("res/" + file + ".png");
		logger.info("Connect data to texture");
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		logger.info("Parameters");
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.7f);
		textures.add(textureID);
		logger.info("Done loading");
		return textureID;
	}
	

	public static int loadCubeMap(String[] textureFiles, String folder) {
		int textureID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureID);
		
		if(!folder.equals("")) {
			folder += "/";
		}
		
		for(int i = 0; i < textureFiles.length; i++) {
			TextureData data = decodeTextureFile("res/" + folder + textureFiles[i] + ".png");
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
					0, GL11.GL_RGBA, data.getWidth(), 
					data.getHeight(), 0, GL11.GL_RGBA, 
					GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		}
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		textures.add(textureID);
		return textureID;
	}
	
	private static TextureData decodeTextureFile(String fileName) {
		int width = 0;
		int height = 0;
		ByteBuffer byteBuffer = null;
		try {
			FileInputStream in = new FileInputStream(fileName);
			BufferedImage decoder = ImageIO.read(in);
			width = decoder.getWidth();
			height = decoder.getHeight();
			byteBuffer = ByteBuffer.allocateDirect(4 * width * height);
			DataBuffer dataBuffer = decoder.getData().getDataBuffer();
			if (dataBuffer instanceof DataBufferByte) {
			    byte[] pixelData = ((DataBufferByte) dataBuffer).getData();
			    byteBuffer = ByteBuffer.wrap(pixelData);
			}
			else if (dataBuffer instanceof DataBufferUShort) {
			    short[] pixelData = ((DataBufferUShort) dataBuffer).getData();
			    byteBuffer = ByteBuffer.allocate(pixelData.length * 2);
			    byteBuffer.asShortBuffer().put(ShortBuffer.wrap(pixelData));
			}
			else if (dataBuffer instanceof DataBufferShort) {
			    short[] pixelData = ((DataBufferShort) dataBuffer).getData();
			    byteBuffer = ByteBuffer.allocate(pixelData.length * 2);
			    byteBuffer.asShortBuffer().put(ShortBuffer.wrap(pixelData));
			}
			else if (dataBuffer instanceof DataBufferInt) {
			    int[] pixelData = ((DataBufferInt) dataBuffer).getData();
			    byteBuffer = ByteBuffer.allocate(pixelData.length * 4);
			    byteBuffer.asIntBuffer().put(IntBuffer.wrap(pixelData));
			}
			else {
			    throw new IllegalArgumentException("Not implemented for data buffer type: " + dataBuffer.getClass());
			}
			//decoder.decode(byteBuffer, width * 4, Format.RGBA);
			byteBuffer.flip();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.severe("Tried to load texture " + fileName + ", didn't work");
			System.exit(-1);
		}
		return new TextureData(byteBuffer, width, height);
	}
	
	private static int createVAO() {
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}
	
	private static void unbindVAO() {
		GL30.glBindVertexArray(0);
	}
	
	private static void storeDataInAttributeList(int attributeNumber, int coordSize, float[] data) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	private static void bindIndicesBuffer(int[] indicies) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indicies);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	private static IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	private static FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	public static RawModel loadToVAO(ModelData modelData) {
		return loadToVAO(modelData.getVertices(), modelData.getTextureCoords(), modelData.getNormals(), modelData.getIndices());
	}
	
	public static void cleanUp() {
		for(int vao: vaos) {
			GL30.glDeleteVertexArrays(vao);
		}
		for(int vbo: vbos) {
			GL15.glDeleteBuffers(vbo);
		}
		for(int texture: textures) {
			GL11.glDeleteTextures(texture);
		}
	}
	
}
