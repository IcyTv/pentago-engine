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
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import core.Constants;
import core.models.ModelData;
import core.models.RawModel;
import core.textures.TextureData;
//import de.matthiasmann.twl.utils.PNGDecoder;
//import de.matthiasmann.twl.utils.PNGDecoder.Format;

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
		//GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_2D, textureID);
		logger.info("Data");
		TextureData data = decodeTextureFile("res/" + file + ".png");
		
		ByteBuffer byteBuffer = data.getBuffer();
		byteBuffer.rewind();
		byte[] buffer = new byte[byteBuffer.capacity()];
		int n = 0;
		while (n < byteBuffer.capacity()) {
		  buffer[n] = byteBuffer.get(n + 0);
		  buffer[n + 1] = byteBuffer.get(n + 1);
		  buffer[n + 2] = byteBuffer.get(n + 2);
		  buffer[n + 3] = byteBuffer.get(n + 3);
		  n += 4;
		}
		byteBuffer.rewind();
		BufferedImage img = new BufferedImage(data.getWidth(), data.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		img.getRaster().setDataElements(0, 0, data.getWidth(), data.getHeight(), buffer);
		try {
			ImageIO.write(img, "PNG", new File("test.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.info("Parameters");
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.7f);


		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL13.GL_TEXTURE_BASE_LEVEL, 0);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL13.GL_TEXTURE_MAX_LEVEL, 0);
		
        //Setup wrap mode
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        
		logger.info("Connect data to texture");
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
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
		File f = new File(fileName);
		
		int width = 0;
		int height = 0;
		ByteBuffer imageBuffer = null;
		
		try {
			if(!f.exists()) throw new IOException();
			try(MemoryStack stack = MemoryStack.stackPush()){
				IntBuffer w = stack.mallocInt(1);
				IntBuffer h = stack.mallocInt(1);
				IntBuffer comp = stack.mallocInt(1);
				
				imageBuffer = STBImage.stbi_load(f.toString(), w, h, comp, 4);
				
				if(imageBuffer == null) throw new IOException();
				
				width = w.get();
				height = h.get();		
			}
		} catch(IOException e) {
			Constants.logger.severe("Texture " + fileName + " does not exist");
			Constants.logger.severe(STBImage.stbi_failure_reason());
		}
		return new TextureData(imageBuffer, width, height);
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
