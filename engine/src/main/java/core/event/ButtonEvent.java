package core.event;

public class ButtonEvent implements Event {

	private boolean down;
	
	public ButtonEvent(boolean down) {
		this.down = down;
	}
	
	
	
	public boolean isDown() {
		return down;
	}



	public void setDown(boolean down) {
		this.down = down;
	}



	@Override
	public String eventType() {
		// TODO Auto-generated method stub
		return "ButtonEvent";
	}

}
