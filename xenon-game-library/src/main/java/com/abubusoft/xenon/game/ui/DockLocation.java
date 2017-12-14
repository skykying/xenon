package com.abubusoft.xenon.game.ui;

/**
 * 
 * 
 * @author Francesco Benincasa
 * 
 */
public class DockLocation {

	/**
	 * 
	 */
	public DockHorizontal dockX;

	/**
	 * 
	 */
	public DockVertical dockY;

	/**
	 * 
	 */
	public float valueX;

	/**
	 * 
	 */
	public float valueY;

	/**
	 * 
	 */
	public DockLocation() {

	}

	/**
	 * @param horizontalValue
	 * @param verticalValue
	 * @return
	 */
	public static DockLocation build(DockHorizontal horizontalValue, DockVertical verticalValue) {
		DockLocation ta = new DockLocation();
		ta.set(horizontalValue, verticalValue);

		return ta;
	}

	/**
	 * @param horizontalValue
	 * @param verticalValue
	 */
	public void set(DockHorizontal horizontalValue, DockVertical verticalValue) {
		dockX = horizontalValue;
		dockY = verticalValue;
	}

	/**
	 * Imposta la posizione in termini assoluti
	 * 
	 * @param valueX
	 * @param valueY
	 */
	public void setLocation(int xValue, int yValue) {
		dockX = DockHorizontal.ABSOLUTE;
		dockY = DockVertical.ABSOLUTE;
		valueX = xValue;
		valueY = yValue;
	}

}
