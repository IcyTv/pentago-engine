package testing;

import core.audio.*;

public class AudioTest {

	public static void main(String[] args) throws Exception {
		//System.setProperty("org.lwjgl.util.Debug", "true");
		AudioMaster.init();
		AudioMaster.setListenerData(0, 0, 0);
		
		int buffer = AudioMaster.loadSound("bounce");
		Source source = new Source();
		source.setLooping(true);
		source.play(buffer);
		
		float xPos = -8;
		int mult = 1;
		source.setPosition(xPos, 0, 1);
		
		char c = ' ';
		while(c != 'q') {
				xPos += 0.03 * mult;
				if(xPos >= 8 || xPos <= -8) {
					mult *= -1;
				}
				source.setPosition(xPos, 0, 1);
				Thread.sleep(10);
		}
	
		source.delete();
		AudioMaster.cleanUp();
	}
	
}
