package com.abubusoft.xenon.core.sensor;

import com.abubusoft.xenon.core.sensor.internal.InputListener;

/**
 * @author Francesco Benincasa
 *
 */
public interface ShakeInputListener extends InputListener {

	/**
	 * generato quando il device viene shakerato
	 */
	void onShake();

}
