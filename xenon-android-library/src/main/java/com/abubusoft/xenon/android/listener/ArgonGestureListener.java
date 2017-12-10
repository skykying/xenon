package com.abubusoft.xenon.android.listener;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.abubusoft.xenon.Uncryptable;

/**
 * Interfaccia per la gestione delle gesture
 * 
 * @author Francesco Benincasa
 * 
 */
public interface ArgonGestureListener extends GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener, Uncryptable {

	/**
	 * <p>
	 * Eseguito quando viene rilasciato. Non restituisce nulla, dato che è trasparente rispetto a tutti gli altri eventi.
	 * </p>
	 * 
	 * @param e
	 */
	public void onUp(MotionEvent e);

}
