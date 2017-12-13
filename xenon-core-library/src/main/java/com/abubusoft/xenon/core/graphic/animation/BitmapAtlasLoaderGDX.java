package com.abubusoft.xenon.core.graphic.animation;

import java.util.HashMap;

import com.abubusoft.xenon.core.graphic.BitmapUtility;
import com.abubusoft.xenon.core.logger.ElioLogger;
import com.abubusoft.xenon.core.util.IOUtility;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Factory degli sprite, animati e non.
 * 
 * @author Francesco Benincasa
 * 
 */
public class BitmapAtlasLoaderGDX {

	public static HashMap<String, BitmapAnimation> createBitmapAtlas(Context context, int spriteDefinitionResourceId, int animationDefinitionResourceId, int imageResourceId) {
		try {
			Bitmap source=BitmapUtility.loadImageFromResource(context, imageResourceId);
			
			String spriteDefinition=IOUtility.readRawTextFile(context, spriteDefinitionResourceId);
			HashMap<String, Bitmap> tilesMap=LoaderGDXHelper.createTiles(spriteDefinition, source);
			
			String animationDefinition=IOUtility.readRawTextFile(context, animationDefinitionResourceId);
			HashMap<String, BitmapAnimation> animationMap=LoaderGDXHelper.createAnimations(context.getResources(), animationDefinition, tilesMap);
									
			source.recycle();

			return animationMap;
		} catch (Exception e) {
			ElioLogger.fatal(e.getMessage());
			e.printStackTrace();
			return null;
		}

	}
	
}
