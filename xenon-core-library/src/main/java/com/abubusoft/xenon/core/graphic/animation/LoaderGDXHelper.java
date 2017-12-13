package com.abubusoft.xenon.core.graphic.animation;

import java.util.HashMap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;


/**
 * Analizza la definizione di uno sprite
 * 
 * @author Francesco Benincasa
 * 
 */
public class LoaderGDXHelper {

	private final static int SPRITE_NAME_INDEX = 0;
	private final static int SPRITE_XY_INDEX = 2;
	private final static int SPRITE_SIZE_INDEX = 3;

	/**
	 * <p>Data una bitmap atlas ed un set di definizione di sprite, crea le sottobitmap
	 * che vengono poi referenziate come sprite, mediante il nome</p>
	 * 
	 * @param input
	 * @param source
	 * @return
	 */
	public static HashMap<String, Bitmap> createTiles(String input, Bitmap source) {

		HashMap<String, Bitmap> map = new HashMap<String, Bitmap>();
		Bitmap tile = null;
		
		String[] definition = input.split("\n");
		String[] temp;
		String name;
		int xy1, xy2;
		int size1, size2;

		for (int i = 4; i < definition.length; i += 7) {
			name = definition[i + SPRITE_NAME_INDEX].trim();
			temp = definition[i + SPRITE_XY_INDEX].replace("xy:", "").trim().split(",");
			xy1 = Integer.valueOf(temp[0].trim());
			xy2 = Integer.valueOf(temp[1].trim());

			temp = definition[i + SPRITE_SIZE_INDEX].replace("size:", "").trim().split(",");
			size1 = Integer.valueOf(temp[0].trim());
			size2 = Integer.valueOf(temp[1].trim());

			tile = Bitmap.createBitmap(source, xy1, xy2, size1, size2);

			map.put(name, tile);
		}

		return map;
	}

	public static HashMap<String, BitmapAnimation> createAnimations(Resources resources, String input, HashMap<String, Bitmap> tilesMap) {

		HashMap<String, BitmapAnimation> map = new HashMap<String, BitmapAnimation>();
		String name=null;
		BitmapAnimation animation=null;
		String[] definition = input.split("\n");
		String row;
		String[] temp;

		for (int i = 0; i < definition.length; i++) {
			row = definition[i].trim();
			if ("{".equals(row)) {
				animation=new BitmapAnimation();
				animation.name=name;
			} else if ("}".equals(row)) {
				map.put(name, animation);
			} else if (row.startsWith("looping:")) {
				row=row.replace("looping:", "").trim();
				
				animation.frames.setOneShot(true);
				if ("true".equalsIgnoreCase(row)) animation.frames.setOneShot(false);
					
			} else if (row.startsWith("frame:")) {
				row=row.replace("frame:", "").trim();
				temp=row.split(",");
				
				animation.frames.addFrame(new BitmapDrawable(resources, tilesMap.get(temp[0].trim().toLowerCase())), Integer.parseInt(temp[3].trim()));
			}else {
				name = definition[i].trim().toLowerCase();
			}
		}

		return map;
	}

}
