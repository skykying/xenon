package com.abubusoft.xenon.android.listener;

import com.abubusoft.kripton.android.Logger;
import android.view.MotionEvent;

/**
 * Gestore delle gesture
 *
 * @author xcesco
 */
public class ArgonGestureDefaultListenerImpl extends ArgonGestureListenerImpl {

    public boolean onDoubleTap(MotionEvent e) {
        Logger.debug("onDoubleTap " + e.toString());

        return super.onDoubleTap(e);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Logger.debug("onDown " + e.toString());

        return super.onDown(e);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Logger.debug("onFling " + e2.toString());

        return super.onFling(e1, e2, velocityX, velocityY);
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Logger.debug("onLongPress" + e.toString());

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Logger.debug("onScroll " + e2.toString());

        return super.onScroll(e1, e2, distanceX, distanceY);
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Logger.debug("onShowPress" + e.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Logger.debug("onSingleTapUp" + e.toString());

        return super.onSingleTapUp(e);
    }
}
