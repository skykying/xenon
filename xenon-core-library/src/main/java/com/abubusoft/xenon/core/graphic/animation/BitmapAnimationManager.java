/**
 * 
 */
package com.abubusoft.xenon.core.graphic.animation;

import java.util.HashMap;
import java.util.HashSet;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;

/**
 * <p>Gestore delle animazioni bitmap. </p>
 *  
 * @author Francesco Benincasa
 * 
 */
public class BitmapAnimationManager {

	/**
	 * instanza singleton
	 */
	private final static BitmapAnimationManager instance = new BitmapAnimationManager();

	/**
	 * pattern singleton
	 * 
	 * @return
	 */
	public static final BitmapAnimationManager getInstance() {
		return instance;
	}

	/**
	 * mappa degli animator
	 */
	private HashMap<String, BitmapAnimation> animationMap;
	
	/**
	 * insieme delle animazioni già definite
	 */
	private HashSet<Integer> animationId;

	private BitmapAnimationManager() {
		animationMap = new HashMap<String, BitmapAnimation>();
		animationId=new HashSet<Integer>();
	}

	public void clear() {
		for (BitmapAnimation item : animationMap.values()) {
			item.frames = null;
		}
		animationMap.clear();
	}


	/**
	 * <p>Crea un insieme di animazioni drawable a partire da un insieme di image atlas in formato gdx.
	 * 
	 * @param context
	 * @param spriteDefinitionResourceId
	 * @param animationDefinitionResourceId
	 * @param imageResourceId
	 * @return
	 */
	public int createAnimationFromGDX(Context context, int spriteDefinitionResourceId, int animationDefinitionResourceId, int imageResourceId) {
		// se l'animationId è stato già definito, allora usciamo senza fare niente.
		if (animationId.contains(animationDefinitionResourceId)) return 0;
		animationId.add(animationDefinitionResourceId);
		
		HashMap<String, BitmapAnimation> atlas = BitmapAtlasLoaderGDX.createBitmapAtlas(context, spriteDefinitionResourceId, animationDefinitionResourceId, imageResourceId);

		// creiamo associazione tra animazione ed atlas
		for (BitmapAnimation item : atlas.values()) {
			
			if (animationMap.containsKey(item.name))
			{
				// lo puliamo per sicurezza
				animationMap.get(item.name).frames=null;
			}
			animationMap.put(item.name, item);
		}
		
		return atlas.size();
	}

	/**
	 * Recupera l'animazione da tutti gli atlas partendo dal nome
	 * dell'animazione
	 * 
	 * @param animationName
	 * @return
	 */
	public AnimationDrawable getAnimation(String animationName) {
		return createCopy(animationMap.get(animationName).frames);
	}
	
	/**
	 * Crea una copia dell'animazione
	 * @param src
	 * @return
	 */
	public AnimationDrawable createCopy(AnimationDrawable src)
	{
		AnimationDrawable dest=new AnimationDrawable();
		
		dest.setOneShot(src.isOneShot());
		for (int i=0; i<src.getNumberOfFrames();i++)
		{
			dest.addFrame(src.getFrame(i), src.getDuration(i));
		}
		
		return dest;
	}

}
