/**
 * 
 */
package com.abubusoft.xenon.game.ui;


/**
 * <p>
 * Supporta una pagina unica di caratteri
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class ArgonBitmapFont {

	public String name;

	// public AtlasTexture atlas;

	public ArgonBitmapFontData data;

	/**
	 * <p>
	 * opzioni della font
	 * </p>
	 */
	public BitmapFontOptions options;

	public int textLenght;

	public static class TextBounds {
		public float width;
		public float height;

		public TextBounds() {
		}

		public TextBounds(TextBounds bounds) {
			set(bounds);
		}

		public void set(TextBounds bounds) {
			width = bounds.width;
			height = bounds.height;
		}
	}

	static public enum HAlignment {
		LEFT, CENTER, RIGHT
	}

	static boolean isWhitespace(char c) {
		switch (c) {
		case '\n':
		case '\r':
		case '\t':
		case ' ':
			return true;
		default:
			return false;
		}
	}

}
