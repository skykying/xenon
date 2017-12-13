package com.abubusoft.xenon.core.sensor;

import android.hardware.SensorManager;

/**
 * Tipo di delay nell'acquisizione dei sensori
 * 
 * @author Francesco Benincasa
 *
 */
public enum ElioSensorDelayType {
	
	/**
	 * incapsula delay molto frequente, per i giochi 
	 */
	DELAY_GAME(SensorManager.SENSOR_DELAY_GAME),  
	/**
	 * incapsula delay più basso, da UI
	 */
	DELAY_UI(SensorManager.SENSOR_DELAY_UI);

	/**
	 * valore incapsulato
	 */
	public final int value;

	private ElioSensorDelayType(int newValue) {
		value = newValue;
	}
}
