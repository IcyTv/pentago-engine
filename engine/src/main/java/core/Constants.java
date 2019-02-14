package core;

import java.util.logging.Logger;

public abstract class Constants {

	public static Logger logger = Logger.getLogger("Debug");
	
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
