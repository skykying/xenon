package com.abubusoft.xenon;

import com.abubusoft.xenon.settings.ArgonSettings;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * <p>Serve a marcare i modi con i quali l'applicazione può essere lanciata.</p>
 * 
 * @author Francesco Benincasa
 * 
 */
public interface Argon {

	/**
	 * <p>Da avviare in fase di avvio dell'applicazione</p>
	 * 
	 * @param contextValue
	 * @param settingsValue
	 * @param preferenceValue
	 */
	void onStartup(Context contextValue, ArgonSettings settingsValue, SharedPreferences preferenceValue);
	

	/**
	 * <p>Consente all'applicazione di avvertire il reset della configurazione</p>
	 */
	void onConfigReset();
	
	/**
	 * <p>Recupera l'application context</p>
	 * 
	 * @return
	 */
	Context getContext();
}
