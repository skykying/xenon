package com.abubusoft.xenon.android.listener;

import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * <p>Gestore delle gesture.</p>
 * 
 * @author Francesco Benincasa
 *
 */
public class ArgonGestureListenerImpl implements ArgonGestureListener {
	
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	/* (non-Javadoc)
	 * @see android.view.ScaleGestureDetector.OnScaleGestureListener#onScale(android.view.ScaleGestureDetector)
	 */
	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		return false;
	}

	/* (non-Javadoc)
	 * @see android.view.ScaleGestureDetector.OnScaleGestureListener#onScaleBegin(android.view.ScaleGestureDetector)
	 */
	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		// deve tornare true per iniziare a considerare l'eventuale zoom
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		// lasciato vuoto
		
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.abubu.argon.android.listener.ArgonGestureListener#onUp(android.view.MotionEvent)
	 */
	@Override
	public void onUp(MotionEvent e) {
		
	}


}
