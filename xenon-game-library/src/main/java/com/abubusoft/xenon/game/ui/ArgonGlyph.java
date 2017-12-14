package com.abubusoft.xenon.game.ui;

public class ArgonGlyph {
	public int id;
	public int srcX;
	public int srcY;
	public int width, height;
	public float u, v, u2, v2;
	public int xoffset, yoffset;
	public int xadvance;
	public byte[][] kerning;

	/** The index to the texture page that holds this glyph. */
	public int page = 0;

	public int getKerning(char ch) {
		if (kerning != null) {
			byte[] page = kerning[ch >>> BitmapFontManager.LOG2_PAGE_SIZE];
			if (page != null)
				return page[ch & BitmapFontManager.PAGE_SIZE - 1];
		}
		return 0;
	}

	public void setKerning(int ch, int value) {
		if (kerning == null)
			kerning = new byte[BitmapFontManager.PAGES][];
		byte[] page = kerning[ch >>> BitmapFontManager.LOG2_PAGE_SIZE];
		if (page == null)
			kerning[ch >>> BitmapFontManager.LOG2_PAGE_SIZE] = page = new byte[BitmapFontManager.PAGE_SIZE];
		page[ch & BitmapFontManager.PAGE_SIZE - 1] = (byte) value;
	}
}