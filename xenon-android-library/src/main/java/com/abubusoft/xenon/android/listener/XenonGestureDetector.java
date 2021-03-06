/**
 *
 */
package com.abubusoft.xenon.android.listener;


import com.abubusoft.kripton.android.Logger;
import com.abubusoft.xenon.Xenon4OpenGL;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * @author Francesco Benincasa
 */
public class XenonGestureDetector {

    /**
     * gestore delle gesture
     */
    public GestureDetector gestureDetector;

    /**
     * gestore dello scale
     */
    public ScaleGestureDetector scaleGestureDetector;

    public XenonGestureListener listener;

    Xenon4OpenGL xenon4OpenGL;


    public XenonGestureDetector(Xenon4OpenGL xenonGL, XenonGestureListener gestureListener) {
        this.xenon4OpenGL = xenonGL;
        gestureDetector = new GestureDetector(xenonGL.context(), gestureListener);
        scaleGestureDetector = new ScaleGestureDetector(xenonGL.context(), gestureListener);
        listener = gestureListener;
    }


    /**
     * <p>
     * Evento associato al touch
     * </p>
     *
     * @param event evento in input
     * @return true se l'evento è stato consumato.
     */
    public boolean onTouchEvent(MotionEvent event) {
        if (!xenon4OpenGL.isSceneReady()) {
            Logger.debug("Scene is not ready, skip touch event");
            return false;
        }

        final int action = event.getActionMasked();
        // evento up
        if (action == MotionEvent.ACTION_UP) {
            listener.onUp(event);
        }

        // come da
        // http://stackoverflow.com/questions/15309743/use-scalegesturedetector-with-gesturedetector
        boolean result = scaleGestureDetector.onTouchEvent(event);
        // result is always true here, so I need another way to check for a
        // detected scaling gesture
        boolean isScaling = result = scaleGestureDetector.isInProgress();
        if (!isScaling) {
            // if no scaling is performed check for other gestures (fling, long
            // tab, etc.)
            result = gestureDetector.onTouchEvent(event);
        }
        return result;
    }
}
