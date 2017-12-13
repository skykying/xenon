package com.abubusoft.xenon.core.util;

import android.os.SystemClock;

/**
 * Classe di utilità per monitorare il tempo
 * @author Francesco Benincasa
 *
 */
public class Timer {
	
	/**
	 * tempo iniziale
	 */
	private long time;

	/**
	 * inizio timer 
	 */
	public void start()
	{
		time=now();
	}
	
	/**
	 * fine 
	 * @return
	 */
	public long end()
	{
		return now()-time;
	}
	
	/**
	 * tempo attuale in millisecondi.
	 * @return
	 */
	public static long now()
	{
		return SystemClock.elapsedRealtime();
	}
}
