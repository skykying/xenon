package com.abubusoft.xenon.game;

import com.abubusoft.xenon.ArgonBean;
import com.abubusoft.xenon.ArgonBeanInject;
import com.abubusoft.xenon.ArgonBeanType;
import com.abubusoft.xenon.android.listener.ArgonGestureListenerImpl;

import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * <p>
 * Listener di base per game.
 * </p>
 * 
 * @author Francesco Benincasa
 */
@ArgonBean
public class GameGestureListener extends ArgonGestureListenerImpl {
	
	@ArgonBeanInject(ArgonBeanType.APPLICATION)
	public GameApplicationImpl application;

	@Override
	public boolean onDown(MotionEvent e) {
		return application.stateMachine.currentState.gesturesListener.onDown(e);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return application.stateMachine.currentState.gesturesListener.onFling(e1, e2, velocityX, velocityY);
	}

	@Override
	public void onLongPress(MotionEvent e) {
		application.stateMachine.currentState.gesturesListener.onLongPress(e);
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return application.stateMachine.currentState.gesturesListener.onScroll(e1, e2, distanceX, distanceY);
	}

	@Override
	public void onShowPress(MotionEvent e) {
		application.stateMachine.currentState.gesturesListener.onShowPress(e);
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return application.stateMachine.currentState.gesturesListener.onSingleTapUp(e);
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		return application.stateMachine.currentState.gesturesListener.onScale(detector);
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return application.stateMachine.currentState.gesturesListener.onScaleBegin(detector);
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		application.stateMachine.currentState.gesturesListener.onScaleEnd(detector);
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		return application.stateMachine.currentState.gesturesListener.onSingleTapConfirmed(e);
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		return application.stateMachine.currentState.gesturesListener.onDoubleTap(e);
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return application.stateMachine.currentState.gesturesListener.onDoubleTapEvent(e);
	}
	
	
}
