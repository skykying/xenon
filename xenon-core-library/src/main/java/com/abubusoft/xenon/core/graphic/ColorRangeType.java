package com.abubusoft.xenon.core.graphic;

import static com.abubusoft.xenon.core.graphic.ColorRangeMasks.*;

public enum ColorRangeType {

	/**
	 * Range solo per alpha
	 */
	ALPHA(MASK_ALPHA),
	/**
	 * Cambiano tutti i componenti ARGB
	 */
	RGB(MASK_RED | MASK_BLUE | MASK_GREEN | MASK_ALPHA),
	/**
	 * Cambia solo il componente RGB RED
	 */
	RGB_RED(MASK_RED),
	/**
	 * Cambia il componente RGB green
	 */
	RGB_GREEN(MASK_GREEN),
	/**
	 * Cambia il componente RGB blue
	 */
	RGB_BLUE(MASK_BLUE),
	/**
	 * Cambia le componenti HSV
	 */
	HSV(MASK_ALPHA | MASK_HUE | MASK_SAT | MASK_VALUE),
	/**
	 * Cambia la componente HUE HSV
	 */
	HSV_HUE(MASK_HUE),
	/**
	 * Cambia la componente SAT
	 */
	HSV_SAT(MASK_SAT),
	/**
	 * Cambia la componente value HSV
	 */
	HSV_VALUE(MASK_VALUE);

	/**
	 * mask
	 */
	public final int mask;

	/**
	 * @param maskValue
	 */
	private ColorRangeType(int maskValue) {
		mask = maskValue;
	}

	/**
	 * @return
	 */
	public boolean isWorkingOnRGB() {
		return (mask & (MASK_RED | MASK_GREEN | MASK_BLUE)) > 0;
	}

	/**
	 * @return
	 */
	public boolean isWorkingOnHSV() {
		return (mask & (MASK_HUE | MASK_SAT | MASK_VALUE)) > 0;
	}

	/**
	 * @return
	 */
	public boolean isWorkingOnAlphaChannel() {
		return (mask & (MASK_ALPHA)) > 0;
	}

	/**
	 * Verifica se è abilitato una determinata variazione
	 * 
	 * @param maskValue
	 * @return
	 */
	public boolean isEnable(int maskValue) {
		return (mask & (maskValue)) > 0;
	}
}
