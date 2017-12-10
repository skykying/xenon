package com.abubusoft.xenon.android.listener;

import org.abubu.elio.logger.ElioLogger;
import org.abubu.elio.logger.ElioLoggerLevelType;

import android.view.MotionEvent;

/**
 * Gestore delle gesture
 * 
 * @author xcesco
 *
 */
public class ArgonGestureDefaultListenerImpl extends ArgonGestureListenerImpl {

	public boolean onDoubleTap(MotionEvent e) {
		if (ElioLogger.isEnabledFor(ElioLoggerLevelType.DEBUG)) {
			ElioLogger.debug("onDoubleTap " + e.toString());
		}

		return super.onDoubleTap(e);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		if (ElioLogger.isEnabledFor(ElioLoggerLevelType.DEBUG)) {
			ElioLogger.debug("onDown " + e.toString());
		}

		return super.onDown(e);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if (ElioLogger.isEnabledFor(ElioLoggerLevelType.DEBUG)) {
			ElioLogger.debug("onFling " + e2.toString());
		}

		return super.onFling(e1, e2, velocityX, velocityY);
	}

	@Override
	public void onLongPress(MotionEvent e) {
		if (ElioLogger.isEnabledFor(ElioLoggerLevelType.DEBUG)) {
			ElioLogger.debug("onLongPress" + e.toString());
		}
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		if (ElioLogger.isEnabledFor(ElioLoggerLevelType.DEBUG)) {
			ElioLogger.debug("onScroll " + e2.toString());
		}

		return super.onScroll(e1, e2, distanceX, distanceY);
	}

	@Override
	public void onShowPress(MotionEvent e) {
		if (ElioLogger.isEnabledFor(ElioLoggerLevelType.DEBUG)) {
			ElioLogger.debug("onShowPress" + e.toString());
		}
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		if (ElioLogger.isEnabledFor(ElioLoggerLevelType.DEBUG)) {
			ElioLogger.debug("onSingleTapUp" + e.toString());
		}

		return super.onSingleTapUp(e);
	}
}
