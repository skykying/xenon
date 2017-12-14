/**
 * 
 */
package com.abubusoft.xenon.game.ui;

/**
 * <p></p>
 * 
 * @author Francesco Benincasa
 * 
 */
public interface UIViewOnClickListener {

	/**
	 * <p>
	 * Se true indica che l'evento è stato consumato e non deve essere verificato il fatto che altri componenti siano stati selezionati.
	 * </p>
	 * 
	 * @param component
	 * @return
	 */
	boolean onClick(UIViewComponent component);
}
