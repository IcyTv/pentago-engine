package core.loaders;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
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
		TextureData data = decodeImage("res/" + file + ".png");
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
			TextureData data = decodeImage("res/" + folder + textureFiles[i] + ".png");
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
	
	private static TextureData decodeImage(String filename) {
		BufferedImage bufferedImage;
		try {
			FileInputStream in = new FileInputStream(filename);
			bufferedImage = ImageIO.read(in);
		} catch (IOException e) {
			logger.severe("Image " + filename + " could not load");
			return null;
		}
		
		ByteBuffer imageBuffer;
	    WritableRaster raster;
	    BufferedImage texImage;

	    ColorModel glAlphaColorModel = new ComponentColorModel(ColorSpace
	            .getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8, 8 },
	            true, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
	    

	    raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
	            bufferedImage.getWidth(), bufferedImage.getHeight(), 4, null);
	    texImage = new BufferedImage(glAlphaColorModel, raster, true,
	            new Hashtable());

	    // copy the source image into the produced image
	    Graphics g = texImage.getGraphics();
	    g.setColor(new Color(0f, 0f, 0f, 0f));
	    g.fillRect(0, 0, 256, 256);
	    g.drawImage(bufferedImage, 0, 0, null);

	    // build a byte buffer from the temporary image
	    // that be used by OpenGL to produce a texture.
	    byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer())
	            .getData();

	    imageBuffer = ByteBuffer.allocateDirect(data.length);
	    imageBuffer.order(ByteOrder.nativeOrder());
	    imageBuffer.put(data, 0, data.length);
	    imageBuffer.flip();
	    
	    //imageBuffer = ByteBuffer.wrap(data);
	    imageBuffer.flip();
	    
	    return new TextureData(imageBuffer, bufferedImage.getWidth(), bufferedImage.getHeight());
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
