package core.audio;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.EXTEfx;
import org.lwjgl.openal.ALCCapabilities;

public class AudioMaster {

	private static List<Integer> buffers;
	private static long device;
	
	public static void init() {
		buffers = new ArrayList<Integer>();
		try {
			device = ALC10.alcOpenDevice((ByteBuffer)null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ALCCapabilities deviceCaps = ALC.createCapabilities(device);
		IntBuffer contextAttribList = BufferUtils.createIntBuffer(16);
		contextAttribList.put(ALC10.ALC_REFRESH);
		contextAttribList.put(60);
		
		contextAttribList.put(ALC10.ALC_SYNC);
		contextAttribList.put(AL10.AL_FALSE);
		
		contextAttribList.put(EXTEfx.ALC_MAX_AUXILIARY_SENDS);
		contextAttribList.put(2);
		
		contextAttribList.put(0);
		contextAttribList.flip();
		
		long newContext= ALC10.alcCreateContext(device, contextAttribList);
		if(!ALC10.alcMakeContextCurrent(newContext)) {
			throw new RuntimeException("Failed to make context current!");
		}
		
		AL.createCapabilities(deviceCaps);
	}
	
	public static void setListenerData(float x, float y, float z) {
		AL10.alListener3f(AL10.AL_POSITION, x,y,z);
		AL10.alListener3f(AL10.AL_VELOCITY, 0,0,0);
	}
	
	public static int loadSound(String file){
		
	    FileInputStream fin = null;
	    //WaveData waveFile = null;
	    try {
	      fin = new FileInputStream("res/audio/" + file + ".wav");
	      //waveFile = WaveData.create(new BufferedInputStream(fin));
	    } catch (java.io.FileNotFoundException ex) {
	      ex.printStackTrace();
	    }
	    finally {
	      if(fin != null) {
	        try{ fin.close(); }catch(java.io.IOException ex){}
	      }
	    }
		int buffer = AL10.alGenBuffers();
		buffers.add(buffer);
		//AL10.alBufferData(buffer, waveFile.format, waveFile.data, waveFile.samplerate);
		//waveFile.dispose();
		return buffer;
	}
	
	public static void cleanUp() {
		for(int buffer: buffers) {
			AL10.alDeleteBuffers(buffer);
		}
		ALC.destroy();
	}
	
	/**
	 * 
	 * 0: Exponent distance
	 * 1: Inverse distance
	 * 2: Linear distance
	 * 
	 * @param method
	 * @param clamped
	 */
	
	public static void setDistanceAttenuationMethod(int method, boolean clamped) {
		switch(method) {
			case 0:
				AL10.alDistanceModel(clamped ? AL11.AL_EXPONENT_DISTANCE_CLAMPED : AL11.AL_EXPONENT_DISTANCE);
				break;
			case 1:
				AL10.alDistanceModel(clamped ? AL10.AL_INVERSE_DISTANCE_CLAMPED : AL10.AL_INVERSE_DISTANCE);
				break;
			case 2:
				AL10.alDistanceModel(clamped ? AL11.AL_LINEAR_DISTANCE_CLAMPED : AL11.AL_LINEAR_DISTANCE);
				break;
			default:
				AL10.alDistanceModel(clamped ? AL11.AL_EXPONENT_DISTANCE_CLAMPED : AL11.AL_EXPONENT_DISTANCE);
				break;
		}
	}
	
}
