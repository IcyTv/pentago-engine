package core.audio;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.EXTEfx;
import org.lwjgl.stb.STBVorbis;

public class AudioMaster {

	private static List<Integer> buffers;
	private static long device;
	private static boolean clean = true;

	private static boolean initialized = false;

	public static void init() {
		if(initialized) {
			return;
		}
		clean = false;
		buffers = new ArrayList<Integer>();
		try {
			device = ALC10.alcOpenDevice((ByteBuffer) null);
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

		long newContext = ALC10.alcCreateContext(device, contextAttribList);
		if (!ALC10.alcMakeContextCurrent(newContext)) {
			throw new RuntimeException("Failed to make AL context current!");
		}

		AL.createCapabilities(deviceCaps);
		
		initialized = true;
	}

	public static void setListenerDataPos(float x, float y, float z) {
		AL10.alListener3f(AL10.AL_POSITION, x, y, z);
		AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
	}

	public static void setListenerDataRot(float x, float y, float z) {
		AL10.alListener3f(AL10.AL_ORIENTATION, x, y, z);
	}

	public static int loadSound(String file) {
		IntBuffer channels = BufferUtils.createIntBuffer(1);
		IntBuffer sampleRate = BufferUtils.createIntBuffer(1);
		ShortBuffer songBuffer = STBVorbis.stb_vorbis_decode_filename(file, channels, sampleRate);

		if (songBuffer == null)
			throw new RuntimeException("Song " + file + " not found");

		int buffer = AL10.alGenBuffers();
		buffers.add(buffer);
		AL10.alBufferData(buffer, AL10.AL_FORMAT_MONO16, songBuffer, sampleRate.get());
		return buffer;
	}

	public static void cleanUp() {
		if (clean)
			return;
		for (int buffer : buffers) {
			AL10.alDeleteBuffers(buffer);
		}
		ALC10.alcDestroyContext(ALC10.alcGetCurrentContext());
		ALC10.alcCloseDevice(device);
		ALC.destroy();
		clean = true;
		initialized = false;
	}

	/**
	 * 
	 * 0: Exponent distance 1: Inverse distance 2: Linear distance
	 * 
	 * @param method
	 * @param clamped
	 */

	public static void setDistanceAttenuationMethod(int method, boolean clamped) {
		switch (method) {
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
