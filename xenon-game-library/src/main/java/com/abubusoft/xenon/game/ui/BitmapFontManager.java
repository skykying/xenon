/**
 * 
 */
package com.abubusoft.xenon.game.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.abubu.argon.texture.Texture;
import org.abubu.argon.texture.TextureFilterType;
import org.abubu.argon.texture.TextureManager;
import org.abubu.argon.texture.TextureOptions;
import org.abubu.elio.ElioRuntimeException;
import org.abubu.elio.util.StreamUtility;

import android.content.Context;
import android.util.SparseArray;

/**
 * <p>
 * Gestore delle font bitmap. Si parte del presupposto che il manager opera su
 * uno schermo di dimensioni costanti per ogni bitmapFont. Questi parametri
 * servono a calcolare la posizione dei testi.
 * </p>
 * 
 * <p>
 * Prima di essere utilizzato, richiede l'uso del metodo <code>init</code>.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class BitmapFontManager {

	public static final int LOG2_PAGE_SIZE = 9;
	public static final int PAGE_SIZE = 1 << LOG2_PAGE_SIZE;
	public static final int PAGES = 0x10000 / PAGE_SIZE;

	public static final char[] xChars = { 'x', 'e', 'a', 'o', 'n', 's', 'r', 'c', 'u', 'm', 'v', 'w', 'z' };
	public static final char[] capChars = { 'M', 'N', 'B', 'D', 'C', 'E', 'F', 'K', 'A', 'G', 'H', 'I', 'J', 'L', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	private static final BitmapFontManager instance = new BitmapFontManager();

	public static final BitmapFontManager instance() {
		return instance;
	}

	/**
	 * nome della font di default
	 */
	private String defaultFontKey;

	private BitmapFontManager() {
		fonts = new HashMap<String, ArgonBitmapFont>();
	}

	/**
	 * <p>
	 * Crea 
	 * </p>
	 * 
	 * @param context
	 * @param fontId
	 * @param fileName
	 * @param options
	 * @return
	 */
	public ArgonBitmapFont createBitmapFont(Context context, String fontId, String fileName, BitmapFontOptions options) {
		TextureOptions textureOptions = TextureOptions.build().textureFilter(TextureFilterType.LINEAR);

		ArgonBitmapFont font = loadFont(context, fileName, textureOptions, false);
		font.name = fontId;
		font.options = options;
		// i vertici verranno modificati in seguito		

		fonts.put(fontId, font);

		if (defaultFontKey == null)
			defaultFontKey = fontId;

		return font;
	}

	/**
	 * @return
	 */
	public ArgonBitmapFont defaultFont() {
		return fonts.get(defaultFontKey);
	}

	public ArgonBitmapFont loadFont(Context context, String fontFileName, TextureOptions textureOptions, boolean flip) {
		ArgonBitmapFont font = new ArgonBitmapFont();
		ArgonBitmapFontData data = loadData(context, fontFileName, textureOptions, flip);

		font.data = data;

		ArgonGlyph glyph;
		for (int i = 0; i < data.glyphs.size(); i++) {
			glyph = data.glyphs.valueAt(i);
			if (glyph == null)
				continue;

			Texture region = data.imageTextures[glyph.page];

			if (region == null) {
				// TODO: support null regions by parsing scaleW / scaleH ?
				throw new IllegalArgumentException("BitmapFont texture region array cannot contain null elements");
			}

			float invTexWidth = 1.0f / region.info.dimension.width;
			float invTexHeight = 1.0f / region.info.dimension.height;

			float offsetX = 0, offsetY = 0;
			float u = 0;// region.u;
			float v = 0;// region.v;
			float regionWidth = region.info.dimension.width;
			float regionHeight = region.info.dimension.height;

			float x = glyph.srcX;
			float x2 = glyph.srcX + glyph.width;
			float y = glyph.srcY;
			float y2 = glyph.srcY + glyph.height;

			// Shift glyph for left and top edge stripped whitespace. Clip
			// glyph for right and bottom edge stripped whitespace.
			if (offsetX > 0) {
				x -= offsetX;
				if (x < 0) {
					glyph.width += x;
					glyph.xoffset -= x;
					x = 0;
				}
				x2 -= offsetX;
				if (x2 > regionWidth) {
					glyph.width -= x2 - regionWidth;
					x2 = regionWidth;
				}
			}
			if (offsetY > 0) {
				y -= offsetY;
				if (y < 0) {
					glyph.height += y;
					y = 0;
				}
				y2 -= offsetY;
				if (y2 > regionHeight) {
					float amount = y2 - regionHeight;
					glyph.height -= amount;
					glyph.yoffset += amount;
					y2 = regionHeight;
				}
			}

			glyph.u = u + x * invTexWidth;
			glyph.u2 = u + x2 * invTexWidth;
			if (data.flipped) {
				glyph.v = v + y * invTexHeight;
				glyph.v2 = v + y2 * invTexHeight;
			} else {
				glyph.v2 = v + y * invTexHeight;
				glyph.v = v + y2 * invTexHeight;
			}
		}

		return font;
	}

	public ArgonBitmapFontData loadData(Context context, String fontFileName, TextureOptions textureOptions, boolean flip) {
		ArgonBitmapFontData data = new ArgonBitmapFontData();
		data.flipped = flip;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(context.getAssets().open(fontFileName)), 512);
			reader.readLine(); // info

			String line = reader.readLine();
			if (line == null)
				throw new ElioRuntimeException("Invalid font file: " + fontFileName);
			String[] common = line.split(" ", 7); // we want the 6th element to
													// be in tact; i.e. "page=N"

			// we only really NEED lineHeight and base
			if (common.length < 3)
				throw new ElioRuntimeException("Invalid font file: " + fontFileName);

			if (!common[1].startsWith("lineHeight="))
				throw new ElioRuntimeException("Invalid font file: " + fontFileName);
			data.lineHeight = Integer.parseInt(common[1].substring(11));

			if (!common[2].startsWith("base="))
				throw new ElioRuntimeException("Invalid font file: " + fontFileName);
			float baseLine = Integer.parseInt(common[2].substring(5));

			// parse the pages count
			int imgPageCount = 1;
			if (common.length >= 6 && common[5] != null && common[5].startsWith("pages=")) {
				try {
					imgPageCount = Math.max(1, Integer.parseInt(common[5].substring(6)));
				} catch (NumberFormatException e) {
					// just ignore and only use one page...
					// somebody must have tampered with the page count >:(
				}
			}

			data.imageTextures = new Texture[imgPageCount];

			// read each page definition
			for (int p = 0; p < imgPageCount; p++) {
				// read each "page" info line
				line = reader.readLine();
				if (line == null)
					throw new ElioRuntimeException("Expected more 'page' definitions in font file " + fontFileName);
				String[] pageLine = line.split(" ", 4);
				if (!pageLine[2].startsWith("file="))
					throw new ElioRuntimeException("Invalid font file: " + fontFileName);

				// we will expect ID to mean "index" -- if for some reason this
				// is not the case, it will fuck everything up
				// so we need to warn the user that their BMFont output is bogus
				if (pageLine[1].startsWith("id=")) {
					try {
						int pageID = Integer.parseInt(pageLine[1].substring(3));
						if (pageID != p)
							throw new ElioRuntimeException("Invalid font file: " + fontFileName + " -- page ids must be indices starting at 0");
					} catch (NumberFormatException e) {
						throw new ElioRuntimeException("NumberFormatException on 'page id' element of " + fontFileName);
					}
				}

				String imgFilename = null;
				if (pageLine[2].endsWith("\"")) {
					imgFilename = pageLine[2].substring(6, pageLine[2].length() - 1);
				} else {
					imgFilename = pageLine[2].substring(5, pageLine[2].length());
				}

				String path = imgFilename;

				data.imageTextures[p] = TextureManager.instance().createTextureFromAssetsFile(context, path, textureOptions);
			}
			data.descent = 0;

			while (true) {
				line = reader.readLine();
				if (line == null)
					break; // EOF
				if (line.startsWith("chars ")) {
					StringTokenizer tokens = new StringTokenizer(line, " =");
					tokens.nextToken();
					tokens.nextToken();
					//int count = Integer.parseInt(tokens.nextToken());
					data.glyphs = new SparseArray<ArgonGlyph>();
					continue;
				}
				if (line.startsWith("kernings "))
					break; // Starting kernings block
				if (!line.startsWith("char "))
					continue;

				ArgonGlyph glyph = new ArgonGlyph();

				StringTokenizer tokens = new StringTokenizer(line, " =");
				tokens.nextToken();
				tokens.nextToken();
				int ch = Integer.parseInt(tokens.nextToken());
				if (ch <= Character.MAX_VALUE)
					data.setGlyph(ch, glyph);
				else
					continue;
				glyph.id = ch;
				tokens.nextToken();
				glyph.srcX = Integer.parseInt(tokens.nextToken());
				tokens.nextToken();
				glyph.srcY = Integer.parseInt(tokens.nextToken());
				tokens.nextToken();
				glyph.width = Integer.parseInt(tokens.nextToken());
				tokens.nextToken();
				glyph.height = Integer.parseInt(tokens.nextToken());
				tokens.nextToken();
				glyph.xoffset = Integer.parseInt(tokens.nextToken());
				tokens.nextToken();
				if (flip)
					glyph.yoffset = Integer.parseInt(tokens.nextToken());
				else
					glyph.yoffset = -(glyph.height + Integer.parseInt(tokens.nextToken()));
				tokens.nextToken();
				glyph.xadvance = Integer.parseInt(tokens.nextToken());

				// also check for page.. a little safer here since we don't want
				// to break any old functionality
				// and since maybe some shitty BMFont tools won't bother writing
				// page id??
				if (tokens.hasMoreTokens())
					tokens.nextToken();
				if (tokens.hasMoreTokens()) {
					try {
						glyph.page = Integer.parseInt(tokens.nextToken());
					} catch (NumberFormatException e) {
					}
				}

				if (glyph.width > 0 && glyph.height > 0)
					data.descent = Math.min(baseLine + glyph.yoffset, data.descent);
			}

			while (true) {
				line = reader.readLine();
				if (line == null)
					break;
				if (!line.startsWith("kerning "))
					break;

				StringTokenizer tokens = new StringTokenizer(line, " =");
				tokens.nextToken();
				tokens.nextToken();
				int first = Integer.parseInt(tokens.nextToken());
				tokens.nextToken();
				int second = Integer.parseInt(tokens.nextToken());
				if (first < 0 || first > Character.MAX_VALUE || second < 0 || second > Character.MAX_VALUE)
					continue;
				ArgonGlyph glyph = data.getGlyph((char) first);
				tokens.nextToken();
				int amount = Integer.parseInt(tokens.nextToken());
				if (glyph != null) { // it appears BMFont outputs kerning for
										// glyph pairs not contained in the
										// font, hence the null check
					glyph.setKerning(second, amount);
				}
			}

			ArgonGlyph spaceGlyph = data.getGlyph(' ');
			if (spaceGlyph == null) {
				spaceGlyph = new ArgonGlyph();
				spaceGlyph.id = (int) ' ';
				ArgonGlyph xadvanceGlyph = data.getGlyph('l');
				if (xadvanceGlyph == null)
					xadvanceGlyph = data.getFirstGlyph();
				spaceGlyph.xadvance = xadvanceGlyph.xadvance;
				data.setGlyph(' ', spaceGlyph);
			}
			data.spaceWidth = spaceGlyph != null ? spaceGlyph.xadvance + spaceGlyph.width : 1;

			ArgonGlyph xGlyph = null;
			for (int i = 0; i < xChars.length; i++) {
				xGlyph = data.getGlyph(xChars[i]);
				if (xGlyph != null)
					break;
			}
			if (xGlyph == null)
				xGlyph = data.getFirstGlyph();
			data.xHeight = xGlyph.height;

			ArgonGlyph capGlyph = null;
			for (int i = 0; i < capChars.length; i++) {
				capGlyph = data.getGlyph(capChars[i]);
				if (capGlyph != null)
					break;
			}
			if (capGlyph == null) {
				ArgonGlyph glyph;
				for (int i = 0; i < data.glyphs.size(); i++) {
					glyph = data.glyphs.valueAt(i);
					if (glyph == null || glyph.height == 0 || glyph.width == 0)
						continue;
					data.capHeight = Math.max(data.capHeight, glyph.height);
				}
			} else
				data.capHeight = capGlyph.height;

			data.ascent = baseLine - data.capHeight;
			data.down = -data.lineHeight;
			if (flip) {
				data.ascent = -data.ascent;
				data.down = -data.down;
			}
		} catch (Exception ex) {
			throw new ElioRuntimeException("Error loading font file: " + fontFileName, ex);
		} finally {
			StreamUtility.closeQuietly(reader);
		}

		return data;
	}

	private HashMap<String, ArgonBitmapFont> fonts;

}
