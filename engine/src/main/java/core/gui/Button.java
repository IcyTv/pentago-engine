package core.gui;

import org.joml.Vector2f;

import core.event.ButtonEvent;
import core.event.Event;
import core.font.Font;

public class Button extends GUIElement {

	public Button(String text, float x, float y, float w, float h) {
		super(x, y, w, h, false);

		displayElement = new GUI(new Vector2f(ox, oy), new Vector2f(ow, oh));
		System.out.println(ow);
		displayElement.addText(text, new Font("fonts/segoeUI", 3f, false), new Vector2f(ox, oy), ow);
	}

	public boolean clicked(float mouseX, float mouseY) {
		return mouseX > x && mouseX < x + w && mouseY > y && mouseY < y + h;
	}

	@Override
	public void onClick(Event e) {
		ButtonEvent be = (ButtonEvent) e;
		
		System.out.println("Button Pressed");
		
	}

	
}