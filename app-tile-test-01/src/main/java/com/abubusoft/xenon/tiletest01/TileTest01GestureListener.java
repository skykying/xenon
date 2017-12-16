package com.abubusoft.xenon.tiletest01;

import java.util.Random;

import android.view.MotionEvent;

import com.abubusoft.xenon.XenonBean;
import com.abubusoft.xenon.android.listener.XenonGestureListenerImpl;
import com.abubusoft.xenon.context.XenonBeanInject;
import com.abubusoft.xenon.context.XenonBeanType;
import com.abubusoft.xenon.engine.TouchManager;
import com.abubusoft.xenon.engine.TouchType;

/**
 * @author Francesco Benincasa
 */
@XenonBean
public class TileTest01GestureListener extends XenonGestureListenerImpl {

    @XenonBeanInject(XenonBeanType.APPLICATION)
    public TileTest01Application application;

    Random rnd = new Random();


    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        TouchManager.instance().sendMessage(TouchType.SCROLL, distanceX, distanceY);

        return true;
    }

	/* (non-Javadoc)
     * @see org.abubu.argon.android.listener.ArgonGestureListenerImpl#onFling(android.view.MotionEvent, android.view.MotionEvent, float, float)
	 */
	/*@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		float distanceX=e2.getX()-e1.getX();
		float distanceY=e2.getY()-e1.getY();
		
		if (e1.getY() > ArgonGL.screenInfo.height * 0.5f) {
			// movimento player
			if (!application.path.isRunning()) {
				application.objPlayerController.moveFromScreen(distanceX, distanceY);
				application.mapController.position(application.objPlayerController.obj.x, application.objPlayerController.obj.y, TiledMapPositionType.MIDDLE_CENTER);
			}
		} else {
			// movimento tile
			//application.mapController.scrollFromScreen(distanceX, distanceY);
		}
		
		return true;
	}*/

    @Override
    public void onLongPress(MotionEvent e) {
        TouchManager.instance().sendMessage(TouchType.LONG_PRESS, e.getX(), e.getY());

    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        TouchManager.instance().sendMessage(TouchType.DOUBLE_TAP, e.getX(), e.getY());

        return true;
    }

    @Override
    public void onUp(MotionEvent e) {
        //application.mapController.touch(e.getRawX(), e.getRawY());
		/*if (!application.path.isRunning()) {
			application.objPlayerController.moveStop();
			
			
		}*/
        TouchManager.instance().sendMessage(TouchType.UP, e.getX(), e.getY());
    }

}
