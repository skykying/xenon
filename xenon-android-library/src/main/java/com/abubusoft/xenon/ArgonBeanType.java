package com.abubusoft.xenon;

import com.abubusoft.xenon.settings.ArgonSettings;

/**
 * <p>
 * Sono i valori che possono essere trasmessi mediante reflection
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public enum ArgonBeanType {

	/**
	 * application
	 */
	APPLICATION,

	/**
	 * 
	 */
	ARGON,

	/**
	 * <p>
	 * Impostazioni caricate da file xml. il bean è di tipo
	 * {@link ArgonSettings}
	 * </p>
	 * 
	 */
	ARGON_SETTINGS,

	/**
	 * android context (application context)
	 */
	CONTEXT,

	/**
	 * configurazione dell'applicazione. Deve derivare da ConfigBase
	 */
	CONFIG;

	/**
	 * istanza di classe da associare
	 */
	public Object value;
}
