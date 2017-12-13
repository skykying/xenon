package com.abubusoft.xenon.core.graphic.animation;

import android.graphics.drawable.AnimationDrawable;

/**
 * Animazione di uno sprite sottorma di animationdrawable. Serve a gestire le animazioni.
 * 
 * @author Francesco Benincasa
 * 
 */
public class BitmapAnimation {
	/**
	 * nome dell'animazione
	 */
	public String name;
	
	/**
	 * animazioni
	 */
	public AnimationDrawable frames;
	
	/**
	 * 
	 */
	public BitmapAnimation() {
		frames = new AnimationDrawable();			
	}
	
}
