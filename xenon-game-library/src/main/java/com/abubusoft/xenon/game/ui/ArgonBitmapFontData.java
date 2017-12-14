package com.abubusoft.xenon.game.ui;

import org.abubu.argon.texture.Texture;

import android.util.SparseArray;

public class ArgonBitmapFontData {
	/** An array of the image paths, i.e. for multiple texture pages */
	public Texture[] imageTextures;
	public boolean flipped;
	public float lineHeight;
	public float capHeight = 1;
	public float ascent;
	public float descent;
	public float down;
	public float scaleX = 1, scaleY = 1;

	public SparseArray<ArgonGlyph> glyphs;
	public float spaceWidth;
	public float xHeight = 1;

	public void setGlyph(int ch, ArgonGlyph glyph) {
		glyphs.put(ch, glyph);
	}

	public ArgonGlyph getFirstGlyph() {
		return glyphs.valueAt(0);
	}

	public ArgonGlyph getGlyph(char ch) {

		return glyphs.get(ch);
	}
}
