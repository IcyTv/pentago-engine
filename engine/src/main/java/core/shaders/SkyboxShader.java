package core.shaders;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import core.entities.Camera;
import core.renderEngine.DisplayManager;
import tools.Maths;

public class SkyboxShader extends ShaderProgram{

	private static final String VERTEX_FILE = "skyboxVertexShader.glsl";
	private static final String FRAGMENT_FILE = "skyboxFragmentShader.glsl";
	
	private static final float ROTATION_SPEED = 0.5f;
	
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_fogColor;
	private int location_blendFactor;
	private int location_cubeMap;
	private int location_cubeMap2;
	
	private float rotation = 0;
	
	public SkyboxShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	public void loadFogColor(float r, float g, float b) {
		super.loadVector(location_fogColor, new Vector3f(r, g, b));
	}
	
	public void loadBlendFactor(float factor) {
		super.loadFloat(location_blendFactor, factor);
	}
	
	public void connectTextureUnits(){
		super.loadInt(location_cubeMap, 0);
		super.loadInt(location_cubeMap2, 1);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(location_projectionMatrix, matrix);
	}

	public void loadViewMatrix(Camera camera){
		Matrix4f matrix = Maths.createViewMatrix(camera);
		matrix._m30(0);
		matrix._m31(0);
		matrix._m32(0);
		rotation += ROTATION_SPEED * DisplayManager.getFrameTimeSeconds();
		matrix.rotate((float) Math.toRadians(rotation), new Vector3f(0, 1, 0), matrix);
		super.loadMatrix(location_viewMatrix, matrix);
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_fogColor = super.getUniformLocation("fogColor");
		location_blendFactor = super.getUniformLocation("blendFactor");
		location_cubeMap = super.getUniformLocation("cubeMap");
		location_cubeMap2 = super.getUniformLocation("cubeMap2");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
}