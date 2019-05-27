package core.gui;

import core.event.Event;
import core.renderEngine.DisplayManager;

public abstract class GUIElement {

	protected float x;
	protected float y;

	protected float w;
	protected float h;

	// OpenGL Display Coordinates
	protected float ox;
	protected float oy;
	protected float ow;
	protected float oh;

	protected GUI displayElement;

	public GUIElement(float x, float y, float width, float height) {
		this(x, y, width, height, false);
	}

	public GUIElement(float x, float y, float width, float height, boolean relative) {
		if (relative) {
			this.x = x * DisplayManager.WIDTH;
			this.y = y * DisplayManager.HEIGHT;

			this.w = width * DisplayManager.WIDTH;
			this.h = height * DisplayManager.HEIGHT;
		} else {
			this.x = x;
			this.y = y;
			
			this.w = width;
			this.h = height;
		}
		ox = this.x / DisplayManager.WIDTH;
		oy = this.y / DisplayManager.HEIGHT;
		ow = this.w / DisplayManager.WIDTH;
		oh = this.h / DisplayManager.HEIGHT;
	}

	public GUI getGuiElement() {
		return displayElement;
	}
	
	public abstract void onClick(Event e);

}