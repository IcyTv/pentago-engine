package core.shaders;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import core.entities.Camera;
import core.entities.Light;
import tools.Maths;

public class StaticShader extends ShaderProgram{

	private static final int MAX_LIGHTS = 4;
	
	private static final String VERTEX_FILE = "vertexShader.glsl";
	private static final String FRAGMENT_FILE = "fragmentShader.glsl";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;

	private int location_lightPos[];
	private int location_lightColor[];
	private int location_attenuation[];

	private int location_shineDamper;
	private int location_reflectivity;

	private int location_useFakeLighting;

	private int location_skyColor;
	private int location_density;
	private int location_gradient;
	
	private int location_numOfRows;
	private int location_offset;

	private int location_plane;
	
	private int location_sampler;
	
	private int location_color;
	
	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		
		location_skyColor = super.getUniformLocation("skyColor");
		location_density = super.getUniformLocation("density");
		location_gradient = super.getUniformLocation("gradient");

		location_numOfRows = super.getUniformLocation("numOfRows");
		location_offset = super.getUniformLocation("offset");
		
		location_plane = super.getUniformLocation("plane");
		
		
		location_lightPos = new int[MAX_LIGHTS];
		location_lightColor = new int[MAX_LIGHTS];
		location_attenuation = new int[MAX_LIGHTS];
		
		location_sampler = super.getUniformLocation("textureSampler");
		
		for(int i = 0; i < MAX_LIGHTS; i++) {
			location_lightPos[i] = super.getUniformLocation("lightPos[" + i + "]");
			location_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
			location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}
		
		location_color = super.getUniformLocation("color");
	}
	
	public void loadClipPlane(Vector4f plane) {
		super.loadVector(location_plane, plane);
	}
	
	public void loadNumberOfRows(int numOfRows) {
		super.loadFloat(location_numOfRows, numOfRows);
	}
	
	public void loadOffset(float x, float y) {
		super.loadVector(location_offset, new Vector2f(x, y));
	}
	
	public void loadFog(float density, float gradient) {
		super.loadFloat(location_density, density);
		super.loadFloat(location_gradient, gradient);
	}
	
	public void loadSkyColor(float r, float g, float b) {
		super.loadVector(location_skyColor, new Vector3f(r, g, b));
	}
	
	public void loadFakeLighting(boolean useFake) {
		super.loadBoolean(location_useFakeLighting, useFake);
	}

	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadShineVariables(float damper, float reflectivity) {
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	
	public void loadLight(Light light, int index) {
		super.loadVector(location_lightPos[index], light.getPosition());
		super.loadVector(location_lightColor[index], light.getColor());
	}
	
	public void loadLights(List<Light> lights) {
		for(int j = 0; j < MAX_LIGHTS; j++) {
			if(j < lights.size()) {
				super.loadVector(location_lightPos[j], lights.get(j).getPosition());
				super.loadVector(location_lightColor[j], lights.get(j).getColor());
				super.loadVector(location_attenuation[j], lights.get(j).getAttenuation());
			} else {
				super.loadVector(location_lightPos[j], new Vector3f(0, 0, 0));
				super.loadVector(location_lightColor[j], new Vector3f(0, 0, 0));
				super.loadVector(location_attenuation[j], new Vector3f(1, 0, 0));
			}
		}
	}
	
	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	
	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}

	public void loadTexture(int i) {
		super.loadInt(location_sampler, i);		
	}
	
	public void loadColor(Vector3f c) {
		super.loadVector(location_color, c);
	}
}