package core.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector2f;

import core.font.Font;
import core.font.FontType;
import core.font.GUIText;
import core.font.TextMeshData;
import core.loaders.Loader;

public class GUI {

	private List<GUIImage> images;
	private List<GUIText> texts;
	private Map<FontType, List<GUIText>> renderMap;
	
	private Vector2f position;
	private Vector2f scale;

	public GUI(Vector2f position, Vector2f scale) {
		images = new ArrayList<GUIImage>();
		texts = new ArrayList<GUIText>();
		renderMap = new HashMap<FontType, List<GUIText>>();

		this.position = position;
		this.scale = scale;
	}

	public void addImage(GUIImage image) {
		images.add(image);
	}

	public void addImage(String texturePath, Vector2f position, Vector2f scale) {
		Vector2f tmpPos = position.add(this.position);
		Vector2f tmpScale = scale.mul(this.scale);
		images.add(new GUIImage(texturePath, tmpPos, tmpScale));
	}

	public void addText(GUIText text) {
		texts.add(text);
		loadFont(text);
	}

	public void addText(String text, Font font, Vector2f position, float ow) {
		Vector2f tmpPos = position.add(this.position);
		GUIText tmp = new GUIText(text, font, tmpPos, ow);
		//tmp.setColor(1, 1, 1);
		texts.add(tmp);
		loadFont(tmp);
	
	}
	
	private void loadFont(GUIText text) {
		FontType font = text.getFont();
		TextMeshData data = font.loadText(text);
		int vao = Loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
		text.setMeshInfo(vao, data.getVertexCount());
		List<GUIText> textBatch = renderMap.get(font);
		if(textBatch == null){
			textBatch = new ArrayList<GUIText>();
			renderMap.put(font, textBatch);
		}
		textBatch.add(text);
	}
	
	public Map<FontType, List<GUIText>> getRenderMap() {
		return renderMap;
	}

	public List<GUIImage> getImages() {
		return images;
	}

	public List<GUIText> getTexts() {
		return texts;
	}

}