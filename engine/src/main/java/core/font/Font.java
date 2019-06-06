package core.font;

import java.io.File;

import core.loaders.Loader;

public class Font {
	private FontType fontType;
	private float fontSize;
	private boolean centered;

	public Font(FontType fontType, float fontSize, boolean centered) {
		this.fontType = fontType;
		this.fontSize = fontSize;
		this.centered = centered;
	}

	public Font(String path, float fontSize, boolean centered) {
		fontType = new FontType(Loader.loadTexture(path), new File("res/" + path + ".fnt"));
		this.fontSize = fontSize;
		this.centered = centered;
	}

	public FontType getFontType() {
		return this.fontType;
	}

	public void setFontType(FontType fontType) {
		this.fontType = fontType;
	}

	public float getFontSize() {
		return this.fontSize;
	}

	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
	}

	public boolean isCentered() {
		return this.centered;
	}

	public boolean getCentered() {
		return this.centered;
	}

	public void setCentered(boolean centered) {
		this.centered = centered;
	}

}