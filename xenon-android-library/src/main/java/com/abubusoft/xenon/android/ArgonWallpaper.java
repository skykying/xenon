package com.abubusoft.xenon.android;

import com.abubusoft.xenon.android.listener.ArgonGestureDetector;
import com.abubusoft.xenon.opengl.ArgonGLRenderer;
import com.abubusoft.xenon.opengl.ArgonGLRendererBuilder;

import android.content.Context;

/**
 * Interfaccia che i vari wallpaper engine devono implementare per poter essere usati
 * in argon.
 * 
 * @author Francesco Benincasa
 *
 */
public interface ArgonWallpaper extends ArgonGLRendererBuilder {
	
	/**
	 * Imposta il renderer
	 * 
	 * @param renderer
	 * 		rendere da utilizzare
	 */
	void setRenderer(ArgonGLRenderer renderer);

	/**
	 * Fornisce il context dell'applicazione
	 * 
	 * @return
	 * 		context dell'applicazione
	 */
	Context getApplicationContext();
	
	/**
	 * Imposta il gesture detector
	 * 
	 * @param gestureDetectorValue
	 */
	void setGestureDetector(ArgonGestureDetector gestureDetectorValue);
	
	/**
	 * Evento invocato quando l'applicazione si sta chiudendo
	 */
	void onDestroy();
	
}
