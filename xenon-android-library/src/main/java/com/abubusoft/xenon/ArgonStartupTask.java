/**
 * 
 */
package com.abubusoft.xenon;

import com.abubusoft.xenon.android.ArgonActivitySplash;

/**
 * <p>
 * Contiene il task da eseguire durante la visualizzazione dello splash screen.
 * </p>
 * 
 * @author Cesco
 * 
 */
public interface ArgonStartupTask {

	/**
	 * <p>
	 * effettua il task.
	 * </p>
	 * 
	 * @param splashActivity
	 */
	void doTask(ArgonActivitySplash splashActivity);

}
