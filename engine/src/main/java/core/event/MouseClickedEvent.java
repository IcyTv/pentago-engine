package core.event;

public class MouseClickedEvent implements Event {

	private float x;
	private float y;
	private int button;
	private int action;

	public MouseClickedEvent(float x, float y, int button, int action) {
		this.x = x;
		this.y = y;
		this.button = button;
		this.action = action;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public int getButton() {
		return this.button;
	}

	public int getAction() {
		return action;
	}
}