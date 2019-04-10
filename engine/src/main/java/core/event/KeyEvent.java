package core.event;

public class KeyEvent implements Event {
	private int key;
	private int action;
	private int mods;

	public KeyEvent() {
	}

	public KeyEvent(int key, int action, int mods) {
		this.key = key;
		this.action = action;
		this.mods = mods;
	}

	public int getKey() {
		return this.key;
	}

	public int getAction() {
		return this.action;
	}

	public int getMods() {
		return this.mods;
	}

	@Override
	public String eventType() {
		return "KeyEvent";
	}

}