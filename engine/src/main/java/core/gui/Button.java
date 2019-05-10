package core.gui;

import org.joml.Vector2f;

import core.font.Font;

public class Button extends GUIElement {

	public Button(String text, float x, float y, float w, float h) {
		super(x, y, w, h, false);

		displayElement = new GUI(new Vector2f(ox, oy), new Vector2f(ow, oh));
		displayElement.addText(text, new Font("fonts/segoeUI", 24, true), new Vector2f(x, y), 20);
	}

	public boolean clicked(float mouseX, float mouseY) {
		return mouseX > x && mouseX < x + w && mouseY > y && mouseY < y + h;
	}

}