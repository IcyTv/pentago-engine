package core;

public abstract class Constants {

	public static enum STATE{
		GAME,
		MENU,
		CUTSCENE
	}
	
	public static enum COLOR{
		RED,
		GREEN,
		BLUE,
		PURPLE
	}
	
	public static STATE state = STATE.MENU;
	
}
