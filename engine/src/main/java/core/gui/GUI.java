package core.gui;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

import core.font.Font;
import core.font.GUIText;

public class GUI {

	private List<GUIImage> images;
	private List<GUIText> texts;

	private Vector2f position;
	private Vector2f scale;

	public GUI(Vector2f position, Vector2f scale) {
		images = new ArrayList<GUIImage>();
		texts = new ArrayList<GUIText>();

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
	}

	public void addText(String text, Font font, Vector2f position, int maxLineLength) {
		Vector2f tmpPos = position.add(this.position);
		texts.add(new GUIText(text, font, tmpPos, maxLineLength));
	}

	public List<GUIImage> getImages() {
		return images;
	}

	public List<GUIText> getTexts() {
		return texts;
	}

}