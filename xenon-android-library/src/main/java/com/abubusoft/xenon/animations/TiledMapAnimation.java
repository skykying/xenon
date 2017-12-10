package com.abubusoft.xenon.animations;

import com.abubusoft.kripton.annotation.BindType;

@BindType(typeVariables={"K0,K","K1"})
public class TiledMapAnimation extends Parallel2Animation<TranslationFrame, TextureKeyFrame> {

	/**
	 * Consente con un unico metodo di definire l'animazione in termini di texture e di spostamento da eseguire.
	 * 
	 * Il tempo viene definito dall'animazione.
	 * 
	 * @param x
	 * @param y
	 * @param animation
	 */
	public void setAnimation(float x, float y, String animation)
	{
		TextureAnimation animation1=TextureAnimationManager.instance().getAnimation(animation);
		
		Translation animation0=new Translation();
		animation0.setInterval(TranslationFrame.build(animation1.duration()), TranslationFrame.build(x, y, 0, 0));
		
		setAnimation(animation0);
		setAnimation1(animation1);
	}
	
}
