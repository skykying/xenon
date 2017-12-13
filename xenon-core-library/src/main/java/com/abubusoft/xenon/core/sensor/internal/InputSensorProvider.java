package com.abubusoft.xenon.core.sensor.internal;

import java.util.HashSet;

import android.hardware.SensorEventListener;

/**
 * Marcatore per le classi detector agganciate ai sensori.
 * 
 * @author Francesco Benincasa
 *
 */
public interface InputSensorProvider extends SensorEventListener {
	/**
	 * Recupera il set di sensori agganciati al detector
	 * @return
	 * 		elenco dei sensori collegati al detector
	 */
	HashSet<Integer> getAttachedTypeofSensors();
}
