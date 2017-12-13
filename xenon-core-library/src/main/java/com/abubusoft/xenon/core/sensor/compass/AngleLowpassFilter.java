package com.abubusoft.xenon.core.sensor.compass;

import java.util.ArrayDeque;

/**
 * Gestore per la media di una serie di angoli espressi in forma di radianti.
 * Questo usa la trigonetria per gestire ad esempio quei casi in cui si oltrepassa il limite:
 * ad esempio 360 + 40 non fa 200 di media. Con la trigonometria si oltrepassa questo limite.
 * 
 * @author xcesco
 *
 */
public class AngleLowpassFilter {

	/**
	 * numero di valori con cui elaborare la media
	 */
	private final int LENGTH = 10;

	private float sumSin, sumCos;

	/**
	 * coda di valori
	 */
	private ArrayDeque<Float> queue = new ArrayDeque<Float>();

	/**
	 * aggiunge un valore alla media
	 * 
	 * @param radians
	 */
	public void add(float radians) {
		sumSin += (float) Math.sin(radians);
		sumCos += (float) Math.cos(radians);
		queue.add(radians);

		if (queue.size() > LENGTH) {
			float old = queue.poll();
			sumSin -= Math.sin(old);
			sumCos -= Math.cos(old);
		}
	}

	/**
	 * calcola la media dei valori calcolati finora
	 * 
	 * @return
	 * 		media
	 */
	public float average() {
		int size = queue.size();
		return (float) Math.atan2(sumSin / size, sumCos / size);
	}
	
	/**
	 * aggiunge un valore e restituisce la media
	 * 
	 * @param radians
	 * 		nuovo valore da sommare alla media
	 * @return
	 * 		media
	 */
	public float average(float radians) {
		add(radians);
		return average();
	}
}